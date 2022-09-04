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

import cop.raml.mocks.ElementsMock;
import cop.raml.mocks.MockUtils;
import cop.raml.mocks.TypeElementMock;
import cop.raml.mocks.tools.JCTree;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oleg Cherednik
 * @since 10.01.2017
 */
@SuppressWarnings("InstanceMethodNamingConvention")
public class SorcererJavacUtilsTest {
    @BeforeClass
    private static void coberturaFix() {
        ReflectionUtils.invokeConstructor(SorcererJavacUtils.class);
    }

    @BeforeMethod
    private void initContext() {
        MockUtils.initThreadLocalContext();
    }

    @BeforeMethod
    private void clearContext() {
        ThreadLocalContext.remove();
    }

    @Test
    public void shouldReturnEmptySetWhenNoImportsInElement() throws ClassNotFoundException {
        TypeElementMock classElement = MockUtils.createElement(MockUtils.Scenario.class);
        assertThat(SorcererJavacUtils.getImports(classElement)).isEmpty();
    }

    @Test
    public void shouldReturnImportListWhenSourceContainsImports() throws ClassNotFoundException {
        TypeElementMock classElement = MockUtils.createElement(MockUtils.Scenario.class);
        ElementsMock elements = (ElementsMock)ThreadLocalContext.getElementUtils();
        assertThat(SorcererJavacUtils.getImports(classElement)).isEmpty();

        elements.addImport(PathVariable.class.getName());
        elements.addImport(RequestMapping.class.getName());
        elements.addImport("spring.empty.dto.Analysis");
        elements.addImport("spring.empty.dto.Project");
        assertThat(SorcererJavacUtils.getImports(classElement)).hasSize(4);

        elements.addImport(List.class.getName());
        assertThat(SorcererJavacUtils.getImports(classElement)).hasSize(5);

        elements.addStaticImport("org.springframework.http.MediaType.APPLICATION_JSON_VALUE");
        elements.addStaticImport("org.springframework.web.bind.annotation.RequestMethod.DELETE");
        assertThat(SorcererJavacUtils.getImports(classElement)).hasSize(5);

        elements.addImport("spring.empty.dto.Project");

        ((ElementsMock)ThreadLocalContext.getElementUtils()).getTreeAndTopLevel(null).snd.addNode(new JCTree("This is class code imitation"));
        assertThat(SorcererJavacUtils.getImports(classElement)).hasSize(5);
    }

    @Test
    public void shouldReturnEmptySetWhenExceptionOccurred() throws ClassNotFoundException {
        ((ElementsMock)ThreadLocalContext.getElementUtils()).addImport(null);
        assertThat(SorcererJavacUtils.getImports(MockUtils.createElement(MockUtils.Scenario.class))).isEmpty();
    }
}
