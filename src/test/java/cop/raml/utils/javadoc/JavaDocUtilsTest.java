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
package cop.raml.utils.javadoc;

import cop.raml.TestUtils;
import cop.raml.utils.ReflectionUtils;
import cop.raml.mocks.ElementMock;
import cop.raml.mocks.ProcessingEnvironmentMock;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oleg Cherednik
 * @since 12.12.2016
 */
public class JavaDocUtilsTest {
    @BeforeClass
    private static void coberturaFix() {
        ReflectionUtils.invokeConstructor(JavaDocUtils.class);
    }

    @Test
    public void testGetDocComment() {
        ProcessingEnvironmentMock processingEnv = new ProcessingEnvironmentMock();
        ElementMock element = new ElementMock();

        assertThat(JavaDocUtils.getDocComment(null, null)).isNull();
        assertThat(JavaDocUtils.getDocComment(null, processingEnv)).isNull();
        assertThat(JavaDocUtils.getDocComment(element, processingEnv)).isNull();

        element.setDocComment("");
        assertThat(JavaDocUtils.getDocComment(element, processingEnv)).isNull();

        element.setDocComment("   ");
        assertThat(JavaDocUtils.getDocComment(element, processingEnv)).isNull();

        element.setDocComment("aaa");
        assertThat(JavaDocUtils.getDocComment(element, processingEnv)).isEqualTo("aaa");

        element.setDocComment("   aaa   ");
        assertThat(JavaDocUtils.getDocComment(element, processingEnv)).isEqualTo("aaa");
    }

    @Test
    public void testGetText() {
        List<String> doc = Arrays.asList(
                "Retrieve project with given {@code projectId}",
                "<p>",
                "Main purpose of this text is @param to check * multi * line comment<br>",
                "with different @return new line symbols.",
                "",
                "@param projectId project id",
                "@param modelId model id",
                "   this is some additional comment to the param modelId",
                "@return {@link Project} object",
                "@throws GeneralException in case of any error");

        assertThat(JavaDocUtils.getText(doc)).isEqualTo(TestUtils.joinStrings(
                "Retrieve project with given {@code projectId}",
                "",
                "",
                "Main purpose of this text is @param to check * multi * line comment",
                "with different @return new line symbols."));
    }

    @Test
    public void shouldIgnoreFirstAndLastEmptyLine() {
        List<String> doc = Arrays.asList(
                "",
                "line1",
                "",
                "",
                "line2",
                "");

        String expected = TestUtils.joinStrings(
                "line1",
                "",
                "",
                "line2");
        String actual = JavaDocUtils.getText(doc);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testClearMacros() {
        assertThat(JavaDocUtils.clearMacros(null)).isNull();
        assertThat(JavaDocUtils.clearMacros("")).isNull();
        assertThat(JavaDocUtils.clearMacros("   ")).isNull();

        String doc = TestUtils.joinStrings(
                "This is a test",
                "<p>",
                "more text<br>",
                "{@name aaa} {@optional}",
                "last line");
        assertThat(JavaDocUtils.clearMacros(doc)).isEqualTo(TestUtils.joinStrings(
                "This is a test",
                "",
                "",
                "more text",
                "last line"));

        assertThat(JavaDocUtils.clearMacros(" {@name aaa} {@optional} ")).isNull();
    }

    @Test
    public void testIsArray() {
        assertThat(JavaDocUtils.isArray(null)).isFalse();
        assertThat(JavaDocUtils.isArray("")).isFalse();
        assertThat(JavaDocUtils.isArray("   ")).isFalse();
        assertThat(JavaDocUtils.isArray("this is a text")).isFalse();
        assertThat(JavaDocUtils.isArray("this is {@type number} text")).isFalse();
        assertThat(JavaDocUtils.isArray("this is {@type arr} text")).isTrue();
    }
}
