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
package cop.raml.mocks.annotations;

import org.springframework.web.bind.annotation.PathVariable;

import java.lang.annotation.Annotation;

/**
 * @author Oleg Cherednik
 * @since 20.01.2017
 */
@SuppressWarnings("ClassExplicitlyAnnotation")
public final class PathVariableMock implements PathVariable {
    private String value;
    private String name;
    private boolean required = true;

    public PathVariableMock setValue(String value) {
        this.value = value;
        return this;
    }

    public PathVariableMock setName(String name) {
        this.name = name;
        return this;
    }

    public PathVariableMock setRequired(boolean required) {
        this.required = required;
        return this;
    }

    // ========== RequestParam ==========

    @Override
    public String value() {
        return value;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean required() {
        return required;
    }

    // ========== RequestParam ==========

    @Override
    public Class<? extends Annotation> annotationType() {
        return PathVariable.class;
    }
}
