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

import cop.raml.utils.ReflectionUtils;
import cop.raml.utils.ThreadLocalContext;
import cop.raml.mocks.MockUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author Oleg Cherednik
 * @since 29.12.2016
 */
public class JavaDocTagTest {
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
        assertThat(JavaDocTag.values()).hasSize(14);
    }

    @Test
    public void testId() throws Exception {
        assertThat(getId(JavaDocTag.API_NOTE)).isEqualTo("@apiNote");
        assertThat(getId(JavaDocTag.AUTHOR)).isEqualTo("@author");
        assertThat(getId(JavaDocTag.DEPRECATED)).isEqualTo("@deprecated");
        assertThat(getId(JavaDocTag.EXCEPTION)).isEqualTo("@exception");
        assertThat(getId(JavaDocTag.IMPL_NOTE)).isEqualTo("@implNote");
        assertThat(getId(JavaDocTag.IMPL_SPEC)).isEqualTo("@implSpec");
        assertThat(getId(JavaDocTag.PARAM)).isEqualTo("@param");
        assertThat(getId(JavaDocTag.RETURN)).isEqualTo("@return");
        assertThat(getId(JavaDocTag.SEE)).isEqualTo("@see");
        assertThat(getId(JavaDocTag.SERIAL)).isEqualTo("@serial");
        assertThat(getId(JavaDocTag.SERIAL_DATA)).isEqualTo("@serialData");
        assertThat(getId(JavaDocTag.SINCE)).isEqualTo("@since");
        assertThat(getId(JavaDocTag.THROWS)).isEqualTo("@throws");
        assertThat(getId(JavaDocTag.VERSION)).isEqualTo("@version");
    }

    @Test
    public void testGetPattern() {
        assertThat(JavaDocTag.API_NOTE.getPattern()).isNull();
        assertThat(JavaDocTag.AUTHOR.getPattern()).isNull();
        assertThat(JavaDocTag.DEPRECATED.getPattern()).isNull();
        assertThat(JavaDocTag.EXCEPTION.getPattern()).isNull();
        assertThat(JavaDocTag.IMPL_NOTE.getPattern()).isNull();
        assertThat(JavaDocTag.IMPL_SPEC.getPattern()).isNull();
        assertThat(JavaDocTag.PARAM.getPattern()).isNotNull();
        assertThat(JavaDocTag.RETURN.getPattern()).isNotNull();
        assertThat(JavaDocTag.SEE.getPattern()).isNull();
        assertThat(JavaDocTag.SERIAL.getPattern()).isNull();
        assertThat(JavaDocTag.SERIAL_DATA.getPattern()).isNull();
        assertThat(JavaDocTag.SINCE.getPattern()).isNull();
        assertThat(JavaDocTag.THROWS.getPattern()).isNull();
        assertThat(JavaDocTag.VERSION.getPattern()).isNull();
    }

    @Test
    public void testStartsWith() {
        assertThat(JavaDocTag.startsWith(null)).isFalse();
        assertThat(JavaDocTag.startsWith("")).isFalse();
        assertThat(JavaDocTag.startsWith("   ")).isFalse();
        assertThat(JavaDocTag.startsWith("some string")).isFalse();
        assertThat(JavaDocTag.startsWith("@param")).isTrue();
    }

    // ========== static ==========

    private static String getId(JavaDocTag javaDocTag) throws Exception {
        return ReflectionUtils.getFieldValue(javaDocTag, "id");
    }
}
