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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.annotation.Annotation;

/**
 * @author Oleg Cherednik
 * @since 02.01.17
 */
@SuppressWarnings("ClassExplicitlyAnnotation")
public class JsonIgnorePropertiesMock implements JsonIgnoreProperties {
    private final String[] value;

    public JsonIgnorePropertiesMock(String... value) {
        this.value = ArrayUtils.isEmpty(value) ? ArrayUtils.EMPTY_STRING_ARRAY : value;
    }

    // ========== JsonIgnoreProperties ==========

    @Override
    public String[] value() {
        return value;
    }

    @Override
    public boolean ignoreUnknown() {
        return false;
    }

    @Override
    public boolean allowGetters() {
        return false;
    }

    @Override
    public boolean allowSetters() {
        return false;
    }

    // ========== Annotation ==========

    @Override
    public Class<? extends Annotation> annotationType() {
        return JsonIgnoreProperties.class;
    }
}
