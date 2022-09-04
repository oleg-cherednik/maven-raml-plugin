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
package cop.raml.processor;

import cop.raml.TestUtils;
import cop.raml.processor.exceptions.RamlProcessingException;
import cop.raml.utils.ThreadLocalContext;
import cop.raml.mocks.MockUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

/**
 * @author Oleg Cherednik
 * @since 18.01.2017
 */
@SuppressWarnings("InstanceMethodNamingConvention")
public class ParameterTest {
    private static final String NAME = "foo";

    private Parameter parameter;

    @BeforeMethod
    private void init() {
        parameter = new Parameter(NAME);
    }

    @Test
    public void testGetName() {
        assertThat(parameter.getName()).isEqualTo(NAME);
    }

    @Test
    public void testDisplayName() {
        assertThat(parameter.getDisplayName()).isNull();
        assertThat(parameter.getDisplayName(2)).isNull();

        parameter.setDisplayName("display name");

        assertThat(parameter.getDisplayName()).isEqualTo("display name");
        assertThat(parameter.getDisplayName(2)).isEqualTo("  display name");
    }

    @Test
    public void testSetGetExample() {
        parameter.setExample(null);
        assertThat(parameter.getExample()).isNull();
        assertThat(parameter.getExample(2)).isNull();

        parameter.setExample("");
        assertThat(parameter.getExample()).isEmpty();
        assertThat(parameter.getExample(2)).isEmpty();

        parameter.setExample("  ");
        assertThat(parameter.getExample()).isEqualTo("  ");
        assertThat(parameter.getExample(2)).isEqualTo("  ");

        parameter.setExample("example");
        assertThat(parameter.getExample()).isEqualTo("example");
        assertThat(parameter.getExample(2)).isEqualTo("  example");
    }

    @Test
    public void testIsSingleLine() {
        assertThat(parameter.getExample()).isNull();
        assertThat(parameter.isSingleLine()).isTrue();

        parameter.setExample("one");
        assertThat(parameter.isSingleLine()).isTrue();

        parameter.setExample(TestUtils.joinStrings("one", "two"));
        assertThat(parameter.isSingleLine()).isFalse();
    }

    @Test
    public void testDescription() {
        for (String description : Arrays.asList(null, "", "  ")) {
            parameter.setDescription(description);
            assertThat(parameter.getDescription()).isNull();
            assertThat(parameter.getDescription(2)).isNull();
        }

        parameter.setDescription("description");
        assertThat(parameter.getDescription()).isEqualTo("description");
        assertThat(parameter.getDescription(2)).isEqualTo("  description");
    }

    @Test(groups = "type")
    public void shouldSetTypeWhenTypeIsValid() throws Exception {
        assertThat(parameter.getType()).isEqualTo(Parameter.Type.STRING.getId());

        parameter.setType(null);
        assertThat(parameter.getType()).isEqualTo(Parameter.Type.STRING.getId());

        parameter.setType(MockUtils.createElement(int.class).asType());
        assertThat(parameter.getType()).isEqualTo(Parameter.Type.INTEGER.getId());
    }

    @Test(groups = "type")
    public void shouldThrowExceptionWhenSetEnumForNotStringType() {
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());
        ThreadLocalContext.setClassName("class");
        ThreadLocalContext.setMethodName("method");
        ThreadLocalContext.setParamName("param");

