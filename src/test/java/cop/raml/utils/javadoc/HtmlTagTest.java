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

import org.testng.annotations.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author Oleg Cherednik
 * @since 23.12.2016
 */
public class HtmlTagTest {
    @Test
    public void testConstantAmount() {
        assertThat(HtmlTag.values()).hasSize(2);
    }

    @Test
    public void testBlankString() {
        for (HtmlTag tag : HtmlTag.values()) {
            assertThat(tag.is(null)).isFalse();
            assertThat(tag.is("")).isFalse();
            assertThat(tag.is("   ")).isFalse();

            assertThat(tag.replace(null)).isNull();
            assertThat(tag.replace("")).isEmpty();
            assertThat(tag.replace("   ")).isEqualTo("   ");
        }
    }

    @Test
    public void testForNoTagText() {
        for (HtmlTag tag : HtmlTag.values()) {
            assertThat(tag.is("text without tag")).isFalse();
            assertThat(tag.replace("text without tag")).isEqualTo("text without tag");
        }
    }

    @Test
    public void testIs() {
        assertThat(HtmlTag.P.is("<p>")).isTrue();
        assertThat(HtmlTag.P.is("<p/>")).isTrue();
        assertThat(HtmlTag.P.is("<br>")).isFalse();

        assertThat(HtmlTag.BR.is("<br>")).isTrue();
        assertThat(HtmlTag.BR.is("<br/>")).isTrue();
        assertThat(HtmlTag.BR.is("<p>")).isFalse();
    }

    @Test
    public void testReplace() {
        assertThat(HtmlTag.P.replace("aaa<p>ccc")).isEqualTo("aaa\n\nccc");
        assertThat(HtmlTag.P.replace("aaa<p/>ccc")).isEqualTo("aaa\n\nccc");

        assertThat(HtmlTag.BR.replace("aaa<br>ccc")).isEqualTo("aaa\nccc");
        assertThat(HtmlTag.BR.replace("aaa<br/>ccc")).isEqualTo("aaa\nccc");
    }

    @Test
    public void testReplaceAll() {
        assertThat(HtmlTag.replaceAll("aaa<p>bbb<br>ccc")).isEqualTo("aaa\n\nbbb\nccc");
        assertThat(HtmlTag.replaceAll("aaa<p/>bbb<br/>ccc")).isEqualTo("aaa\n\nbbb\nccc");
    }
}
