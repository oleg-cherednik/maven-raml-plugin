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

import cop.raml.mocks.ExecutableElementMock;
import cop.raml.mocks.MockUtils;
import cop.raml.mocks.VariableElementMock;
import cop.raml.mocks.annotations.PathVariableMock;
import cop.raml.mocks.annotations.RequestBodyMock;
import cop.raml.mocks.annotations.RequestMappingMock;
import cop.raml.mocks.annotations.RequestParamMock;
import cop.raml.mocks.annotations.RestControllerMock;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ValueConstants;
import org.testng.annotations.Test;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oleg Cherednik
 * @since 20.01.2017
 */
@SuppressWarnings("InstanceMethodNamingConvention")
public class SpringRestImplTest {
    private static final String VAR_NAME = "foo";

    private final SpringRestImpl rest = SpringRestImpl.INSTANCE;

    @Test
    public void testIsBody() throws ClassNotFoundException {
        assertThat(rest.isBody(createVariable())).isFalse();
        assertThat(rest.isBody(createVariable(new RequestBodyMock(true)))).isTrue();
    }

    @Test
    public void testBodyRequired() throws ClassNotFoundException {
        assertThat(rest.isBodyRequired(createVariable())).isFalse();
        assertThat(rest.isBodyRequired(createVariable(new RequestBodyMock(false)))).isFalse();
        assertThat(rest.isBodyRequired(createVariable(new RequestBodyMock(true)))).isTrue();
    }

    @Test
    public void testIsUriParam() throws ClassNotFoundException {
        assertThat(rest.isUriParam(createVariable())).isFalse();
        assertThat(rest.isUriParam(createVariable(new PathVariableMock()))).isTrue();
    }

    @Test
    public void testGetUriParamName() throws ClassNotFoundException {
        assertThat(rest.getUriParamName(createVariable())).isNull();
        assertThat(rest.getUriParamName(createVariable(new PathVariableMock()))).isEqualTo(VAR_NAME);
        assertThat(rest.getUriParamName(createVariable(new PathVariableMock().setValue("aaa")))).isEqualTo("aaa");
        assertThat(rest.getUriParamName(createVariable(new PathVariableMock().setName("bbb")))).isEqualTo("bbb");
        assertThat(rest.getUriParamName(createVariable(new PathVariableMock().setValue("aaa").setName("bbb")))).isEqualTo("aaa");
    }

    @Test
    public void testIsUriParamRequired() throws ClassNotFoundException {
        assertThat(rest.isUriParamRequired(createVariable())).isFalse();
        assertThat(rest.isUriParamRequired(createVariable(new PathVariableMock().setRequired(false)))).isFalse();
        assertThat(rest.isUriParamRequired(createVariable(new PathVariableMock().setRequired(true)))).isTrue();
    }

    @Test
    public void testIsQueryParam() throws ClassNotFoundException {
        assertThat(rest.isQueryParam(createVariable())).isFalse();
        assertThat(rest.isQueryParam(createVariable(new RequestParamMock()))).isTrue();
    }

    @Test
    public void testGetQueryParamName() throws ClassNotFoundException {
        assertThat(rest.getQueryParamName(createVariable())).isNull();
        assertThat(rest.getQueryParamName(createVariable(new RequestParamMock()))).isEqualTo(VAR_NAME);
        assertThat(rest.getQueryParamName(createVariable(new RequestParamMock().setValue("aaa")))).isEqualTo("aaa");
        assertThat(rest.getQueryParamName(createVariable(new RequestParamMock().setName("bbb")))).isEqualTo("bbb");
        assertThat(rest.getQueryParamName(createVariable(new RequestParamMock().setValue("aaa").setName("bbb")))).isEqualTo("aaa");
    }

    @Test
    public void testIsQueryParamRequired() throws ClassNotFoundException {
        assertThat(rest.isQueryParamRequired(createVariable())).isFalse();
        assertThat(rest.isQueryParamRequired(createVariable(new RequestParamMock().setRequired(false)))).isFalse();
        assertThat(rest.isQueryParamRequired(createVariable(new RequestParamMock().setRequired(true)))).isTrue();
    }

