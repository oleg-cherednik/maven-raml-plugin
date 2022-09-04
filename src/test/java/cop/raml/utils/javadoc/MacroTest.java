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
import cop.raml.processor.Config;
import cop.raml.processor.exceptions.RamlProcessingException;
import cop.raml.utils.ThreadLocalContext;
import cop.raml.mocks.MessagerMock;
import cop.raml.mocks.MockUtils;
import org.assertj.core.api.Assertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author Oleg Cherednik
 * @since 22.12.2016
 */
@SuppressWarnings("InstanceMethodNamingConvention")
public class MacroTest {
    @BeforeMethod
    private void initContext() {
        MockUtils.initThreadLocalContext();
    }

    @AfterMethod
    private void clearContext() {
        ThreadLocalContext.remove();
    }

    @Test
    public void testConstantAmount() {
        assertThat(Macro.values()).hasSize(9);
    }

    @Test
    public void testBlankString() {
        for (Macro macro : Macro.values()) {
            assertThat(macro.get(null)).isNull();
            assertThat(macro.get("")).isNull();
            assertThat(macro.get("   ")).isNull();

            assertThat(macro.remove(null)).isNull();
            assertThat(macro.remove("")).isEmpty();
            assertThat(macro.remove("   ")).isEqualTo("   ");

            assertThat(macro.exists(null)).isFalse();
            assertThat(macro.exists("")).isFalse();
            assertThat(macro.exists("   ")).isFalse();
        }
    }

    @Test
    public void testForNoMacroText() {
        for (Macro macro : Macro.values()) {
            assertThat(macro.get("text without macro")).isNull();
            assertThat(macro.remove("text without macro")).isEqualTo("text without macro");
            assertThat(macro.exists("text without macro")).isFalse();
        }
    }

    @Test
    public void testId() {
        assertThat(Macro.DEFAULT.getId()).isEqualTo("@default");
        assertThat(Macro.ENUM.getId()).isEqualTo("@enum");
        assertThat(Macro.EXAMPLE.getId()).isEqualTo("@example");
        assertThat(Macro.NAME.getId()).isEqualTo("@name");
        assertThat(Macro.PATTERN.getId()).isEqualTo("@pattern");
        assertThat(Macro.STATUS.getId()).isEqualTo("@status");
        assertThat(Macro.TYPE.getId()).isEqualTo("@type");
        assertThat(Macro.URL.getId()).isEqualTo("@url");
    }

    @Test
    public void testGet() {
        assertThat(Macro.DEFAULT.get("aaa {@default bbb} ccc")).isEqualTo("bbb");
        assertThat(Macro.ENUM.get("aaa {@enum [bbb;ccc;ddd]} eee")).isEqualTo("[bbb,ccc,ddd]");
        assertThat(Macro.ENUM.get("aaa {@enum [bbb,ccc,ddd]} eee")).isEqualTo("[bbb,ccc,ddd]");
        assertThat(Macro.EXAMPLE.get("aaa {@example john.doe} ccc")).isEqualTo("john.doe");
        assertThat(Macro.NAME.get("aaa {@name bbb} ccc")).isEqualTo("bbb");
        assertThat(Macro.PATTERN.get("aaa {@pattern bbb} ccc")).isEqualTo("bbb");
        assertThat(Macro.STATUS.get("aaa {@status 666} ccc")).isEqualTo("666");
        assertThat(Macro.TYPE.get("aaa {@type bbb} ccc")).isEqualTo("bbb");
        assertThat(Macro.URL.get("aaa {@url aaa/bbb/ccc} ccc")).isEqualTo("aaa/bbb/ccc");
        assertThat(Macro.URL.get("aaa {@url {@link aaa#bbb}} ccc")).isEqualTo("{@link aaa#bbb}");
    }

    @Test
    public void shouldGetFirstGroupWhenMultipleGroupsExist() {
        assertThat(Macro.DEFAULT.get("aaa {@default bbb} ccc {@default ddd}")).isEqualTo("bbb");
        assertThat(Macro.ENUM.get("aaa {@enum [bbb;ccc;ddd]} eee {@enum [fff;ggg;hhh]}")).isEqualTo("[bbb,ccc,ddd]");
        assertThat(Macro.ENUM.get("aaa {@enum [bbb,ccc,ddd]} eee {@enum [fff,ggg,hhh]}")).isEqualTo("[bbb,ccc,ddd]");
        assertThat(Macro.EXAMPLE.get("aaa {@example john.doe} ccc {@example max.payne}")).isEqualTo("john.doe");
        assertThat(Macro.NAME.get("aaa {@name bbb} ccc {@name ddd}")).isEqualTo("bbb");
        assertThat(Macro.PATTERN.get("aaa {@pattern bbb} ccc {@pattern ddd}")).isEqualTo("bbb");
        assertThat(Macro.STATUS.get("aaa {@status 666} ccc {@status 777}")).isEqualTo("666");
        assertThat(Macro.TYPE.get("aaa {@type bbb} ccc {@type ddd}")).isEqualTo("bbb");
        assertThat(Macro.URL.get("aaa {@url aaa/bbb/ccc} ccc {@url ddd/eee/fff} ddd")).isEqualTo("aaa/bbb/ccc");
        assertThat(Macro.URL.get("aaa {@url {@link aaa#bbb}} ccc {@url {@link ddd#eee}} ddd")).isEqualTo("{@link aaa#bbb}");
    }

