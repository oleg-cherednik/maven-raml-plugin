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
package cop.raml.utils.example;

import cop.raml.processor.Config;
import cop.raml.utils.ReflectionUtils;
import cop.raml.utils.ThreadLocalContext;
import cop.raml.utils.Utils;
import cop.raml.mocks.MockUtils;
import cop.raml.mocks.TypeElementMock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oleg Cherednik
 * @since 01.01.17
 */
@SuppressWarnings("InstanceMethodNamingConvention")
public class ExampleProviderTest {
    @BeforeMethod
    private void initContext() {
        MockUtils.initThreadLocalContext();
    }

    @AfterMethod
    private void clearContext() {
        ThreadLocalContext.remove();
    }

    @Test
    public void shouldUseCorrectStrategy() throws Exception {
        ExampleProvider provider = new ExampleProvider();
        Map<String, Example> strategies = getStrategies(provider);

        assertThat(strategies).hasSize(1);
        assertThat(strategies.get(Utils.APPLICATION_JSON)).isExactlyInstanceOf(JsonExample.class);
    }

    @Test
    public void shouldReturnExampleWhenConfigOptionDepending() throws ClassNotFoundException {
        ThreadLocalContext.setConfig(Config.builder().ramlShowExample(false).build());
        TypeElementMock element = MockUtils.createElement(MockUtils.Count.class);
        assertThat(new ExampleProvider().getExample(element, Utils.APPLICATION_JSON, false)).isNull();

        ThreadLocalContext.setConfig(Config.builder().ramlShowExample(true).build());
        assertThat(new ExampleProvider().getExample(element, Utils.APPLICATION_JSON, false)).isNotEmpty();
    }

    @Test
    public void shouldReturnExampleWhenArrayOption() throws ClassNotFoundException {
        TypeElementMock element = MockUtils.createElement(MockUtils.Count.class);
        assertThat(new ExampleProvider().getExample(element, Utils.APPLICATION_JSON, false)).isEqualTo("ONE");
        assertThat(new ExampleProvider().getExample(element, Utils.APPLICATION_JSON, true)).isEqualTo("[ \"ONE\" ]");
    }

    @Test
    public void shouldReturnNullWhenMediaTypeIsNotJson() throws ClassNotFoundException {
        TypeElementMock element = MockUtils.createElement(MockUtils.Count.class);
        assertThat(new ExampleProvider().getExample(element, "<unknown>", false)).isNull();
    }

    @Test
    public void shouldNotReturnArrayExampleWhenDefaultOptionIsUsed() throws ClassNotFoundException {
        TypeElementMock element = MockUtils.createElement(MockUtils.Count.class);
        assertThat(new ExampleProvider().getExample(element, Utils.APPLICATION_JSON)).isEqualTo("ONE");
    }

    // ========== static ==========

    private static Map<String, Example> getStrategies(ExampleProvider provider) throws Exception {
        return ReflectionUtils.getFieldValue(provider, "strategies");
    }
}