    @Test
    public void testGetQueryParamDefault() throws ClassNotFoundException {
        assertThat(rest.getQueryParamDefault(createVariable())).isNull();
        assertThat(rest.getQueryParamDefault(createVariable(new RequestParamMock()))).isNull();
        assertThat(rest.getQueryParamDefault(createVariable(new RequestParamMock().setDefaultValue(null)))).isNull();
        assertThat(rest.getQueryParamDefault(createVariable(new RequestParamMock().setDefaultValue(ValueConstants.DEFAULT_NONE)))).isNull();
        assertThat(rest.getQueryParamDefault(createVariable(new RequestParamMock().setDefaultValue("")))).isEmpty();
        assertThat(rest.getQueryParamDefault(createVariable(new RequestParamMock().setDefaultValue("  ")))).isEqualTo("  ");
        assertThat(rest.getQueryParamDefault(createVariable(new RequestParamMock().setDefaultValue("aaa")))).isEqualTo("aaa");
    }

    @Test(groups = "getRequestPath")
    public void shouldReturnNullWhenGetRequestPath() throws ClassNotFoundException {
        assertThat(rest.getRequestPath(null, null)).isNull();
        assertThat(rest.getRequestPath(MockUtils.createElement(int.class).setKind(ElementKind.OTHER), null)).isNull();
        assertThat(rest.getRequestPath(MockUtils.createElement(MockUtils.Scenario.class), null)).isNull();
        assertThat(rest.getRequestPath(MockUtils.createElement(MockUtils.Scenario.class).addAnnotation(new RestControllerMock()), null)).isNull();
        assertThat(rest.getRequestPath(null, MockUtils.createExecutable("foo()"))).isNull();
        assertThat(rest.getRequestPath(null, MockUtils.createExecutable("foo()").addAnnotation(new RequestMappingMock()))).isNull();
    }

    @Test(groups = "getRequestPath")
    public void shouldReturnClassPathWhenMethodPathIsBlank() throws ClassNotFoundException {
        TypeElement classElement = MockUtils.createElement(MockUtils.Scenario.class).addAnnotation(new RestControllerMock("aaa"));
        assertThat(rest.getRequestPath(classElement, null)).isEqualTo("aaa");
    }

    @Test(groups = "getRequestPath")
    public void shouldReturnMethodValueWhenMethodAnnotationExists() throws ClassNotFoundException {
        ExecutableElementMock methodElement = MockUtils.createExecutable("foo()").addAnnotation(new RequestMappingMock().value("aaa").path("bbb"));
        assertThat(rest.getRequestPath(null, methodElement)).isEqualTo("aaa");
    }

    @Test(groups = "getRequestPath")
    public void shouldReturnMethodPathWhenMethodAnnotationExists() throws ClassNotFoundException {
        ExecutableElementMock methodElement = MockUtils.createExecutable("foo()").addAnnotation(new RequestMappingMock().path("bbb"));
        assertThat(rest.getRequestPath(null, methodElement)).isEqualTo("bbb");
    }

    @Test
    public void testGetRequestMethod() {
        assertThat(rest.getRequestMethod(MockUtils.createExecutable("foo()"))).isNull();
        assertThat(rest.getRequestMethod(MockUtils.createExecutable("foo()").addAnnotation(new RequestMappingMock()))).isNull();

        ExecutableElementMock methodElement = MockUtils.createExecutable("foo()").addAnnotation(new RequestMappingMock().method(RequestMethod.GET));
        assertThat(rest.getRequestMethod(methodElement)).isEqualTo(RequestMethod.GET.name());
    }

    @Test
    public void testGetRequestController() {
        assertThat(rest.getRequestController()).isSameAs(RestController.class);
    }

    @Test
    public void testGetRequestMapping() {
        assertThat(rest.getRequestMapping()).isSameAs(RequestMapping.class);
    }

    // ========== static ==========

    private static VariableElementMock createVariable() throws ClassNotFoundException {
        return createVariable(null);
    }

    private static VariableElementMock createVariable(Annotation annotation) throws ClassNotFoundException {
        VariableElementMock var = MockUtils.createVariable(VAR_NAME, String.class);

        if (annotation != null)
            var.addAnnotation(annotation);

        return var;
    }
}
