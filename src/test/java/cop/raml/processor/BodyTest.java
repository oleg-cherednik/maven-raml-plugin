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
import cop.raml.utils.Utils;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oleg Cherednik
 * @since 22.12.2016
 */
public class BodyTest {
    @Test
    public void testBody() {
        Body body = new Body(Utils.APPLICATION_JSON);
        assertThat(body.getMediaType()).isEqualTo(Utils.APPLICATION_JSON);
        assertThat(body.isDef()).isFalse();

        assertThat(body.getSchema()).isNull();
        body.setSchema("schema");
        assertThat(body.getSchema()).isEqualTo("schema");

        assertThat(body.isRequired()).isFalse();
        body.setRequired(true);
        assertThat(body.isRequired()).isTrue();

        assertThat(body.hasExample()).isTrue();
        assertThat(body.getExample()).isNull();
        body.setExample(TestUtils.joinStrings(
                "{",
                "\"key1\" : \"val1\",",
                "\"key2\" : \"val2\"",
                "}"));
        assertThat(body.hasExample()).isFalse();
        assertThat(body.getExample(2)).isEqualTo(TestUtils.joinStrings(
                "  {",
                "  \"key1\" : \"val1\",",
                "  \"key2\" : \"val2\"",
                "  }"));
    }

    @Test
    public void testIsSingleLine() {
        Body body = new Body(Utils.APPLICATION_JSON);
        assertThat(body.isSingleLine()).isTrue();

        body.setExample("");
        assertThat(body.isSingleLine()).isTrue();
        body.setExample("   ");
        assertThat(body.isSingleLine()).isTrue();
        body.setExample("   aaa   ");
        assertThat(body.isSingleLine()).isTrue();
        body.setExample("aaa\nbbb");
        assertThat(body.isSingleLine()).isFalse();
        body.setExample("\n\n\n");
        assertThat(body.isSingleLine()).isTrue();
    }
}
