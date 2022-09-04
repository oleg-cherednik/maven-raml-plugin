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

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Oleg Cherednik
 * @since 29.01.2017
 */
@SuppressWarnings("InstanceMethodNamingConvention")
public class ResponseTest {
    @Test
    public void testResponse() {
        Response response = new Response(200);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBodies()).isEmpty();

        response.setDescription("description");
        assertThat(response.getDescription()).isEqualTo("description");
        assertThat(response.getDescription(2)).isEqualTo("  description");
    }

    @Test
    public void shouldNotDuplicateBodyWhenAddBodyForSameMediaType() {
        Response response = new Response(200);

        response.addBody(Utils.APPLICATION_JSON);
        assertThat(response.getBodies()).hasSize(1);

        response.addBody(Utils.APPLICATION_JSON);
        assertThat(response.getBodies()).hasSize(1);

        response.addBody(Utils.APPLICATION_XML);
        assertThat(response.getBodies()).hasSize(2);
    }
}
