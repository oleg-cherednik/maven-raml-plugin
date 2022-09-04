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
package cop.raml.mocks;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Oleg Cherednik
 * @since 12.12.2016
 */
public class ProcessingEnvironmentMock implements ProcessingEnvironment {
    private final Elements elements = new ElementsMock();
    private final MessagerMock messager = new MessagerMock();
    private final Map<String, String> options = new HashMap<>();
    private Filer filer;

    public ProcessingEnvironmentMock addOptions(String key, String value) {
        options.put(key, value);
        return this;
    }

    public ProcessingEnvironmentMock setFiler(Filer filer) {
        this.filer = filer;
        return this;
    }

    // ========== ProcessingEnvironment ==========

    @Override
    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public Messager getMessager() {
        return messager;
    }

    @Override
    public Filer getFiler() {
        return filer;
    }

    @Override
    public Elements getElementUtils() {
        return elements;
    }

    @Override
    public Types getTypeUtils() {
        return null;
    }

    @Override
    public SourceVersion getSourceVersion() {
        return null;
    }

    @Override
    public Locale getLocale() {
        return null;
    }
}
