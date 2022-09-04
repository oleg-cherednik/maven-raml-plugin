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

import cop.raml.TestUtils;
import cop.raml.processor.Config;
import cop.raml.utils.javadoc.tags.TagLink;
import cop.raml.mocks.ElementsMock;
import cop.raml.mocks.ImportScannerMock;
import cop.raml.mocks.MockUtils;
import cop.raml.mocks.ProcessingEnvironmentMock;
import cop.raml.mocks.RoundEnvironmentMock;
import cop.raml.mocks.TypeElementMock;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Oleg Cherednik
 * @since 10.01.2017
 */
public class ThreadLocalContextTest {
    @BeforeClass
    private static void coberturaFix() {
        ReflectionUtils.invokeConstructor(ThreadLocalContext.class);
    }

    @BeforeMethod
    public void clearContext() {
        ThreadLocalContext.remove();
    }

    @Test
    public void testSetGetProcessingEnv() {
        ProcessingEnvironment processingEnv = new ProcessingEnvironmentMock();
        assertThat(ThreadLocalContext.getProcessingEnv()).isNull();

        ThreadLocalContext.setProcessingEnv(processingEnv);
        assertThat(ThreadLocalContext.getProcessingEnv()).isSameAs(processingEnv);
    }

    @Test
    public void testSetGetImportScanner() {
        ImportScanner importScanner = new ImportScannerMock();
        assertThat(ThreadLocalContext.getImportScanner()).isNull();

        ThreadLocalContext.setImportScanner(importScanner);
        assertThat(ThreadLocalContext.getImportScanner()).isSameAs(importScanner);
    }

    @Test
    public void testSetGetConfig() {
        Config config = Config.builder().ramlShowExample(true).build();
        assertThat(ThreadLocalContext.getConfig()).isSameAs(Config.NULL);

        ThreadLocalContext.setConfig(config);
        assertThat(ThreadLocalContext.getConfig()).isSameAs(config);
    }

    @Test
    public void testGetMessager() {
        ThreadLocalContext.setProcessingEnv(new ProcessingEnvironmentMock());
        assertThat(ThreadLocalContext.getMessager()).isSameAs(ThreadLocalContext.getProcessingEnv().getMessager());
    }

    @Test
    public void testSetGetRoundEnv() {
        RoundEnvironment roundEnv = new RoundEnvironmentMock();
        assertThat(ThreadLocalContext.getRoundEnv()).isNull();

        ThreadLocalContext.setRoundEnv(roundEnv);
        assertThat(ThreadLocalContext.getRoundEnv()).isSameAs(roundEnv);
    }

    @Test
    public void testGetDocCommentElement() throws ClassNotFoundException {
        ThreadLocalContext.setProcessingEnv(new ProcessingEnvironmentMock());
        TypeElementMock typeElement = MockUtils.createElement(MockUtils.Count.class);
        typeElement.setDocComment("javadoc");

        assertThat(ThreadLocalContext.getDocComment((Element)null)).isNull();
        assertThat(ThreadLocalContext.getDocComment(typeElement)).isEqualTo("javadoc");
    }

    @Test
    public void testGetDocCommentLink() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        TypeElementMock typeElement = MockUtils.createElement(MockUtils.Count.class);
        typeElement.setDocComment("javadoc");

        ImportScannerMock importScanner = new ImportScannerMock();
        importScanner.addElement(MockUtils.Count.class.getName(), typeElement);

        ThreadLocalContext.setProcessingEnv(new ProcessingEnvironmentMock());
        ThreadLocalContext.setImportScanner(importScanner);

