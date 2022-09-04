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

import cop.raml.processor.Config;
import cop.raml.processor.RestProcessor;
import cop.raml.utils.Utils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Oleg Cherednik
 * @since 04.12.2016
 */
abstract class AbstractRestToRamlMojo extends AbstractMojo {
    private static final String DEF_FILE_NAME = "${project.artifactId}_${project.version}";

    private static final Lock LOCK = new ReentrantLock();
    private static final IOFileFilter JAVA_FILE_FILTER = new SuffixFileFilter(".java");

    /** output directory */
    @Parameter(defaultValue = "docs", required = true)
    protected String out;
    @Parameter(defaultValue = "${mojoExecution}", readonly = true, required = true)
    protected MojoExecution mojoExecution;
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;
    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;
    @Parameter(property = "plugin.artifacts", readonly = true)
    private List<Artifact> pluginArtifacts;
    @Parameter(property = "project.build.sourceEncoding", defaultValue = "UTF-8", required = true)
    private String encoding;
    @Parameter(defaultValue = "false")
    private boolean skip;
    @Parameter(property = "fileName", defaultValue = DEF_FILE_NAME)
    private String fileName;

    private PluginParameterExpressionEvaluator evaluator;

    // ---------- abstract ----------

    protected abstract Set<File> getSourceDirectories();

    @NotNull
    protected abstract Set<String> getClasspathElements();

    @NotNull
    protected abstract String readConfiguration() throws IOException;

    // ----------

    private List<File> getSourceFiles() {
        return getSourceDirectories().stream()
                                     .map(sourceDirectory -> FileUtils.listFiles(sourceDirectory, JAVA_FILE_FILTER, DirectoryFileFilter.INSTANCE))
                                     .map(sourceFiles -> (List<File>)sourceFiles)
                                     .flatMap(List::stream)
                                     .collect(Collectors.toList());
    }

    private List<String> getCompilerOptions() {
        List<String> options = new ArrayList<>();

        options.add("-cp");
        options.add(buildCompileClasspath());

        options.add("-proc:only");

        options.add("-processor");
        options.add(RestProcessor.class.getName());

        options.add("-d");
        options.add(out);

        options.add(String.format("-A%s=%s", RestProcessor.KEY_FILE_NAME, fileName));

        return options;
    }

    private void checkOutputDirectoryExists() {
        if (out == null)
            out = "docs";
        if (!out.startsWith(project.getBuild().getDirectory()))
            out = FilenameUtils.concat(project.getBuild().getDirectory(), out);
        if (new File(out).mkdirs())
            getLog().debug(String.format("Output directory '%s' created", out));
    }

    private void checkFileName() {
        fileName = evaluate(StringUtils.isNotBlank(fileName) ? fileName : DEF_FILE_NAME);

        if (StringUtils.isBlank(fileName) && StringUtils.isBlank(fileName = evaluate(DEF_FILE_NAME)))
            fileName = RestProcessor.DEF_FILE_NAME;
    }

    protected String evaluate(String expr) {
        try {
            return Utils.trimLine((String)evaluator.evaluate(expr));
        } catch(Exception ignored) {
            return null;
        }
    }

    private static final JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();

    protected void executeWithExceptionsHandled() throws Exception {
        evaluator = new PluginParameterExpressionEvaluator(session, mojoExecution);

        checkOutputDirectoryExists();
        checkFileName();

        FileUtils.write(new File(out, Config.YAML), readConfiguration(), encoding);

        StandardJavaFileManager fileManager = COMPILER.getStandardFileManager(null, null, Charset.forName(encoding));
        List<JavaFileObject> compilationUnits = StreamSupport.stream(fileManager.getJavaFileObjectsFromFiles(getSourceFiles()).spliterator(), true)
                                                             .collect(Collectors.toList());

        if (compilationUnits.isEmpty())
            getLog().warn("no source file(s) detected! Processor task will be skipped");
        else {
            JavaCompiler.CompilationTask task = COMPILER.getTask(null, fileManager, null, getCompilerOptions(), null, compilationUnits);

            if (!task.call())
                throw new Exception("error during compilation");
        }
    }

    private String buildCompileClasspath() {
        Set<String> elements = new LinkedHashSet<>();

        if (CollectionUtils.isNotEmpty(pluginArtifacts))
            elements.addAll(pluginArtifacts.stream()
                                           .filter(artifact -> Artifact.SCOPE_COMPILE.equalsIgnoreCase(artifact.getScope())
                                                   || Artifact.SCOPE_RUNTIME.equalsIgnoreCase(artifact.getScope()))
                                           .map(Artifact::getFile)
                                           .filter(Objects::nonNull)
                                           .map(File::getAbsolutePath)
                                           .collect(Collectors.toList()));

        elements.addAll(getClasspathElements());

        return String.join(";", elements);
    }

    // ========== Mojo ==========

    @Override
    public final void execute() {
        if ("pom".equalsIgnoreCase(project.getPackaging()) || skip)
            return;

        LOCK.lock();

        try {
            executeWithExceptionsHandled();
        } catch(Exception e) {
            getLog().error("error on execute: " + e.getStackTrace()[0], e);
        } finally {
            LOCK.unlock();
        }
    }
}