        parameter.setEnum("aaa,bbb");
        assertThatThrownBy(() -> parameter.setType(MockUtils.createElement(int.class).asType())).isExactlyInstanceOf(RamlProcessingException.class);
        assertThatThrownBy(() -> parameter.setType(MockUtils.createElement(File.class).asType())).isExactlyInstanceOf(RamlProcessingException.class);
        assertThatThrownBy(() -> parameter.setType(MockUtils.createElement(Date.class).asType())).isExactlyInstanceOf(RamlProcessingException.class);
        assertThatThrownBy(() -> parameter.setType(MockUtils.createElement(boolean.class).asType()))
                .isExactlyInstanceOf(RamlProcessingException.class);
    }

    @Test
    public void testRequired() {
        assertThat(parameter.isRequired()).isTrue();
        parameter.setRequired(false);
        assertThat(parameter.isRequired()).isFalse();
    }

    @Test(groups = "default")
    public void shouldSetDefaultWhenDefaultIsValid() {
        assertThat(parameter.getDef()).isNull();

        parameter.setDefault(null);
        assertThat(parameter.getDef()).isNull();

        parameter.setDefault("one");
        assertThat(parameter.getDef()).isEqualTo("one");
    }

    @Test(groups = "enum")
    public void shouldSetEnumWhenEnumIsValid() {
        assertThat(parameter.getEnum()).isNull();

        parameter.setEnum(null);
        assertThat(parameter.getEnum()).isNull();

        parameter.setEnum("");
        assertThat(parameter.getEnum()).isNull();

        parameter.setEnum("  ");
        assertThat(parameter.getEnum()).isNull();

        parameter.setEnum("one");
        assertThat(parameter.getEnum()).isEqualTo("[one]");

        parameter.setEnum("one,two");
        assertThat(parameter.getEnum()).isEqualTo("[one,two]");

        parameter.setEnum("  one  ,  two  ");
        assertThat(parameter.getEnum()).isEqualTo("[one,two]");

        parameter.setEnum(null);
        assertThat(parameter.getEnum()).isNull();
    }

    @Test(groups = "enum")
    public void shouldThrowExceptionWhenDefaultIsNotInEnum() {
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());
        ThreadLocalContext.setClassName("class");
        ThreadLocalContext.setMethodName("method");
        ThreadLocalContext.setParamName("param");

        parameter.setDefault("one");
        parameter.setEnum("one,two");
        assertThat(parameter.getEnum()).isEqualTo("[one,two]");
        assertThatThrownBy(() -> parameter.setEnum("aaa,bbb")).isExactlyInstanceOf(RamlProcessingException.class);
    }

    @Test(groups = "enum")
    public void shouldThrowExceptionWhenEnumIsNotMatchPattern() {
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());
        ThreadLocalContext.setClassName("class");
        ThreadLocalContext.setMethodName("method");
        ThreadLocalContext.setParamName("param");

        parameter.setPattern("\\d+");
        parameter.setEnum("123,456");
        assertThat(parameter.getEnum()).isEqualTo("[123,456]");
        assertThatThrownBy(() -> parameter.setEnum("aaa,bbb")).isExactlyInstanceOf(RamlProcessingException.class);
    }

    @Test(groups = "enum")
    public void shouldThrowExceptionWhenEnumConstantDuplication() {
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());
        ThreadLocalContext.setClassName("class");
        ThreadLocalContext.setMethodName("method");
        ThreadLocalContext.setParamName("param");

        assertThatThrownBy(() -> parameter.setEnum("one,one,two")).isExactlyInstanceOf(RamlProcessingException.class);
    }

    @Test(groups = "enum")
    public void shouldThrowExceptionsWhenTypeIsNotString() throws Exception {
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());
        ThreadLocalContext.setClassName("class");
        ThreadLocalContext.setMethodName("method");
        ThreadLocalContext.setParamName("param");

        parameter.setType(MockUtils.createElement(int.class).asType());
        assertThatThrownBy(() -> parameter.setEnum("one,two")).isExactlyInstanceOf(RamlProcessingException.class);
    }

    @Test(groups = "Parameter.Type")
    public void testConstantAmount() throws Exception {
        assertThat(Parameter.Type.values()).hasSize(6);
    }

    @Test(groups = "Parameter.Type")
    public void testId() throws Exception {
        assertThat(Parameter.Type.STRING.getId()).isEqualTo("string");
        assertThat(Parameter.Type.INTEGER.getId()).isEqualTo("integer");
        assertThat(Parameter.Type.NUMBER.getId()).isEqualTo("number");
        assertThat(Parameter.Type.FILE.getId()).isEqualTo("file");
        assertThat(Parameter.Type.DATE.getId()).isEqualTo("date");
        assertThat(Parameter.Type.BOOLEAN.getId()).isEqualTo("boolean");
    }

    @Test(groups = "Parameter.Type")
    public void shouldRetrieveDefaultTypeWhenUnknownTypeMirror() throws Exception {
        assertThat(Parameter.Type.parseTypeMirror(null)).isSameAs(Parameter.Type.STRING);
        assertThat(Parameter.Type.parseTypeMirror(MockUtils.createElement(MockUtils.Count.class).asType())).isSameAs(Parameter.Type.STRING);
    }

    @Test(groups = "Parameter.Type")
    public void shouldRetrieveStringWhenStringTypeMirror() throws Exception {
        assertThat(Parameter.Type.parseTypeMirror(MockUtils.createElement(String.class).asType())).isSameAs(Parameter.Type.STRING);
    }

    @Test(groups = "Parameter.Type")
    public void shouldRetrieveIntegerWhenIntegerTypeMirror() throws Exception {
        assertThat(Parameter.Type.parseTypeMirror(MockUtils.createElement(int.class).asType())).isSameAs(Parameter.Type.INTEGER);
        assertThat(Parameter.Type.parseTypeMirror(MockUtils.createElement(Integer.class).asType())).isSameAs(Parameter.Type.INTEGER);
    }

    @Test(groups = "Parameter.Type")
    public void shouldRetrieveNumberWhenLongTypeMirror() throws Exception {
        assertThat(Parameter.Type.parseTypeMirror(MockUtils.createElement(long.class).asType())).isSameAs(Parameter.Type.NUMBER);
        assertThat(Parameter.Type.parseTypeMirror(MockUtils.createElement(Long.class).asType())).isSameAs(Parameter.Type.NUMBER);
    }

    @Test(groups = "Parameter.Type")
    public void shouldRetrieveNumberWhenDoubleTypeMirror() throws Exception {
        assertThat(Parameter.Type.parseTypeMirror(MockUtils.createElement(double.class).asType())).isSameAs(Parameter.Type.NUMBER);
        assertThat(Parameter.Type.parseTypeMirror(MockUtils.createElement(Double.class).asType())).isSameAs(Parameter.Type.NUMBER);
    }

    @Test(groups = "Parameter.Type")
    public void shouldRetrieveNumberWhenFloatTypeMirror() throws Exception {
        assertThat(Parameter.Type.parseTypeMirror(MockUtils.createElement(float.class).asType())).isSameAs(Parameter.Type.NUMBER);
        assertThat(Parameter.Type.parseTypeMirror(MockUtils.createElement(Float.class).asType())).isSameAs(Parameter.Type.NUMBER);
    }

    @Test(groups = "Parameter.Type")
    public void shouldRetrieveNumberWhenShortTypeMirror() throws Exception {
        assertThat(Parameter.Type.parseTypeMirror(MockUtils.createElement(short.class).asType())).isSameAs(Parameter.Type.NUMBER);
        assertThat(Parameter.Type.parseTypeMirror(MockUtils.createElement(Short.class).asType())).isSameAs(Parameter.Type.NUMBER);
    }

    @Test(groups = "Parameter.Type")
    public void shouldRetrieveNumberWhenByteTypeMirror() throws Exception {
        assertThat(Parameter.Type.parseTypeMirror(MockUtils.createElement(byte.class).asType())).isSameAs(Parameter.Type.NUMBER);
        assertThat(Parameter.Type.parseTypeMirror(MockUtils.createElement(Byte.class).asType())).isSameAs(Parameter.Type.NUMBER);
    }

    @Test(groups = "Parameter.Type")
    public void shouldRetrieveFileWhenFileTypeMirror() throws Exception {
        assertThat(Parameter.Type.parseTypeMirror(MockUtils.createElement(File.class).asType())).isSameAs(Parameter.Type.FILE);
    }

    @Test(groups = "Parameter.Type")
    public void shouldRetrieveDateWhenDateTypeMirror() throws Exception {
        assertThat(Parameter.Type.parseTypeMirror(MockUtils.createElement(Date.class).asType())).isSameAs(Parameter.Type.DATE);
    }

    @Test(groups = "Parameter.Type")
    public void shouldRetrieveBooleanWhenBooleanTypeMirror() throws Exception {
        assertThat(Parameter.Type.parseTypeMirror(MockUtils.createElement(boolean.class).asType())).isSameAs(Parameter.Type.BOOLEAN);
        assertThat(Parameter.Type.parseTypeMirror(MockUtils.createElement(Boolean.class).asType())).isSameAs(Parameter.Type.BOOLEAN);
    }

    @Test
    public void testMin() {
        assertThat(parameter.getMin()).isNull();
        parameter.setMin("3");
        assertThat(parameter.getMin()).isEqualTo("3");
    }

    @Test
    public void testMax() {
        assertThat(parameter.getMax()).isNull();
        parameter.setMax("3");
        assertThat(parameter.getMax()).isEqualTo("3");
    }

    @Test
    public void testToString() {
        parameter.setDescription(null);
        assertThat(parameter.toString()).isEqualTo(NAME);
        parameter.setDescription("");
        assertThat(parameter.toString()).isEqualTo(NAME);
        parameter.setDescription("  ");
        assertThat(parameter.toString()).isEqualTo(NAME);
        parameter.setDescription("aaa");
        assertThat(parameter.toString()).isEqualTo(String.format("%s [aaa]", NAME));
    }

    @Test
    public void testPattern() {
        assertThat(parameter.getPattern()).isNull();
        parameter.setPattern("pattern");
        assertThat(parameter.getPattern()).isEqualTo("pattern");
    }
}