        assertThat(ThreadLocalContext.getDocComment(TagLink.NULL)).isNull();
        assertThat(ThreadLocalContext.getDocComment(TagLink.create(MockUtils.Count.class.getName(), null))).isEqualTo("javadoc");
    }

    @Test
    public void testGetDocCommentAsList() throws ClassNotFoundException {
        ThreadLocalContext.setProcessingEnv(new ProcessingEnvironmentMock());
        TypeElementMock typeElement = MockUtils.createElement(MockUtils.Count.class);
        typeElement.setDocComment(TestUtils.joinStrings("line1", "line2", "line3"));
        assertThat(ThreadLocalContext.getDocCommentAsList(typeElement)).hasSize(3);
    }

    @Test
    public void testGetTypeElement() throws ClassNotFoundException {
        ProcessingEnvironmentMock processingEnv = new ProcessingEnvironmentMock();
        TypeElementMock typeElement = MockUtils.createElement(MockUtils.Count.class);

        ((ElementsMock)processingEnv.getElementUtils()).addTypeElement(typeElement);

        ThreadLocalContext.setProcessingEnv(processingEnv);
        assertThat(ThreadLocalContext.getElement(MockUtils.Count.class.getName())).isSameAs(typeElement);
    }

    @Test
    public void testGetElementUtils() throws ClassNotFoundException {
        ProcessingEnvironmentMock processingEnv = new ProcessingEnvironmentMock();
        assertThat(ThreadLocalContext.getProcessingEnv()).isNull();

        ThreadLocalContext.setProcessingEnv(processingEnv);
        assertThat(ThreadLocalContext.getElementUtils()).isSameAs(processingEnv.getElementUtils());
    }

    @Test
    public void testSetGetClassName() {
        assertThat(ThreadLocalContext.getClassName()).isNull();
        ThreadLocalContext.setClassName("className");
        assertThat(ThreadLocalContext.getClassName()).isEqualTo("className");
    }

    @Test
    public void testSetGetMethodName() {
        assertThat(ThreadLocalContext.getMethodName()).isNull();
        ThreadLocalContext.setMethodName("methodName");
        assertThat(ThreadLocalContext.getMethodName()).isEqualTo("methodName");
    }

    @Test
    public void testSetGetParamName() {
        assertThat(ThreadLocalContext.getParamName()).isNull();
        ThreadLocalContext.setParamName("paramName");
        assertThat(ThreadLocalContext.getParamName()).isEqualTo("paramName");
    }

    @Test
    public void testRemove() {
        assertThatContextIsNull();

        ThreadLocalContext.setProcessingEnv(new ProcessingEnvironmentMock());
        ThreadLocalContext.setImportScanner(new ImportScannerMock());
        ThreadLocalContext.setConfig(Config.builder().ramlShowExample(true).build());
        ThreadLocalContext.setRoundEnv(new RoundEnvironmentMock());
        ThreadLocalContext.setClassName("className");
        ThreadLocalContext.setMethodName("methodName");
        ThreadLocalContext.setParamName("paramName");

        assertThatContextIsNotNull();
        ThreadLocalContext.remove();
        assertThatContextIsNull();
    }

    // ========== static ==========

    private static void assertThatContextIsNull() {
        assertThat(ThreadLocalContext.getProcessingEnv()).isNull();
        assertThat(ThreadLocalContext.getImportScanner()).isNull();
        assertThat(ThreadLocalContext.getConfig()).isSameAs(Config.NULL);
        assertThat(ThreadLocalContext.getRoundEnv()).isNull();
        assertThat(ThreadLocalContext.getClassName()).isNull();
        assertThat(ThreadLocalContext.getMethodName()).isNull();
        assertThat(ThreadLocalContext.getParamName()).isNull();
    }

    private static void assertThatContextIsNotNull() {
        assertThat(ThreadLocalContext.getProcessingEnv()).isNotNull();
        assertThat(ThreadLocalContext.getImportScanner()).isNotNull();
        assertThat(ThreadLocalContext.getConfig()).isNotSameAs(Config.NULL);
        assertThat(ThreadLocalContext.getMessager()).isNotNull();
        assertThat(ThreadLocalContext.getRoundEnv()).isNotNull();
        assertThat(ThreadLocalContext.getClassName()).isNotNull();
        assertThat(ThreadLocalContext.getMethodName()).isNotNull();
        assertThat(ThreadLocalContext.getParamName()).isNotNull();
    }

}
