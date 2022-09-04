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

import cop.raml.processor.Config;
import org.apache.commons.lang3.ArrayUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * @author Oleg Cherednik
 * @since 01.04.2016
 */
abstract class AbstractRestImpl implements RestImpl {
    protected String[] getConsumesMediaTypes(@NotNull ExecutableElement methodElement) {
        return null;
    }

    protected String[] getProducesMediaTypes(@NotNull ExecutableElement methodElement) {
        return null;
    }

    // ========== RestImpl ==========

    @Override
    public Class<? extends Annotation> getRequestController() {
        return null;
    }

    @Override
    public Class<? extends Annotation> getRequestMapping() {
        return null;
    }

    @Override
    public String getRequestPath(TypeElement classElement, ExecutableElement methodElement) {
        return null;
    }

    @Override
    public String getRequestMethod(ExecutableElement methodElement) {
        return null;
    }

    // --- MediaType ---

    @Override
    public final String[] getRequestMediaTypes(@NotNull ExecutableElement methodElement) {
        String[] mediaTypes = getConsumesMediaTypes(methodElement);
        return defOnNull(mediaTypes == null ? getProducesMediaTypes(methodElement) : mediaTypes);
    }

    @Override
    public final String[] getResponseMediaTypes(@NotNull ExecutableElement methodElement) {
        String[] mediaTypes = getProducesMediaTypes(methodElement);
        return defOnNull(mediaTypes == null ? getConsumesMediaTypes(methodElement) : mediaTypes);
    }

    // --- Body ---

    @Override
    public boolean isBody(@NotNull VariableElement var) {
        return false;
    }

    @Override
    public boolean isBodyRequired(@NotNull VariableElement var) {
        return false;
    }

    // --- URI Parameters ---

    @Override
    public boolean isUriParam(@NotNull VariableElement var) {
        return false;
    }

    @Override
    public String getUriParamName(@NotNull VariableElement var) {
        return null;
    }

    @Override
    public boolean isUriParamRequired(@NotNull VariableElement var) {
        return false;
    }

    // --- Query Parameters ---

    @Override
    public boolean isQueryParam(@NotNull VariableElement var) {
        return false;
    }

    @Override
    public String getQueryParamName(@NotNull VariableElement var) {
        return null;
    }

    @Override
    public boolean isQueryParamRequired(@NotNull VariableElement var) {
        return false;
    }

    @Override
    public String getQueryParamDefault(@NotNull VariableElement var) {
        return null;
    }

    // ========== static ==========

    protected static String[] defOnNull(String... mediaTypes) {
        if (mediaTypes != null) {
            Arrays.sort(mediaTypes);
            return mediaTypes;
        }

        String def = Config.get().apiMediaType();
        return def != null ? ArrayUtils.toArray(def) : null;
    }
}
