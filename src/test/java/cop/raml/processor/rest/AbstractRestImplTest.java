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
package cop.raml.processor.rest;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oleg Cherednik
 * @since 20.01.2017
 */
public class AbstractRestImplTest {
    private AbstractRestImpl rest = new Rest();

    @BeforeMethod
    private void init() {
        rest = new Rest();
    }

    @Test
    public void testDefaultForNullObject() {
        assertThat(rest.getRequestController()).isNull();
        assertThat(rest.getRequestMapping()).isNull();
        assertThat(rest.getRequestPath(null, null)).isNull();
        assertThat(rest.getRequestMethod(null)).isNull();
        // --- Body ---
        assertThat(rest.isBody(null)).isFalse();
        assertThat(rest.isBodyRequired(null)).isFalse();
        // --- URI Parameters ---
        assertThat(rest.isUriParam(null)).isFalse();
        assertThat(rest.getUriParamName(null)).isNull();
        assertThat(rest.isUriParamRequired(null)).isFalse();
        // --- Query Parameters --
        assertThat(rest.isQueryParam(null)).isFalse();
        assertThat(rest.getQueryParamName(null)).isNull();
        assertThat(rest.isQueryParamRequired(null)).isFalse();
        assertThat(rest.getQueryParamDefault(null)).isNull();
    }

    // ========== data ==========

    private static final class Rest extends AbstractRestImpl {
    }
}