    @Test
    public void shouldIgnoreSpacesWhenMultipleSpacesExist() {
        assertThat(Macro.DEFAULT.get("   aaa   {   @default   bbb   }   ccc   ")).isEqualTo("bbb");
        assertThat(Macro.ENUM.get("   aaa   {   @enum   [bbb,ccc,ddd]   }   eee   ")).isEqualTo("[bbb,ccc,ddd]");
        assertThat(Macro.ENUM.get("   aaa   {   @enum   [bbb,ccc,ddd]   }   eee   ")).isEqualTo("[bbb,ccc,ddd]");
        assertThat(Macro.EXAMPLE.get("   aaa   {   @example   john.doe   }   ccc   ")).isEqualTo("john.doe");
        assertThat(Macro.NAME.get("   aaa   {   @name   bbb   }   ccc   ")).isEqualTo("bbb");
        assertThat(Macro.PATTERN.get("   aaa   {   @pattern   bbb   }   ccc   ")).isEqualTo("bbb");
        assertThat(Macro.STATUS.get("   aaa   {   @status   666   }   ccc   ")).isEqualTo("666");
        assertThat(Macro.TYPE.get("   aaa   {   @type   bbb   }   ccc   ")).isEqualTo("bbb");
        assertThat(Macro.URL.get("   aaa   {   @url   aaa/bbb/ccc   }   ccc")).isEqualTo("aaa/bbb/ccc");
        assertThat(Macro.URL.get("   aaa   {   @url   {   @link aaa#bbb   }   }   ccc")).isEqualTo("{   @link aaa#bbb   }");
    }

    @Test
    public void shouldReadOneGroupWhenOneGroupContainsPotentialSubgroups() {
        assertThat(Macro.DEFAULT.get("   aaa   {   @default   bbb bbb   }   ccc   ")).isEqualTo("bbb bbb");
        assertThat(Macro.ENUM.get("   aaa   {   @enum   [bbb,ccc,ddd] [bbb,ccc,ddd]   }   eee   ")).isEqualTo("[bbb,ccc,ddd] [bbb,ccc,ddd]");
        assertThat(Macro.ENUM.get("   aaa   {   @enum   [bbb,ccc,ddd] [bbb,ccc,ddd]   }   eee   ")).isEqualTo("[bbb,ccc,ddd] [bbb,ccc,ddd]");
        assertThat(Macro.EXAMPLE.get("   aaa   {   @example   john doe   }   ccc   ")).isEqualTo("john doe");
        assertThat(Macro.NAME.get("   aaa   {   @name   bbb bbb   }   ccc   ")).isEqualTo("bbb bbb");
        assertThat(Macro.PATTERN.get("   aaa   {   @pattern   bbb bbb   }   ccc   ")).isEqualTo("bbb bbb");
    }

    @Test
    public void testExists() {
        assertThat(Macro.DEFAULT.exists("aaa {@default bbb} ccc")).isTrue();
        assertThat(Macro.ENUM.exists("aaa {@enum [bbb;ccc;ddd]} eee")).isTrue();
        assertThat(Macro.EXAMPLE.exists("aaa {@example john.doe} ccc")).isTrue();
        assertThat(Macro.NAME.exists("aaa {@name bbb} ccc")).isTrue();
        assertThat(Macro.PATTERN.exists("aaa {@pattern bbb} ccc")).isTrue();
        assertThat(Macro.OPTIONAL.exists("aaa {@optional} ccc")).isTrue();
        assertThat(Macro.STATUS.exists("aaa {@status 666} ccc")).isTrue();
        assertThat(Macro.TYPE.exists("aaa {@type 666} ccc")).isTrue();
        assertThat(Macro.URL.exists("aaa {@url aaa/bbb/ccc} ccc")).isTrue();
        assertThat(Macro.URL.exists("aaa {@url {@link aaa#bbb}} ccc")).isTrue();
    }

