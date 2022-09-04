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
package cop.maven.plugins.mocks;

import javax.lang.model.SourceVersion;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Set;

/**
 * @author Oleg Cherednik
 * @since 22.02.17
 */
public class JavaCompilerMock implements JavaCompiler {
    private final CompilationTaskMock compilationTask = new CompilationTaskMock();
    private final StandardJavaFileManager standardJavaFileManager = new StandardJavaFileManagerMock();

    public CompilationTaskMock getCompilationTask() {
        return compilationTask;
    }
    // ========== JavaCompiler ==========

    @Override
    public CompilationTask getTask(Writer out, JavaFileManager fileManager,
            DiagnosticListener<? super JavaFileObject> diagnosticListener, Iterable<String> options, Iterable<String> classes,
            Iterable<? extends JavaFileObject> compilationUnits) {
        return compilationTask;
    }

    @Override
    public StandardJavaFileManager getStandardFileManager(DiagnosticListener<? super JavaFileObject> diagnosticListener, Locale locale,
            Charset charset) {
        return standardJavaFileManager;
    }

    // ========== OptionChecker ==========

    @Override
    public int isSupportedOption(String option) {
        return 0;
    }

    // ========== Tool ==========

    @Override
    public int run(InputStream in, OutputStream out, OutputStream err, String... arguments) {
        return 0;
    }

    @Override
    public Set<SourceVersion> getSourceVersions() {
        return null;
    }
}
