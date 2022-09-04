/*
 * Copyright © 2016 Oleg Cherednik
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package cop.maven.plugins;

import cop.maven.plugins.mocks.ArtifactMock;
import cop.maven.plugins.mocks.FileMock;
import cop.maven.plugins.mocks.JavaCompilerMock;
import cop.maven.plugins.mocks.LogMock;
import cop.maven.plugins.mocks.MavenProjectMock;
import cop.maven.plugins.mocks.MavenSessionMock;
import cop.maven.plugins.mocks.MojoExecutionMock;
import cop.raml.TestUtils;
import cop.raml.processor.RestProcessor;
import cop.raml.utils.ReflectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.tools.JavaCompiler;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Oleg Cherednik
 * @since 16.02.2017
 */
@SuppressWarnings({ "serial", "InstanceMethodNamingConvention" })
public class AbstractRestToRamlMojoTest {
    private RamlMojoMock mojo;

    @BeforeMethod(groups = "init")
    public void init() throws Exception {
        mojo = new RamlMojoMock();
    }

    @Test
    public void checkConstants() throws Exception {
        assertThat(getDefFileName()).isEqualTo("${project.artifactId}_${project.version}");
    }

    @Test(groups = "init")
    public void testGetSourceFiles() throws Exception {
        File dir = TestUtils.createTempDir();
        File dirService = TestUtils.createTempDir(dir, "service");
        File dirController = TestUtils.createTempDir(dir, "controller");

        TestUtils.createTempFile(dirService, "one.java");
        TestUtils.createTempFile(dirService, "two.java");
        TestUtils.createTempFile(dirService, "aaa.txt");
        TestUtils.createTempFile(dirController, "three.java");
        TestUtils.createTempFile(dirController, "four.java");
        TestUtils.createTempFile(dirController, "bbb.txt");

        sourceDirectories(mojo, new LinkedHashSet<File>() {{
            add(dirService);
            add(dirController);
        }});

        List<String> files = getSourceFiles(mojo).stream()
                                                 .map(File::getName)
                                                 .collect(Collectors.toList());
        assertThat(files).hasSize(4);
        assertThat(files).containsOnly("one.java", "two.java", "three.java", "four.java");
    }

    @Test(groups = { "init", "buildCompileClasspath" })
    public void shouldIgnoreEmptyPluginArtifactWhenBuildClasspath() throws Exception {
        setClasspathElements(mojo, Arrays.asList("aaa", "bbb"));
        assertThat(buildCompileClasspath(mojo)).isEqualTo("aaa;bbb");
    }

    @Test(groups = "buildCompileClasspath")
    public void shouldAddPluginArtifactWhenBuildClasspath() throws Exception {
        setClasspathElements(mojo, Arrays.asList("aaa", "bbb"));

        pluginArtifacts(mojo, Arrays.asList(
                new ArtifactMock(Artifact.SCOPE_COMPILE, FileMock.createFile("ccc")),
                new ArtifactMock(Artifact.SCOPE_COMPILE, null),
                new ArtifactMock(Artifact.SCOPE_RUNTIME, FileMock.createFile("ddd")),
                new ArtifactMock(Artifact.SCOPE_RUNTIME, null),
                new ArtifactMock(Artifact.SCOPE_PROVIDED, FileMock.createFile("eee")),
                new ArtifactMock(Artifact.SCOPE_PROVIDED, null)
        ));

        assertThat(buildCompileClasspath(mojo)).isEqualTo("ccc;ddd;aaa;bbb");
    }

    @Test(groups = "init")
    public void testGetCompileOptions() throws Exception {
        setClasspathElements(mojo, Arrays.asList("aaa", "bbb"));
        fileName(mojo, "ccc");
        mojo.out = "ddd";

        assertThat(getCompilerOptions(mojo)).containsExactly("-cp", "aaa;bbb", "-proc:only", "-processor", RestProcessor.class.getName(),
                "-d", "ddd", "-AfileName=ccc");
    }

