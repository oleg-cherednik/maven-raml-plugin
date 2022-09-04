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

import javax.annotation.processing.Processor;
import javax.tools.JavaCompiler;
import java.util.Locale;

/**
 * @author Oleg Cherednik
 * @since 23.02.2017
 */
public class CompilationTaskMock implements JavaCompiler.CompilationTask {
    private boolean error;

    public void setError(boolean error) {
        this.error = error;
    }

    // ========== CompilationTask ==========

    @Override
    public void setProcessors(Iterable<? extends Processor> processors) {
    }

    @Override
    public void setLocale(Locale locale) {
    }

    @Override
    public Boolean call() {
        return !error;
    }
}
