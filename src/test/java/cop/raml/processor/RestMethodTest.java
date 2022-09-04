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

import cop.raml.utils.Utils;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Oleg Cherednik
 * @since 29.01.2017
 */
public class RestMethodTest {
    @Test
    public void testRestMethod() {
        RestMethod method = new RestMethod("GET");
        method.addBody(Utils.APPLICATION_JSON);
        method.addQueryParameters("foo");
        method.addResponse(200);

        assertThat(method.getType()).isEqualTo("GET");
        assertThat(method.getQueryParameters()).hasSize(1);
        assertThat(method.getResponses()).hasSize(1);
        assertThat(method.getBodies()).hasSize(1);

        method.setDescription("description");
        assertThat(method.getDescription()).isEqualTo("description");
        assertThat(method.getDescription(2)).isEqualTo("  description");
    }

    @Test
    public void shouldIgnoreCaseTypeWhenSortMethods() {
        Map<String, RestMethod> methods = new TreeMap<>(RestMethod.SORT_BY_TYPE);
        addToMap(methods, new RestMethod("PUT"));
        addToMap(methods, new RestMethod("TRACE"));
        addToMap(methods, new RestMethod("HEAD"));
        addToMap(methods, new RestMethod("POST"));
        addToMap(methods, new RestMethod("GET"));
        addToMap(methods, new RestMethod("OPTIONS"));
        addToMap(methods, new RestMethod("DELETE"));
        addToMap(methods, new RestMethod("PATCH"));

        assertThat(methods.keySet()).containsExactly("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE");
    }

    // ========== static ==========

    private static Map<String, RestMethod> addToMap(Map<String, RestMethod> methods, RestMethod method) {
        methods.put(method.getType(), method);
        return methods;
    }
}
