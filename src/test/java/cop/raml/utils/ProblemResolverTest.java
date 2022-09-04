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

import cop.raml.mocks.MessagerMock;
import cop.raml.mocks.MockUtils;
import cop.raml.processor.Config;
import cop.raml.processor.exceptions.RamlProcessingException;
import cop.raml.utils.javadoc.Macro;
import cop.raml.utils.javadoc.tags.TagLink;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Oleg Cherednik
 * @since 18.12.2016
 */
@SuppressWarnings("InstanceMethodNamingConvention")
public class ProblemResolverTest {
    @BeforeClass
    private static void coberturaFix() {
        ReflectionUtils.invokeConstructor(ProblemResolver.class);
    }

    @BeforeMethod
    private void initContext() {
        MockUtils.initThreadLocalContext();
    }

    @BeforeMethod
    private void clearContext() {
        ThreadLocalContext.remove();
    }

    @Test(groups = "methodParamDuplication")
    public void shouldIgnoreWhenParamNameBlank() {
        MessagerMock messager = (MessagerMock)ThreadLocalContext.getMessager();
        assertThat(messager).isNotNull();

        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(false).build());
        ThreadLocalContext.setClassName("Foo");
        ThreadLocalContext.setMethodName("getParam");

        ThreadLocalContext.setParamName("");
        ProblemResolver.methodParamDuplication();
        assertThat(messager.getMessage()).isNull();

