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
package cop.raml.processor.rest;

import cop.raml.processor.exceptions.RamlProcessingException;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

/**
 * @author Oleg Cherednik
 * @since 25.01.2017
 */
public final class RestImplMock extends AbstractRestImpl {
    private String requestPath;
    private String requestMethod;
    private Class<? extends Annotation> requestMapping;
    private boolean error;

    public RestImplMock setRequestPath(String requestPath) {
        this.requestPath = requestPath;
        return this;
    }

    public RestImplMock setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public RestImplMock setRequestMapping(Class<? extends Annotation> requestMapping) {
        this.requestMapping = requestMapping;
        return this;
    }

    public RestImplMock setError(boolean error) {
        this.error = error;
        return this;
    }

    // ========== RestImpl ==========

    @Override
    public String getRequestPath(TypeElement classElement, ExecutableElement methodElement) {
        return requestPath;
    }

    @Override
    public Class<? extends Annotation> getRequestMapping() {
        if (error)
            throw new RamlProcessingException();
        return requestMapping;
    }

    @Override
    public String getRequestMethod(ExecutableElement methodElement) {
        if (error)
            throw new RamlProcessingException();
        return requestMethod;
    }
}
