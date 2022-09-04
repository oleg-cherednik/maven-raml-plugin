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
package cop.raml.processor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Oleg Cherednik
 * @since 11.12.2016
 */
abstract class AbstractProcessorTest {
    private static final IOFileFilter JAVA_FILE_FILTER = new SuffixFileFilter(".java");

    protected AbstractProcessorTest() {
    }

    protected static boolean runAnnotationProcessor(File dir) throws URISyntaxException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        Set<JavaFileObject> compilationUnits = new LinkedHashSet<>();

        for (File file : (List<File>)FileUtils.listFiles(dir, JAVA_FILE_FILTER, DirectoryFileFilter.INSTANCE))
            compilationUnits.add(new SimpleJavaFileObjectImpl(file, JavaFileObject.Kind.SOURCE));

        List<String> options = new ArrayList<>();
//        options.put("-Apackages=com.bosch");
//        options.put("-AfilterRegex=AnalysisController");
//        options.put("-Aversion=0.8");
        options.add("-d");
        options.add("target");

        JavaCompiler.CompilationTask task = compiler.getTask(null, null, null, options, null, compilationUnits);
        task.setProcessors(Collections.singleton(new RestProcessor()));

        return task.call();
    }

    protected static File getResourceAsFile(String path) {
        URL url = AbstractProcessorTest.class.getClassLoader().getResource(path);

        try {
            return url != null ? new File(url.toURI()) : null;
        } catch(URISyntaxException ignored) {
            return new File(url.getPath());
        }
    }
}
