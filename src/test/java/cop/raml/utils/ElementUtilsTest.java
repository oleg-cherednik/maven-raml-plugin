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
package cop.raml.utils;

import cop.raml.processor.exceptions.RamlProcessingException;
import cop.raml.mocks.ElementMock;
import cop.raml.mocks.ImportScannerMock;
import cop.raml.mocks.MockUtils;
import cop.raml.mocks.VariableElementMock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;

import static cop.raml.mocks.MockUtils.Count;
import static cop.raml.mocks.MockUtils.Scenario;
import static cop.raml.mocks.MockUtils.createStaticVariable;
import static cop.raml.mocks.MockUtils.createVariable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Oleg Cherednik
 * @since 05.01.2017
 */
@SuppressWarnings("InstanceMethodNamingConvention")
public class ElementUtilsTest {
    @BeforeClass
    private static void coberturaFix() {
        ReflectionUtils.invokeConstructor(ElementUtils.class);
    }

    @BeforeMethod
    private void initContext() {
        MockUtils.initThreadLocalContext();
    }

    @AfterMethod
    private void clearContext() {
        ThreadLocalContext.remove();
    }

    @Test(groups = "getType")
    public void shouldReturnLongTypeWhenLongVariable() throws Exception {
        TypeMirror type = ElementUtils.getType(createVariable("id", long.class));
        assertThat(type).isNotNull();
        assertThat(type.getKind()).isSameAs(TypeKind.LONG);
        assertThat(type.toString()).isEqualTo(long.class.getName());
    }

    @Test(groups = "getType")
    public void shouldReturnDeclaredTypeWhenEnumVariable() throws Exception {
        TypeMirror type = ElementUtils.getType(createVariable("count", Count.class));
        assertThat(type).isNotNull();
        assertThat(type.getKind()).isSameAs(TypeKind.DECLARED);
        assertThat(type.toString()).isEqualTo(Count.class.getName());
        assertThat(ElementUtils.asElement(type).getKind()).isSameAs(ElementKind.ENUM);
        assertThat((List<Element>)ElementUtils.asElement(type).getEnclosedElements()).hasSize(4);
    }

    @Test(groups = "getType")
    public void shouldReturnDeclaredTypeForEnumWhenEnumArrayVariable() throws Exception {
        TypeMirror type = ElementUtils.getType(createVariable("counts", Count[].class));
        assertThat(type).isNotNull();
        assertThat(type.getKind()).isSameAs(TypeKind.DECLARED);
        assertThat(type.toString()).isEqualTo(Count.class.getName());
        assertThat(ElementUtils.asElement(type).getKind()).isSameAs(ElementKind.ENUM);
    }

    @Test(groups = "getType")
    public void shouldReturnDeclaredTypeWhenClassVariable() throws Exception {
        TypeMirror type = ElementUtils.getType(createVariable("scenario", Scenario.class));
        assertThat(type).isNotNull();
        assertThat(type.getKind()).isSameAs(TypeKind.DECLARED);
        assertThat(type.toString()).isEqualTo(Scenario.class.getName());
        assertThat(ElementUtils.asElement(type).getKind()).isSameAs(ElementKind.CLASS);
        assertThat((List<Element>)ElementUtils.asElement(type).getEnclosedElements()).isNotEmpty();
    }

    @Test(groups = "getType")
    public void shouldThrowExceptionWhenCollectionVariableAndNoTypeMacro() throws Exception {
        assertThatThrownBy(() -> ElementUtils.getType(createVariable("counts", List.class)))
                .isInstanceOf(RamlProcessingException.class);
    }

    @Test(groups = "getType")
    public void shouldUseTypeMacroWhenCollectionVariableAndTypeMacro() throws Exception {
        TypeElement expectedType = ElementUtils.asElement(createVariable("count", Count.class).asType());
        ImportScannerMock importScanner = (ImportScannerMock)ThreadLocalContext.getImportScanner();
        importScanner.addElement(Count.class.getSimpleName(), expectedType);

        VariableElementMock var = createVariable("counts", List.class);
        var.setDocComment("{@type Count}");

        TypeElement actualType = ElementUtils.asElement(ElementUtils.getType(var));
        assertThat(actualType).isSameAs(expectedType);
    }

    @Test
    public void testIsStatic() throws Exception {
        assertThat(ElementUtils.isStatic(null)).isFalse();
        assertThat(ElementUtils.isStatic(createVariable("id", long.class))).isFalse();
        assertThat(ElementUtils.isStatic(new NoStaticElement())).isFalse();
        assertThat(ElementUtils.isStatic(createStaticVariable("id", long.class))).isTrue();
    }

    @Test
    public void testIsEnum() throws Exception {
        assertThat(ElementUtils.isEnum(null)).isFalse();
        assertThat(ElementUtils.isEnum(createVariable("id", long.class).asType())).isFalse();
        assertThat(ElementUtils.isEnum(createVariable("count", Count.class).asType())).isTrue();
    }

    @Test
    public void testIsArray() throws Exception {
        assertThat(ElementUtils.isArray(null)).isFalse();
        assertThat(ElementUtils.isArray(createVariable("count", Count.class))).isFalse();
        assertThat(ElementUtils.isArray(createVariable("counts", Count[].class))).isTrue();
    }

    @Test
    public void testIsCollection() throws Exception {
        assertThat(ElementUtils.isCollection(null)).isFalse();
        assertThat(ElementUtils.isCollection(createVariable("count", Count.class))).isFalse();
        assertThat(ElementUtils.isCollection(createVariable("countList", List.class))).isTrue();
    }

    // ========== data ==========

    private static class NoStaticElement extends ElementMock {
        @Override
        public boolean isStatic() {
            throw new RuntimeException("isStatic()");
        }
    }
}
