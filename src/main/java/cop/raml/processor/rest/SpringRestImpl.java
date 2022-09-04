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

import cop.raml.utils.Utils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ValueConstants;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;

/**
 * @author Oleg Cherednik
 * @since 15.03.2016
 */
public final class SpringRestImpl extends AbstractRestImpl {
    public static final SpringRestImpl INSTANCE = new SpringRestImpl();

    public static SpringRestImpl getInstance() {
        return INSTANCE;
    }

    private SpringRestImpl() {
    }

    // ========== AbstractRestImpl ==========

    @Override
    protected String[] getConsumesMediaTypes(@NotNull ExecutableElement methodElement) {
        RequestMapping annotation = methodElement.getAnnotation(RequestMapping.class);
        String[] consumers = annotation != null ? annotation.consumes() : null;
        return ArrayUtils.isNotEmpty(consumers) ? consumers : null;
    }

    @Override
    protected String[] getProducesMediaTypes(@NotNull ExecutableElement methodElement) {
        RequestMapping annotation = methodElement.getAnnotation(RequestMapping.class);
        String[] produces = annotation != null ? annotation.produces() : null;
        return ArrayUtils.isNotEmpty(produces) ? produces : null;
    }

    // ========== RestImpl ==========

    @Override
    public Class<? extends Annotation> getRequestController() {
        return RestController.class;
    }

    @Override
    public Class<? extends Annotation> getRequestMapping() {
        return RequestMapping.class;
    }

    @Override
    public String getRequestPath(TypeElement classElement, ExecutableElement methodElement) {
        return Utils.join(getClassRequestPath(classElement), getMethodRequestPath(methodElement));
    }

    @Override
    public String getRequestMethod(ExecutableElement methodElement) {
        RequestMapping annotation = methodElement.getAnnotation(RequestMapping.class);
        // TODO multiple values are not supported now
        return annotation != null && ArrayUtils.isNotEmpty(annotation.method()) ? annotation.method()[0].name() : null;
    }

    // --- Body ---

    @Override
    public boolean isBody(@NotNull VariableElement var) {
        return var.getAnnotation(RequestBody.class) != null;
    }

    @Override
    public boolean isBodyRequired(@NotNull VariableElement var) {
        RequestBody annotation = var.getAnnotation(RequestBody.class);
        return annotation != null && annotation.required();
    }

    // --- URI Parameters ---

    @Override
    public boolean isUriParam(@NotNull VariableElement var) {
        return var.getAnnotation(PathVariable.class) != null;
    }

    @Override
    public String getUriParamName(@NotNull VariableElement var) {
        PathVariable annotation = var.getAnnotation(PathVariable.class);

        if (annotation == null)
            return null;
        if (StringUtils.isNotBlank(annotation.value()))
            return annotation.value();
        if (StringUtils.isNotBlank(annotation.name()))
            return annotation.name();
        return var.getSimpleName().toString();
    }

    @Override
    public boolean isUriParamRequired(@NotNull VariableElement var) {
        PathVariable annotation = var.getAnnotation(PathVariable.class);
        return annotation != null && annotation.required();
    }

    // --- Query Parameters ---

    @Override
    public boolean isQueryParam(@NotNull VariableElement var) {
        return var.getAnnotation(RequestParam.class) != null;
    }

    @Override
    public String getQueryParamName(@NotNull VariableElement var) {
        RequestParam annotation = var.getAnnotation(RequestParam.class);

        if (annotation == null)
            return null;
        if (StringUtils.isNotBlank(annotation.value()))
            return annotation.value();
        if (StringUtils.isNotBlank(annotation.name()))
            return annotation.name();
        return var.getSimpleName().toString();
    }

    @Override
    public boolean isQueryParamRequired(@NotNull VariableElement var) {
        RequestParam annotation = var.getAnnotation(RequestParam.class);
        return annotation != null && annotation.required();
    }

    @Override
    public String getQueryParamDefault(@NotNull VariableElement var) {
        RequestParam annotation = var.getAnnotation(RequestParam.class);
        return annotation != null && !ValueConstants.DEFAULT_NONE.equals(annotation.defaultValue()) ? annotation.defaultValue() : null;
    }

    // ========== static ==========

    private static String getClassRequestPath(TypeElement classElement) {
        if (classElement == null || classElement.getKind() != ElementKind.CLASS)
            return null;

        RestController annotation = classElement.getAnnotation(RestController.class);
        return annotation != null && StringUtils.isNoneBlank(annotation.value()) ? annotation.value() : null;
    }

    private static String getMethodRequestPath(ExecutableElement methodElement) {
        if (methodElement == null)
            return null;

        RequestMapping annotation = methodElement.getAnnotation(RequestMapping.class);

        if (annotation != null) {
            // TODO multiple values are not supported now
            if (ArrayUtils.isNotEmpty(annotation.value()))
                return annotation.value()[0];
            if (ArrayUtils.isNotEmpty(annotation.path()))
                return annotation.path()[0];
        }

        return null;
    }
}