        ThreadLocalContext.setParamName(null);
        ProblemResolver.methodParamDuplication();
        assertThat(messager.getMessage()).isNull();
    }

    @Test(groups = "methodParamDuplication")
    public void shouldThrowExceptionWhenMethodDuplicationAndStopOnError() {
        ThreadLocalContext.setClassName("Foo");
        ThreadLocalContext.setMethodName("getParam");
        ThreadLocalContext.setParamName("param");

        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());
        assertThatThrownBy(ProblemResolver::methodParamDuplication).isInstanceOf(RamlProcessingException.class);
    }

    @Test(groups = "methodParamDuplication")
    public void shouldPrintMessageWhenMethodDuplicationAndNoStopOnError() {
        MessagerMock messager = (MessagerMock)ThreadLocalContext.getMessager();
        assertThat(messager).isNotNull();

        ThreadLocalContext.setClassName("Foo");
        ThreadLocalContext.setMethodName("getParam");
        ThreadLocalContext.setParamName("param");

        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(false).build());
        ProblemResolver.methodParamDuplication();
        assertThat(messager.getMessage()).isNotEmpty();
    }

    @Test(groups = "paramLinksToItself")
    public void shouldIgnoreWhenLinkNotSet() {
        MessagerMock messager = (MessagerMock)ThreadLocalContext.getMessager();
        assertThat(messager).isNotNull();

        ProblemResolver.paramLinksToItself(null);
        messager.clearMessage();
        assertThat(messager.getMessage()).isNull();

        ProblemResolver.paramLinksToItself(TagLink.NULL);
        assertThat(messager.getMessage()).isNull();
    }

    @Test(groups = "paramLinksToItself")
    public void shouldThrowExceptionWhenParamLinkToItselfAndStopOnError() {
        TagLink link = TagLink.create("foo", "param");

        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());
        assertThatThrownBy(() -> ProblemResolver.paramLinksToItself(link)).isInstanceOf(RamlProcessingException.class);
    }

    @Test(groups = "paramLinksToItself")
    public void shouldPrintMessageWhenParamLinkToItselfAndNoStopOnError() {
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(false).build());
        ProblemResolver.paramLinksToItself(TagLink.create("foo", "param"));
        assertThat(((MessagerMock)ThreadLocalContext.getMessager()).getMessage()).isNotEmpty();
    }

    @Test(groups = "macroDuplication")
    public void shouldIgnoreWhenMacroDuplicationAndMacroNotSet() {
        MessagerMock messager = (MessagerMock)ThreadLocalContext.getMessager();
        assertThat(messager).isNotNull();

        ThreadLocalContext.setClassName("Foo");
        ThreadLocalContext.setMethodName("getParam");
        ThreadLocalContext.setParamName("param");

        messager.clearMessage();
        ProblemResolver.macroDuplication(null);
        assertThat(messager.getMessage()).isNull();
    }

    @Test(groups = "macroDuplication")
    public void shouldThrowExceptionWhenMacroDuplicationAndStopOnError() {
        ThreadLocalContext.setClassName("Foo");
        ThreadLocalContext.setMethodName("getParam");
        ThreadLocalContext.setParamName("param");
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());
        assertThatThrownBy(() -> ProblemResolver.macroDuplication(Macro.PATTERN)).isInstanceOf(RamlProcessingException.class);
    }

    @Test(groups = "macroDuplication")
    public void shouldPrintMessageWhenMacroDuplicationAndNoStopOnError() {
        ThreadLocalContext.setClassName("Foo");
        ThreadLocalContext.setMethodName("getParam");
        ThreadLocalContext.setParamName("param");

        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(false).build());
        ProblemResolver.macroDuplication(Macro.PATTERN);
        assertThat(((MessagerMock)ThreadLocalContext.getMessager()).getMessage()).isNotEmpty();
    }

    @Test
    public void shouldThrowExceptionWhenParameterDuplicationAndStopOnError() {
        throw new SkipException("Skipping the test case");
//        List<String> doc = Arrays.asList(
//                "@param paramId aaa",
//                "@param paramId bbb");
//
//        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());
//        ThreadLocalContext.setClassName("Foo");
//        ThreadLocalContext.setMethodName("showParam");
//
//        assertThatThrownBy(() -> MethodJavaDoc.create(doc)).isInstanceOf(RamlProcessingException.class);
    }

    @Test
    public void shouldWaringWhenParameterDuplication() {
        throw new SkipException("Skipping the test case");
//        List<String> doc = Arrays.asList(
//                "@param paramId aaa",
//                "@param paramId bbb");
//
//        MessagerMock messager = new MessagerMock();
//        ThreadLocalContext.setMessager(messager);
//        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(false).build());
//        ThreadLocalContext.setClassName("Foo");
//        ThreadLocalContext.setMethodName("showParam");
//
//        Map<String, TagParam> params = MethodJavaDoc.create(doc).getParams();
//        assertThat(params).hasSize(1);
//        assertThat(params.values("paramId").getText()).isEqualTo("aaa");
//        assertThat(messager.getMessage()).isNotEmpty();
    }

    @Test(groups = "ambiguousImportClassDefinition")
    public void shouldIgnoreWhenClassNamesIsBlank() {
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());

        ProblemResolver.ambiguousImportClassDefinition(null, "className", Collections.emptyList(), "candidate");
        ProblemResolver.ambiguousImportClassDefinition("", "className", Collections.emptyList(), "candidate");
        ProblemResolver.ambiguousImportClassDefinition("  ", "className", Collections.emptyList(), "candidate");
        ProblemResolver.ambiguousImportClassDefinition("className", null, Collections.emptyList(), "candidate");
        ProblemResolver.ambiguousImportClassDefinition("className", "", Collections.emptyList(), "candidate");
        ProblemResolver.ambiguousImportClassDefinition("className", "   ", Collections.emptyList(), "candidate");
    }

    @Test(groups = "ambiguousImportClassDefinition")
    public void shouldThrowExceptionWhenMultipleCandidatesFound() {
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());
        assertThatThrownBy(() -> ProblemResolver.ambiguousImportClassDefinition("aaa", "bbb", Arrays.asList("one", "two"), "ddd"));
    }

    @Test(groups = "ambiguousImportClassDefinition")
    public void shouldPrintMessageWhenMultipleCandidatesFoundAndNoStopOnError() {
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(false).build());
        ProblemResolver.ambiguousImportClassDefinition("aaa", "bbb", Arrays.asList("one", "two"), "ddd");
        assertThat(((MessagerMock)ThreadLocalContext.getMessager()).getMessage()).isNotEmpty();
    }

    @Test(groups = "paramDefaultIsNotInEnum")
    public void shouldIgnoreWhenDefaultAndEnumNotSet() {
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());

        ProblemResolver.paramDefaultIsNotInEnum(null, null);
        ProblemResolver.paramDefaultIsNotInEnum(null, "anEnum");
        ProblemResolver.paramDefaultIsNotInEnum("def", null);
    }

    @Test(groups = "paramDefaultIsNotInEnum")
    public void shouldThrowExceptionWhenDefaultIsNotInEnum() {
        ThreadLocalContext.setClassName("Foo");
        ThreadLocalContext.setMethodName("getParam");
        ThreadLocalContext.setParamName("param");
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());
        assertThatThrownBy(() -> ProblemResolver.paramDefaultIsNotInEnum("aaa", "bbb")).isInstanceOf(RamlProcessingException.class);
    }

    @Test(groups = "paramDefaultIsNotInEnum")
    public void shouldPrintMessageWhenDefaultIsNotInEnum() {
        ThreadLocalContext.setClassName("Foo");
        ThreadLocalContext.setMethodName("getParam");
        ThreadLocalContext.setParamName("param");
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(false).build());

        ProblemResolver.paramDefaultIsNotInEnum("aaa", "bbb");
        assertThat(((MessagerMock)ThreadLocalContext.getMessager()).getMessage()).isNotEmpty();
    }

    @Test(groups = "paramEnumConstantIsNotMatchPattern")
    public void shouldIgnoreWhenEnumAndPatterNotSet() {
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());

        ProblemResolver.paramEnumConstantIsNotMatchPattern(null, null);
        ProblemResolver.paramEnumConstantIsNotMatchPattern("", null);
        ProblemResolver.paramEnumConstantIsNotMatchPattern(null, "");
        ProblemResolver.paramEnumConstantIsNotMatchPattern("aaa", null);
        ProblemResolver.paramEnumConstantIsNotMatchPattern(null, "bbb");
        ProblemResolver.paramEnumConstantIsNotMatchPattern("aaa", "");
        ProblemResolver.paramEnumConstantIsNotMatchPattern("", "bbb");
    }

    @Test(groups = "paramEnumConstantIsNotMatchPattern")
    public void shouldThrowExceptionWhenEnumConstantIsNotMatchPattern() {
        ThreadLocalContext.setClassName("Foo");
        ThreadLocalContext.setMethodName("getParam");
        ThreadLocalContext.setParamName("param");
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());
        assertThatThrownBy(() -> ProblemResolver.paramEnumConstantIsNotMatchPattern("aaa", "bbb")).isInstanceOf(RamlProcessingException.class);
    }

    @Test(groups = "paramEnumConstantIsNotMatchPattern")
    public void shouldPrintMessageWhenEnumConstantIsNotMatchPattern() {
        ThreadLocalContext.setClassName("Foo");
        ThreadLocalContext.setMethodName("getParam");
        ThreadLocalContext.setParamName("param");
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(false).build());

        ProblemResolver.paramEnumConstantIsNotMatchPattern("aaa", "bbb");
        assertThat(((MessagerMock)ThreadLocalContext.getMessager()).getMessage()).isNotEmpty();
    }

    @Test(groups = "paramEnumConstantDuplication")
    public void shouldIgnoreWhenEnumNotSet() {
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());

        ProblemResolver.paramEnumConstantDuplication(null);
        ProblemResolver.paramEnumConstantDuplication("");
    }

    @Test(groups = "paramEnumConstantDuplication")
    public void shouldThrowExceptionWhenParamEnumConstantDuplication() {
        ThreadLocalContext.setClassName("Foo");
        ThreadLocalContext.setMethodName("getParam");
        ThreadLocalContext.setParamName("param");
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());
        assertThatThrownBy(() -> ProblemResolver.paramEnumConstantDuplication("aaa")).isInstanceOf(RamlProcessingException.class);
    }

    @Test(groups = "paramEnumConstantDuplication")
    public void shouldPrintMessageWhenParamEnumConstantDuplication() {
        ThreadLocalContext.setClassName("Foo");
        ThreadLocalContext.setMethodName("getParam");
        ThreadLocalContext.setParamName("param");
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(false).build());

        ProblemResolver.paramEnumConstantDuplication("aaa");
        assertThat(((MessagerMock)ThreadLocalContext.getMessager()).getMessage()).isNotEmpty();
    }

    @Test(groups = "paramEnumForNotStringType")
    public void shouldIgnoreWhenTypeNotSet() {
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());

        ProblemResolver.paramEnumForNotStringType(null);
        ProblemResolver.paramEnumForNotStringType("");
    }

    @Test(groups = "paramEnumForNotStringType")
    public void shouldThrowExceptionWhenParamEnumForNotStringType() {
        ThreadLocalContext.setClassName("Foo");
        ThreadLocalContext.setMethodName("getParam");
        ThreadLocalContext.setParamName("param");
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());
        assertThatThrownBy(() -> ProblemResolver.paramEnumForNotStringType("aaa")).isInstanceOf(RamlProcessingException.class);
    }

    @Test(groups = "paramEnumForNotStringType")
    public void shouldPrintMessageWhenParamEnumForNotStringType() {
        ThreadLocalContext.setClassName("Foo");
        ThreadLocalContext.setMethodName("getParam");
        ThreadLocalContext.setParamName("param");
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(false).build());

        ProblemResolver.paramEnumForNotStringType("aaa");
        assertThat(((MessagerMock)ThreadLocalContext.getMessager()).getMessage()).isNotEmpty();
    }

    @Test(groups = "mediaTypeNotDefined")
    public void shouldIgnoreWhenMediaTypeIsNotSet() {
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());
        ProblemResolver.mediaTypeNotDefined("aaa");
    }

    @Test(groups = "mediaTypeNotDefined")
    public void shouldThrowExceptionWhenMediaTypeNotDefined() {
        ThreadLocalContext.setClassName("Foo");
        ThreadLocalContext.setMethodName("getParam1");
        ThreadLocalContext.setParamName("param1");
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());
        assertThatThrownBy(ProblemResolver::mediaTypeNotDefined).isInstanceOf(RamlProcessingException.class);
    }

    @Test(groups = "mediaTypeNotDefined")
    public void shouldPrintMessageWhenMediaTypeNotDefined() {
        ThreadLocalContext.setClassName("Foo");
        ThreadLocalContext.setMethodName("getParam");
        ThreadLocalContext.setParamName("param");
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(false).build());

        ProblemResolver.mediaTypeNotDefined();
        assertThat(((MessagerMock)ThreadLocalContext.getMessager()).getMessage()).isNotEmpty();
    }
}
