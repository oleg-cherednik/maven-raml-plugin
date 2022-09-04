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

import cop.raml.utils.ReflectionUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Oleg Cherednik
 * @since 29.01.2017
 */
public class DevUtilsTest {
    @BeforeClass
    private static void coberturaFix() {
        ReflectionUtils.invokeConstructor(DevUtils.class);
    }

    @Test
    public void testRestApi() {
        RestApi api = new RestApi();
        DevUtils.setRestApi(api);
        assertThat(DevUtils.getRestApi()).isSameAs(api);
    }

    @Test
    public void testRaml() {
        String raml = "raml";
        DevUtils.setRaml(raml);
        assertThat(DevUtils.getRaml()).isSameAs(raml);
    }

    @Test
    public void testRemove() {
        DevUtils.remove();
        assertContextIsNull();

        DevUtils.setRestApi(new RestApi());
        DevUtils.setRaml("raml");

        assertContextIsNotNull();
        DevUtils.remove();
        assertContextIsNull();
    }

    // =s========= static ==========

    private static void assertContextIsNull() {
        assertThat(DevUtils.getRestApi()).isNull();
        assertThat(DevUtils.getRaml()).isNull();
    }

    private static void assertContextIsNotNull() {
        assertThat(DevUtils.getRestApi()).isNotNull();
        assertThat(DevUtils.getRaml()).isNotNull();
    }
}