    @Test(groups = "checkOutputDirectoryExists")
    public void shouldUpdateOutOnceWhenMultipleCheckOutput() throws Exception {
        File dir = TestUtils.createTempDir();

        Build build = new Build();
        build.setDirectory(dir.getAbsolutePath());
        mojo.project.setBuild(build);

        mojo.out = "sub";
        checkOutputDirectoryExists(mojo);
        assertThat(mojo.out).isEqualTo(FilenameUtils.concat(dir.getAbsolutePath(), "sub"));

        checkOutputDirectoryExists(mojo);
        assertThat(mojo.out).isEqualTo(FilenameUtils.concat(dir.getAbsolutePath(), "sub"));
    }

    @Test(groups = { "init", "checkOutputDirectoryExists" })
    public void shouldSetDefaultOutputWhenOutputIsNotSet() throws Exception {
        File dir = TestUtils.createTempDir();

        Build build = new Build();
        build.setDirectory(dir.getAbsolutePath());
        mojo.project.setBuild(build);

        checkOutputDirectoryExists(mojo);
        assertThat(mojo.out).isEqualTo(FilenameUtils.concat(dir.getAbsolutePath(), "docs"));
    }

    @Test(groups = { "init", "checkOutputDirectoryExists" })
    public void shouldPrintMessageWhenOutputDirectoryWasCreated() throws Exception {
        File dir = TestUtils.createTempDir();
        mojo.out = "sub";

        Build build = new Build();
        build.setDirectory(dir.getAbsolutePath());
        mojo.project.setBuild(build);

        mojo.getLog().debug((String)null);
        checkOutputDirectoryExists(mojo);
        assertThat(((LogMock)mojo.getLog()).getDebugContent()).isNotEmpty();

        mojo.getLog().debug((String)null);
        checkOutputDirectoryExists(mojo);
        assertThat(((LogMock)mojo.getLog()).getDebugContent()).isNull();
    }

    @Test(groups = "init")
    public void testCheckFileName() throws Exception {
        assertThat(fileName(mojo)).isNull();

        checkFileName(mojo);
        assertThat(fileName(mojo)).isEqualTo(getDefFileName());

        fileName(mojo, "aaa");
        checkFileName(mojo);
        assertThat(fileName(mojo)).isEqualTo("aaa");

        mojo.setEvaluate("", "aaa");
        checkFileName(mojo);
        assertThat(fileName(mojo)).isEqualTo(getDefFileName());

        mojo.setEvaluate("", "aaa", getDefFileName());
        checkFileName(mojo);
        assertThat(fileName(mojo)).isEqualTo(RestProcessor.DEF_FILE_NAME);
    }

    @Test
    public void testEvaluate() throws Exception {
        AbstractRestToRamlMojoMock obj = new AbstractRestToRamlMojoMock();
        setEvaluator(obj, new PluginParameterExpressionEvaluator(new MavenSessionMock()));

        assertThat(obj.evaluate("aaa")).isEqualTo("aaa");
        assertThat(obj.evaluate("   aaa   ")).isEqualTo("aaa");
        assertThat(obj.evaluate("${aaa}")).isNull();
    }

    @Test(groups = "executeWithExceptionsHandled")
    public void shouldWarnWhenNoCompilationUnits() throws Exception {
        AbstractRestToRamlMojoMock obj = new AbstractRestToRamlMojoMock();
        JavaCompilerMock compiler = new JavaCompilerMock();

        session(obj, new MavenSessionMock());
        mojoExecution(obj, new MojoExecutionMock());
        fileName(obj, "res.raml");
        encoding(obj, "UTF-8");
        compiler(compiler);

        obj.getProject().getBuild().setDirectory("dir");

        executeWithExceptionsHandled(obj);
        assertThat(((LogMock)obj.getLog()).getWarnContent()).isEqualTo("no source file(s) detected! Processor task will be skipped");
    }

