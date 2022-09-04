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
import cop.raml.mocks.MockUtils;
import cop.raml.mocks.ProblemVariableElement;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.lang.model.element.ElementKind;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oleg Cherednik
 * @since 20.12.2016
 */
@SuppressWarnings("serial")
public class UtilsTest {
    @BeforeClass
    private static void coberturaFix() {
        ReflectionUtils.invokeConstructor(Utils.class);
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
    public void testConstant() {
        assertThat(Utils.COLON.toString()).isEqualTo("\\s*,\\s*");
        assertThat(Utils.SEMICOLON.toString()).isEqualTo("\\s*;\\s*");
        assertThat(Utils.APPLICATION_JSON).isEqualTo("application/json");
        assertThat(Utils.APPLICATION_XML).isEqualTo("application/xml");
        assertThat(Utils.MULTIPART_FORM_DATA).isEqualTo("multipart/form-data");
        assertThat(Utils.TEXT_PLAIN).isEqualTo("text/plain");
    }

    @Test
    public void testArrApplicationJson() {
        assertThat(Utils.arrApplicationJson()).containsOnly("application/json");
    }

    @Test
    public void testArrMultipartFormData() {
        assertThat(Utils.arrMultipartFormData()).containsOnly("multipart/form-data");
    }

    @Test
    public void testComma() {
        assertThat(Utils.comma(null).toString()).isEmpty();
        assertThat(Utils.comma(new StringBuilder()).toString()).isEmpty();
        assertThat(Utils.comma(new StringBuilder("aaa")).toString()).isEqualTo("aaa, ");
    }

    @Test
    public void testJoin() {
        assertThat(Utils.join("aaa/bbb", null)).isEqualTo("aaa/bbb");
        assertThat(Utils.join("aaa/bbb", "")).isEqualTo("aaa/bbb");
        assertThat(Utils.join("aaa/bbb", "   ")).isEqualTo("aaa/bbb");
        assertThat(Utils.join("///aaa/bbb///", null)).isEqualTo("///aaa/bbb");

        assertThat(Utils.join(null, "aaa/bbb")).isEqualTo("aaa/bbb");
        assertThat(Utils.join("", "aaa/bbb")).isEqualTo("aaa/bbb");
        assertThat(Utils.join("   ", "aaa/bbb")).isEqualTo("aaa/bbb");
        assertThat(Utils.join(null, "///aaa/bbb///")).isEqualTo("aaa/bbb");

        assertThat(Utils.join("aaa/bbb///", "///ccc/ddd")).isEqualTo("aaa/bbb/ccc/ddd");
        assertThat(Utils.join("///aaa/bbb///", "///ccc/ddd///")).isEqualTo("///aaa/bbb/ccc/ddd");
        assertThat(Utils.join("aaa/bbb", "///")).isEqualTo("aaa/bbb");

        assertThat(Utils.join("aaa/bbb", "ccc/ddd")).isEqualTo("aaa/bbb/ccc/ddd");
        assertThat(Utils.join("   /aaa/bbb/   ", " /ccc/ddd/   ")).isEqualTo("/aaa/bbb/ccc/ddd");
    }

    @Test
    public void testRemoveLeadingSlashes() throws Exception {
        assertThat(removeLeadingSlashes(null)).isNull();
        assertThat(removeLeadingSlashes("")).isSameAs(StringUtils.EMPTY);
        assertThat(removeLeadingSlashes("   ")).isSameAs(StringUtils.EMPTY);
        assertThat(removeLeadingSlashes("///")).isEmpty();
        assertThat(removeLeadingSlashes("///aaa///")).isEqualTo("aaa///");
        assertThat(removeLeadingSlashes("///aaa")).isEqualTo("aaa");
    }

    @Test
    public void testRemoveTrailingSlashes() throws Exception {
        assertThat(removeTrailingSlashes(null)).isNull();
        assertThat(removeTrailingSlashes("")).isSameAs(StringUtils.EMPTY);
        assertThat(removeTrailingSlashes("   ")).isSameAs(StringUtils.EMPTY);
        assertThat(removeTrailingSlashes("///")).isEmpty();
        assertThat(removeTrailingSlashes("///aaa///")).isEqualTo("///aaa");
        assertThat(removeTrailingSlashes("aaa///")).isEqualTo("aaa");
    }

    @Test
    public void testGetParams() {
        assertThat(Utils.getParams(null)).isSameAs(Collections.emptySet());
        assertThat(Utils.getParams("")).isSameAs(Collections.emptySet());
        assertThat(Utils.getParams("   ")).isSameAs(Collections.emptySet());
        assertThat(Utils.getParams("simple string")).isSameAs(Collections.emptySet());
        assertThat(Utils.getParams("/project/{projectId}/order/{orderId}/validation")).containsExactly("projectId", "orderId");
    }

    @Test
    public void testToEnumStr() throws Exception {
        assertThat(Utils.toEnumStr("[aa]", null)).isEqualTo("[aa]");
        assertThat(Utils.toEnumStr(null, null)).isNull();
        assertThat(Utils.toEnumStr("", null)).isNull();
        assertThat(Utils.toEnumStr("[]", null)).isNull();
        assertThat(Utils.toEnumStr(null, MockUtils.createVariable("count", MockUtils.Count.class))).isEqualTo("[ONE,TWO,THREE]");
        assertThat(Utils.toEnumStr(null, MockUtils.createVariable("count", int.class))).isNull();
        assertThat(Utils.toEnumStr(null, MockUtils.createVariable("count", EmptyEnum.class))).isNull();
        assertThat(Utils.toEnumStr(null, new ProblemVariableElement(ElementKind.OTHER))).isNull();
    }

    @Test
    public void testToEnumStrSet() {
        assertThat(Utils.toEnumStr((Set<String>)null)).isNull();
        assertThat(Utils.toEnumStr(Collections.emptySet())).isNull();
        assertThat(Utils.toEnumStr(new LinkedHashSet<String>() {{
            add("one");
            add("two");
        }})).isEqualTo("[one,two]");
    }

    @Test
    public void testToEnumStrArray() {
        assertThat(Utils.toEnumStr()).isNull();
        assertThat(Utils.toEnumStr(null, "", "  ")).isNull();
        assertThat(Utils.toEnumStr("one")).isEqualTo("[one]");
        assertThat(Utils.toEnumStr("one", "two")).isEqualTo("[one,two]");
        assertThat(Utils.toEnumStr("two", "one")).isEqualTo("[two,one]");
        assertThat(Utils.toEnumStr("one", "one", "two", "two")).isEqualTo("[one,two]");
        assertThat(Utils.toEnumStr("two", "", "  ", null, "one")).isEqualTo("[two,one]");
        assertThat(Utils.toEnumStr("10", "2", "11", "1")).isEqualTo("[10,2,11,1]");
    }

    @Test
    public void testAsSet() {
        assertThat(Utils.asSet()).isSameAs(Collections.emptySet());
        assertThat(Utils.asSet(null, "", "   ")).isEmpty();
        assertThat(Utils.asSet("one")).containsExactly("one");
        assertThat(Utils.asSet("one", "two")).containsExactly("one", "two");
        assertThat(Utils.asSet("  one  ", "  two  ")).containsExactly("one", "two");
        assertThat(Utils.asSet("two", "one")).containsExactly("two", "one");
        assertThat(Utils.asSet("one", "one", "two", "two")).containsExactly("one", "two");
        assertThat(Utils.asSet("two", "", "  ", null, "one")).containsExactly("two", "one");
        assertThat(Utils.asSet("10", "2", "11", "1")).containsExactly("10", "2", "11", "1");
    }

    @Test
    public void shouldReturnEmptyLinesForBlankDoc() {
        assertThat(Utils.toLineList(null)).isEmpty();
        assertThat(Utils.toLineList("")).isEmpty();
        assertThat(Utils.toLineList("  ")).isEmpty();
    }

    @Test
    public void shouldUseNewLineAsSeparator() {
        String doc = TestUtils.joinStrings(
                "one",
                "two",
                "",
                "three",
                "four");
        List<String> lines = Utils.toLineList(doc);

        assertThat(lines).hasSize(5);
        assertThat(lines.get(0)).isEqualTo("one");
        assertThat(lines.get(1)).isEqualTo("two");
        assertThat(lines.get(2)).isEmpty();
        assertThat(lines.get(3)).isEqualTo("three");
        assertThat(lines.get(4)).isEqualTo("four");
    }

    @Test
    public void shouldUsePAsSeparator() {
        String doc = TestUtils.joinStrings(
                "one",
                "two",
                "<p>",
                "three",
                "four");
        List<String> lines = Utils.toLineList(doc);

        assertThat(lines).hasSize(5);
        assertThat(lines.get(0)).isEqualTo("one");
        assertThat(lines.get(1)).isEqualTo("two");
        assertThat(lines.get(2)).isEmpty();
        assertThat(lines.get(3)).isEqualTo("three");
        assertThat(lines.get(4)).isEqualTo("four");
    }

    @Test
    public void shouldReadInlineNewLine() {
        List<String> lines = Utils.toLineList("one<br>two<p>three");

        assertThat(lines).hasSize(4);
        assertThat(lines.get(0)).isEqualTo("one");
        assertThat(lines.get(1)).isEqualTo("two");
        assertThat(lines.get(2)).isEmpty();
        assertThat(lines.get(3)).isEqualTo("three");
    }

    @Test
    public void shouldReadEndingTagsForNewLine() {
        String doc = TestUtils.joinStrings(
                "one",
                "two",
                "<p/>",
                "three<br/>four",
                "five");
        List<String> lines = Utils.toLineList(doc);

        assertThat(lines).hasSize(6);
        assertThat(lines.get(0)).isEqualTo("one");
        assertThat(lines.get(1)).isEqualTo("two");
        assertThat(lines.get(2)).isEmpty();
        assertThat(lines.get(3)).isEqualTo("three");
        assertThat(lines.get(4)).isEqualTo("four");
        assertThat(lines.get(5)).isEqualTo("five");
    }

    @Test
    public void shouldTreatBrTagLineAsNewLine() {
        String doc = TestUtils.joinStrings(
                "one",
                "<br>",
                "two",
                "<br/>",
                "three");
        List<String> lines = Utils.toLineList(doc);

        assertThat(lines).hasSize(5);
        assertThat(lines.get(0)).isEqualTo("one");
        assertThat(lines.get(1)).isEmpty();
        assertThat(lines.get(2)).isEqualTo("two");
        assertThat(lines.get(3)).isEmpty();
        assertThat(lines.get(4)).isEqualTo("three");
    }

    @Test
    public void shouldReadMultipleEmptyLinesInTheMiddle() {
        String doc = TestUtils.joinStrings(
                "one",
                "<p>",
                "<br>",
                "two");
        List<String> lines = Utils.toLineList(doc);

        assertThat(lines).hasSize(4);
        assertThat(lines.get(0)).isEqualTo("one");
        assertThat(lines.get(1)).isEmpty();
        assertThat(lines.get(2)).isEmpty();
        assertThat(lines.get(3)).isEqualTo("two");
    }

    @Test
    public void shouldNotReadFirstEndLastEmptyLines() {
        String doc = TestUtils.joinStrings(
                "",
                "<p>",
                "one",
                "",
                "<p>",
                "two",
                "<p>",
                "");
        List<String> lines = Utils.toLineList(doc);

        assertThat(lines).hasSize(4);
        assertThat(lines.get(0)).isEqualTo("one");
        assertThat(lines.get(1)).isEmpty();
        assertThat(lines.get(2)).isEmpty();
        assertThat(lines.get(3)).isEqualTo("two");
    }

    @Test
    public void checkEmptyLineDoc() {
        String doc = TestUtils.joinStrings(
                "",
                "<p>",
                "",
                "<p>",
                "");
        assertThat(Utils.toLineList(doc)).isSameAs(Collections.emptyList());
    }

    @Test
    public void testOffs() {
        assertThat(Utils.offs(null, 2, true)).isNull();
        assertThat(Utils.offs("", 2, true)).isEmpty();
        assertThat(Utils.offs("   ", 2, true)).isEqualTo("   ");

        assertThat(Utils.offs("aaa", -2, true)).isEqualTo("aaa");
        assertThat(Utils.offs(TestUtils.joinStrings("aaa", "bbb", "ccc"), 2, true))
                .isEqualTo(TestUtils.joinStrings("  aaa", "  bbb", "  ccc"));
        assertThat(Utils.offs(TestUtils.joinStrings("  aaa", "  bbb", "  ccc"), 2, true))
                .isEqualTo(TestUtils.joinStrings("    aaa", "    bbb", "    ccc"));
        assertThat(Utils.offs(TestUtils.joinStrings("    aaa", "    bbb", "    ccc"), 2, false))
                .isEqualTo(TestUtils.joinStrings("  aaa", "  bbb", "  ccc"));
    }

    @Test
    public void shouldNotAddOffsetWhenLineEmpty() {
        assertThat(Utils.offs(TestUtils.joinStrings("aaa", "", "bbb", "", "ccc"), 2, false))
                .isEqualTo(TestUtils.joinStrings("  aaa", "", "  bbb", "", "  ccc"));
        assertThat(Utils.offs(TestUtils.joinStrings("aaa", "", "bbb", "", "ccc"), 2, true))
                .isEqualTo(TestUtils.joinStrings("  aaa", "", "  bbb", "", "  ccc"));
    }

    @Test
    public void testSplitPath() {
        assertThat(Utils.splitPath(null)).isNull();
        assertThat(Utils.splitPath("")).isSameAs(ArrayUtils.EMPTY_STRING_ARRAY);
        assertThat(Utils.splitPath("  ")).isSameAs(ArrayUtils.EMPTY_STRING_ARRAY);
        assertThat(Utils.splitPath("aaa")).containsExactly("aaa");
        assertThat(Utils.splitPath("aaa/bbb")).containsExactly("aaa", "bbb");
    }

    @Test
    public void testSplitLine() {
        assertThat(Utils.splitLine(null)).isNull();
        assertThat(Utils.splitLine("")).isSameAs(ArrayUtils.EMPTY_STRING_ARRAY);
        assertThat(Utils.splitLine("   ")).containsExactly("   ");
        assertThat(Utils.splitLine("aaa\nbbb\nccc")).containsExactly("aaa", "bbb", "ccc");
        assertThat(Utils.splitLine("   aaa   \n   bbb   \n   ccc   ")).containsExactly("   aaa   ", "   bbb   ", "   ccc   ");
        assertThat(Utils.splitLine("aaa\rbbb\rccc")).containsExactly("aaa", "bbb", "ccc");
        assertThat(Utils.splitLine("aaa\r\nbbb\r\nccc")).containsExactly("aaa", "bbb", "ccc");
    }

    @Test
    public void testDefOnBlank() {
        assertThat(Utils.defOnBlank(null, null)).isNull();
        assertThat(Utils.defOnBlank("", "")).isNull();
        assertThat(Utils.defOnBlank("", "aaa")).isEqualTo("aaa");
        assertThat(Utils.defOnBlank("aaa", "bbb")).isEqualTo("aaa");
    }

    @Test
    public void testTrimLine() {
        assertThat(Utils.trimLine(null)).isNull();
        assertThat(Utils.trimLine("")).isEmpty();
        assertThat(Utils.trimLine("   ")).isEmpty();
        assertThat(Utils.trimLine("   aaa   ")).isEqualTo("aaa");
        assertThat(Utils.trimLine("   aaa   \n   bbb   \n   ccc   ")).isEqualTo("aaa\nbbb\nccc");
    }

    // ========== data ==========

    private enum EmptyEnum {
    }

    // ========== static ==========

    private static String removeLeadingSlashes(String str) throws Exception {
        return ReflectionUtils.invokeStaticMethod(Utils.class, "removeLeadingSlashes", String.class, str);
    }

    private static String removeTrailingSlashes(String str) throws Exception {
        return ReflectionUtils.invokeStaticMethod(Utils.class, "removeTrailingSlashes", String.class, str);
    }
}
