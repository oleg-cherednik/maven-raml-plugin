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

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Arrays;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oleg Cherednik
 * @since 21.01.2017
 */
@SuppressWarnings("ClassExplicitlyAnnotation")
public class RequestMappingMock implements RequestMapping {
    private final List<String> consumes = new ArrayList<>();
    private final List<String> produces = new ArrayList<>();
    private RequestMethod[] requestMethod;
    private String[] value;
    private String[] path;

    public RequestMappingMock addConsumes(String mediaType) {
        if (StringUtils.isNotBlank(mediaType) && !consumes.contains(mediaType))
            consumes.add(mediaType);
        return this;
    }

    public RequestMappingMock addProduces(String mediaType) {
        if (StringUtils.isNotBlank(mediaType) && !produces.contains(mediaType))
            produces.add(mediaType);
        return this;
    }

    public RequestMappingMock method(RequestMethod... requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public RequestMappingMock value(String value) {
        this.value = value != null ? Arrays.array(value) : null;
        return this;
    }

    public RequestMappingMock path(String path) {
        this.path = path != null ? Arrays.array(path) : null;
        return this;
    }

    // ========== RequestMapping ==========

    @Override
    public String name() {
        return null;
    }

    @Override
    public String[] value() {
        return value;
    }

    @Override
    public String[] path() {
        return path;
    }

    @Override
    public RequestMethod[] method() {
        return requestMethod;
    }

    @Override
    public String[] params() {
        return null;
    }

    @Override
    public String[] headers() {
        return null;
    }

    @Override
    public String[] consumes() {
        return consumes.isEmpty() ? null : consumes.toArray(new String[consumes.size()]);
    }

    @Override
    public String[] produces() {
        return produces.isEmpty() ? null : produces.toArray(new String[produces.size()]);
    }

    // ========== Annotation ==========

    @Override
    public Class<? extends Annotation> annotationType() {
        return RequestMapping.class;
    }
}