    @Test(groups = "executeWithExceptionsHandled")
    public void shouldCompleteWhenTaskHasNoError() throws Exception {
        File dir = TestUtils.createTempDir();
        TestUtils.createTempFile(dir, "aaa.java");

        AbstractRestToRamlMojoMock obj = new AbstractRestToRamlMojoMock();
        JavaCompilerMock compiler = new JavaCompilerMock();

        compiler.getCompilationTask().setError(false);

        session(obj, new MavenSessionMock());
        mojoExecution(obj, new MojoExecutionMock());
        fileName(obj, "res.raml");
        encoding(obj, "UTF-8");
        compiler(compiler);

        obj.getProject().getBuild().setDirectory("dir");
        obj.setSourceDirectories(Collections.singleton(dir));

        executeWithExceptionsHandled(obj);
        assertThat(((LogMock)obj.getLog()).getWarnContent()).isNull();
    }

    @Test(groups = "executeWithExceptionsHandled")
    public void shouldThrowExceptionWhenTaskHasError() throws Exception {
        File dir = TestUtils.createTempDir();
        TestUtils.createTempFile(dir, "aaa.java");

        AbstractRestToRamlMojoMock obj = new AbstractRestToRamlMojoMock();
        JavaCompilerMock compiler = new JavaCompilerMock();

        compiler.getCompilationTask().setError(true);

        session(obj, new MavenSessionMock());
        mojoExecution(obj, new MojoExecutionMock());
        fileName(obj, "res.raml");
        encoding(obj, "UTF-8");
        compiler(compiler);

        obj.getProject().getBuild().setDirectory("dir");
        obj.setSourceDirectories(Collections.singleton(dir));

        assertThatThrownBy(() -> executeWithExceptionsHandled(obj)).isInstanceOf(Exception.class);
    }

    @Test(groups = "execute")
    public void shouldIgnoreWhenPomPackage() {
        AbstractRestToRamlMojoMock obj = new AbstractRestToRamlMojoMock();
        obj.getProject().setPackaging("pom");

        obj.execute();
        assertThat(obj.isExecuted()).isFalse();
    }

    @Test(groups = "execute")
    public void shouldIgnoreWhenSkipOptionSet() throws Exception {
        AbstractRestToRamlMojoMock obj = new AbstractRestToRamlMojoMock();
        skip(obj, true);
        obj.getProject().setPackaging("jar");

        obj.execute();
        assertThat(obj.isExecuted()).isFalse();
    }

    @Test(groups = "execute")
    public void shouldErrorWhenCatchException() throws Exception {
        AbstractRestToRamlMojoMock obj = new AbstractRestToRamlMojoMock();
        obj.getProject().setPackaging("jar");
        obj.setBlockExecution(true).setError(true);

        obj.execute();
        assertThat(obj.isExecuted()).isTrue();
        assertThat(((LogMock)obj.getLog()).getErrorContent()).isNotEmpty();
    }

    @Test(groups = "execute")
    public void shouldCompleteWhenNoError() throws Exception {
        AbstractRestToRamlMojoMock obj = new AbstractRestToRamlMojoMock();
        obj.getProject().setPackaging("jar");
        obj.setBlockExecution(true).setError(false);

        obj.execute();
        assertThat(obj.isExecuted()).isTrue();
        assertThat(((LogMock)obj.getLog()).getErrorContent()).isNull();
    }

    // ========== static ==========

    private static List<File> getSourceFiles(AbstractRestToRamlMojo obj) throws Exception {
        return ReflectionUtils.invokeMethod(obj, "getSourceFiles");
    }

    private static void sourceDirectories(RamlMojo obj, Set<File> sourceDirectories) throws Exception {
        ReflectionUtils.setFieldValue(obj, "sourceDirectories", sourceDirectories);
    }

    private static String buildCompileClasspath(AbstractRestToRamlMojo obj) throws Exception {
        return ReflectionUtils.invokeMethod(obj, "buildCompileClasspath");
    }

    private static void setClasspathElements(RamlMojo obj, List<String> classpathElements) throws Exception {
        ReflectionUtils.setFieldValue(obj, "classpathElements", classpathElements);
    }

