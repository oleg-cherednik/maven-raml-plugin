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

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;

/**
 * @author Oleg Cherednik
 * @since 15.03.2016
 */
public interface RestImpl {
    Class<? extends Annotation> getRequestController();

    Class<? extends Annotation> getRequestMapping();

    String getRequestPath(TypeElement classElement, ExecutableElement methodElement);

    String getRequestMethod(ExecutableElement methodElement);

    // --- MediaType ---

    String[] getRequestMediaTypes(@NotNull ExecutableElement methodElement);

    String[] getResponseMediaTypes(@NotNull ExecutableElement methodElement);

    // --- Body ---

    boolean isBody(@NotNull VariableElement var);

    boolean isBodyRequired(@NotNull VariableElement var);

    // --- URI Parameters ---

    boolean isUriParam(@NotNull VariableElement var);

    String getUriParamName(@NotNull VariableElement var);

    boolean isUriParamRequired(@NotNull VariableElement var);

    // --- Query Parameters ---

    boolean isQueryParam(@NotNull VariableElement var);

    String getQueryParamName(@NotNull VariableElement var);

    boolean isQueryParamRequired(@NotNull VariableElement var);

    String getQueryParamDefault(@NotNull VariableElement var);
}
