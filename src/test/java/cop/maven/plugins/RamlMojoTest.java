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

import cop.maven.plugins.mocks.FileMock;
import cop.raml.TestUtils;
import cop.raml.utils.ReflectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.model.Resource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oleg Cherednik
 * @since 12.02.17
 */
@SuppressWarnings({ "InstanceMethodNamingConvention", "serial" })
public class RamlMojoTest {
    private RamlMojo mojo;

    @BeforeMethod
    public void init() throws Exception {
        mojo = new RamlMojoMock();
    }

    @Test(groups = "getSourceDirectories")
    public void shouldReturnProjectBuildSourcePathWhenSourceDirectoryEmpty() throws Exception {
        mojo.project.getBuild().setSourceDirectory("aaa/bbb");

        Set<File> files = mojo.getSourceDirectories();
        assertThat(files).hasSize(1);
        assertThat(files.iterator().next().getPath()).isEqualTo(FilenameUtils.normalize("aaa/bbb"));
    }

    @Test(groups = "getSourceDirectories")
    public void shouldReturnSourceDirectoryWhenSourceDirectoryNotEmpty() throws Exception {
        setSourceDirectories(mojo, new LinkedHashSet<File>() {{
            add(FileMock.createDirectory("aaa"));
            add(FileMock.createFile("bbb"));
            add(null);
        }});

        Set<File> files = mojo.getSourceDirectories();
        assertThat(files).hasSize(1);
        assertThat(files.iterator().next().getPath()).isEqualTo("aaa");
    }

    @Test(groups = "getClasspathElements")
    public void shouldAddProjectResourcesWhenResourcesNotEmpty() throws Exception {
        setBuildResources(mojo, Collections.singletonList(TestUtils.createResource("aaa")));
        setClasspathElements(mojo, Collections.singletonList("bbb"));

        assertThat(mojo.getClasspathElements()).containsExactly("aaa", "bbb");
    }

    @Test(groups = "getClasspathElements")
    public void shouldReturnClasspathElementWhenResourcesEmpty() throws Exception {
        setClasspathElements(mojo, Collections.singletonList("aaa"));
        assertThat(mojo.getClasspathElements()).containsExactly("aaa");
    }

    // ========== static ==========

    private static void setSourceDirectories(RamlMojo obj, Set<File> sourceDirectories) throws Exception {
        ReflectionUtils.setFieldValue(obj, "sourceDirectories", sourceDirectories);
    }

    private static void setClasspathElements(RamlMojo obj, List<String> classpathElements) throws Exception {
        ReflectionUtils.setFieldValue(obj, "classpathElements", classpathElements);
    }

    private static void setBuildResources(AbstractRamlConfigMojo obj, List<Resource> resources) throws Exception {
        obj.project.getBuild().setResources(resources);
    }
}
