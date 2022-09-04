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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.maven.model.FileSet;
import org.apache.maven.model.Resource;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Oleg Cherednik
 * @since 06.12.2016
 */
@Mojo(name = "raml", threadSafe = true, requiresDependencyResolution = ResolutionScope.COMPILE, defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class RamlMojo extends AbstractRamlConfigMojo {
    @Parameter
    private Set<File> sourceDirectories;
    @Parameter(defaultValue = "${project.compileClasspathElements}", required = true, readonly = true)
    private List<String> classpathElements;

    @Override
    public Set<File> getSourceDirectories() {
        if (CollectionUtils.isEmpty(sourceDirectories))
            return Collections.singleton(new File(project.getBuild().getSourceDirectory()));

        Set<File> dirs = new HashSet<>();

        for (File file : sourceDirectories)
            if (file != null && file.isDirectory())
                dirs.add(file);

        return dirs;
    }

    @NotNull
    @Override
    protected Set<String> getClasspathElements() {
        Set<String> elements = new LinkedHashSet<>();
        List<Resource> resources = (List<Resource>)project.getResources();

        if (CollectionUtils.isNotEmpty(resources))
            elements.addAll(resources.stream().map(FileSet::getDirectory).collect(Collectors.toList()));

        elements.addAll(classpathElements);

        return elements;
    }
}