    @Test
    public void testRemove() {
        assertThat(Macro.DEFAULT.remove("aaa {@default bbb} ccc")).isEqualTo("aaa ccc");
        assertThat(Macro.ENUM.remove("aaa {@enum [bbb;ccc;ddd]} eee")).isEqualTo("aaa eee");
        assertThat(Macro.EXAMPLE.remove("aaa {@example john.doe} ccc")).isEqualTo("aaa ccc");
        assertThat(Macro.NAME.remove("aaa {@name bbb} ccc")).isEqualTo("aaa ccc");
        assertThat(Macro.PATTERN.remove("aaa {@pattern bbb} ccc")).isEqualTo("aaa ccc");
        assertThat(Macro.OPTIONAL.remove("aaa {@optional} ccc")).isEqualTo("aaa ccc");
        assertThat(Macro.STATUS.remove("aaa {@status 666} ccc")).isEqualTo("aaa ccc");
        assertThat(Macro.TYPE.remove("aaa {@type bbb} ccc")).isEqualTo("aaa ccc");
        assertThat(Macro.URL.remove("aaa {@url aaa/bbb/ccc} ccc")).isEqualTo("aaa ccc");
        assertThat(Macro.URL.remove("aaa {@url {@link aaa#bbb}} ccc")).isEqualTo("aaa ccc");

        assertThat(Macro.DEFAULT.remove("aaa {@default bbb} ccc {@default ddd} eee")).isEqualTo("aaa ccc eee");
        assertThat(Macro.EXAMPLE.remove("aaa {@example john.doe} ccc {@example max.payne} eee")).isEqualTo("aaa ccc eee");
        assertThat(Macro.NAME.remove("aaa {@name bbb} ccc {@name ddd} eee")).isEqualTo("aaa ccc eee");
        assertThat(Macro.PATTERN.remove("aaa {@pattern bbb} ccc {@pattern ddd} eee")).isEqualTo("aaa ccc eee");
        assertThat(Macro.OPTIONAL.remove("aaa {@optional} ccc {@optional} eee")).isEqualTo("aaa ccc eee");
        assertThat(Macro.STATUS.remove("aaa {@status 666} ccc {@status 777} eee")).isEqualTo("aaa ccc eee");
        assertThat(Macro.TYPE.remove("aaa {@type bbb} ccc {@type ddd} eee")).isEqualTo("aaa ccc eee");
        assertThat(Macro.URL.remove("aaa {@url aaa/bbb/ccc} ccc {@url ddd/eee/fff} ddd")).isEqualTo("aaa ccc ddd");
        assertThat(Macro.URL.remove("aaa {@url {@link aaa#bbb}} ccc {@url {@link ddd#eee}} ddd")).isEqualTo("aaa ccc ddd");
    }

    @Test
    public void shouldThrowExceptionWhenMacroDuplication() {
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());
        ThreadLocalContext.setParamName("param");
        ThreadLocalContext.setClassName("class");
        ThreadLocalContext.setMethodName("method");

        assertThatThrownBy(() -> Macro.DEFAULT.get("aaa {@default bbb} ccc {@default ddd}")).isInstanceOf(RamlProcessingException.class);
    }

    @Test
    public void shouldWarningWhenMacroDuplication() {
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(false).build());
        ThreadLocalContext.setParamName("param");
        ThreadLocalContext.setClassName("class");
        ThreadLocalContext.setMethodName("method");

        assertThat(Macro.DEFAULT.get("aaa {@default bbb} ccc {@default ddd}")).isEqualTo("bbb");
        Assertions.assertThat(((MessagerMock)ThreadLocalContext.getMessager()).getMessage()).isNotEmpty();
    }

    @Test
    public void shouldReturnNameWhenMultiLineString() {
        String doc = TestUtils.joinStrings(
                "/**",
                " * This is the first line of the description",
                " * This is the second line {@name McDonalds} and the end.",
                " */");
        assertThat(Macro.NAME.get(doc)).isEqualTo("McDonalds");
    }

    @Test
    public void testRemoveAll() {
        String doc = TestUtils.joinStrings(
                "{@default bbb}{@enum [bbb;ccc;ddd]}{@name bbb}{@pattern bbb}",
                "{@optional}{@status 666}{@type bbb}");

        assertThat(Macro.removeAll(doc)).isEmpty();
    }

    @Test
    public void shouldReturnTagLinkWhenUrlHasTagLink() {
        assertThat(Macro.URL.get("{@url {@link aaa}}")).isEqualTo("{@link aaa}");
        assertThat(Macro.URL.get("{@url {@link aaa#bbb}}")).isEqualTo("{@link aaa#bbb}");
        assertThat(Macro.URL.get("{@url {@link aaa.bbb.ccc#ddd}}")).isEqualTo("{@link aaa.bbb.ccc#ddd}");
        assertThat(Macro.URL.get("{@url {@link #bbb}}")).isEqualTo("{@link #bbb}");
    }
}