    private static void pluginArtifacts(AbstractRestToRamlMojo obj, List<Artifact> pluginArtifacts) throws Exception {
        ReflectionUtils.setFieldValue(obj, "pluginArtifacts", pluginArtifacts);
    }

    private static List<String> getCompilerOptions(AbstractRestToRamlMojo obj) throws Exception {
        return ReflectionUtils.invokeMethod(obj, "getCompilerOptions");
    }

    private static void fileName(AbstractRestToRamlMojo obj, String fileName) throws Exception {
        ReflectionUtils.setFieldValue(obj, "fileName", fileName);
    }

    private static String fileName(AbstractRestToRamlMojo obj) throws Exception {
        return ReflectionUtils.getFieldValue(obj, "fileName");
    }

    private static void checkOutputDirectoryExists(AbstractRestToRamlMojo obj) throws Exception {
        ReflectionUtils.invokeMethod(obj, "checkOutputDirectoryExists");
    }

    private static void checkFileName(AbstractRestToRamlMojo obj) throws Exception {
        ReflectionUtils.invokeMethod(obj, "checkFileName");
    }

    private static String getDefFileName() throws Exception {
        return ReflectionUtils.getStaticFieldValue(AbstractRestToRamlMojo.class, "DEF_FILE_NAME");
    }

    private static void setEvaluator(AbstractRestToRamlMojo obj, PluginParameterExpressionEvaluator evaluator) throws Exception {
        ReflectionUtils.setFieldValue(obj, "evaluator", evaluator);
    }

    private static void executeWithExceptionsHandled(AbstractRestToRamlMojo obj) throws Exception {
        ReflectionUtils.invokeMethod(obj, "executeWithExceptionsHandled");
    }

    private static void compiler(JavaCompiler compiler) throws Exception {
        ReflectionUtils.setStaticFieldValue(AbstractRestToRamlMojo.class, "COMPILER", compiler);
    }

    private static void session(AbstractRestToRamlMojo obj, MavenSession session) throws Exception {
        ReflectionUtils.setFieldValue(obj, "session", session);
    }

    private static void mojoExecution(AbstractRestToRamlMojo obj, MojoExecution mojoExecution) throws Exception {
        ReflectionUtils.setFieldValue(obj, "mojoExecution", mojoExecution);
    }

    private static void encoding(AbstractRestToRamlMojo obj, String encoding) throws Exception {
        ReflectionUtils.setFieldValue(obj, "encoding", encoding);
    }

    private static void skip(AbstractRestToRamlMojo obj, boolean skip) throws Exception {
        ReflectionUtils.setFieldValue(obj, "skip", skip);
    }

    // ========== mock ==========

    private static final class AbstractRestToRamlMojoMock extends AbstractRestToRamlMojo {
        private final Set<File> sourceDirectories = new HashSet<>();
        private boolean blockExecution;
        private boolean error;
        private boolean executed;

        public AbstractRestToRamlMojoMock() {
            project = new MavenProjectMock();
            setLog(new LogMock());
        }

        public void setSourceDirectories(Set<File> sourceDirectories) {
            this.sourceDirectories.clear();
            this.sourceDirectories.addAll(sourceDirectories);
        }

        public MavenProjectMock getProject() {
            return (MavenProjectMock)project;
        }

        public AbstractRestToRamlMojoMock setBlockExecution(boolean blockExecution) {
            this.blockExecution = blockExecution;
            return this;
        }

        public AbstractRestToRamlMojoMock setError(boolean error) {
            this.error = error;
            return this;
        }

        public boolean isExecuted() {
            return executed;
        }

        // ========== AbstractRestToRamlMojo ==========

        @Override
        protected void executeWithExceptionsHandled() throws Exception {
            executed = true;

            if (blockExecution) {
                if (error)
                    throw new Exception();
            } else
                super.executeWithExceptionsHandled();
        }

        @Override
        protected Set<File> getSourceDirectories() {
            return sourceDirectories;
        }

        @Override
        protected Set<String> getClasspathElements() {
            return Collections.emptySet();
        }

        @Override
        protected String readConfiguration() {
            return null;
        }
    }
}
