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
import cop.raml.utils.Utils;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * @author Oleg Cherednik
 * @since 21.12.2016
 */
@SuppressWarnings("InstanceMethodNamingConvention")
public class RestApiTest {
    @Test
    public void testFindLongestPrefix() throws Exception {
        assertThat(findLongestPrefix(null, null)).isEmpty();
        assertThat(findLongestPrefix(null, "")).isEmpty();
        assertThat(findLongestPrefix("", null)).isEmpty();
        assertThat(findLongestPrefix("   ", "   ")).isEmpty();
        assertThat(findLongestPrefix("aaa/bbb/ccc", "aaa/bbb/ccc")).isEqualTo("aaa/bbb/ccc");
        assertThat(findLongestPrefix("aaa/bbb/ccc", "ddd/eee/fff")).isEmpty();
        assertThat(findLongestPrefix("aaa/bbb/ccc", null)).isEmpty();
        assertThat(findLongestPrefix(null, "aaa/bbb/ccc")).isEmpty();
        assertThat(findLongestPrefix("aaa/bbb/ccc", "aaa/bbb/ddd")).isEqualTo("aaa/bbb");
        assertThat(findLongestPrefix("aaa/bbb/ccc", "aaa/ddd/eee")).isEqualTo("aaa");
        assertThat(findLongestPrefix("/aaa", "/bbb")).isEmpty();
        assertThat(findLongestPrefix("aaa/bbb/ccc", "aaa/bbb/ddd/eee")).isEqualTo("aaa/bbb");
        assertThat(findLongestPrefix("aaa/bbb/ccc/dd", "aaa/bbb/eee")).isEqualTo("aaa/bbb");
        assertThat(findLongestPrefix("/aaa/bbb", "/aaa/ccc")).isEqualTo("/aaa");
    }

    @Test
    public void testGetEqualPartsAmount() throws Exception {
        assertThat(getEqualPartsAmount(Utils.splitPath("aaa/bbb/ccc"), Utils.splitPath("aaa/bbb/ccc"))).isEqualTo(3);
        assertThat(getEqualPartsAmount(Utils.splitPath("aaa/bbb/ccc"), Utils.splitPath("ddd/eee/fff"))).isZero();
        assertThat(getEqualPartsAmount(Utils.splitPath("aaa/bbb/ccc"), Utils.splitPath("aaa/bbb/ddd"))).isEqualTo(2);
        assertThat(getEqualPartsAmount(Utils.splitPath("aaa/bbb/ccc"), Utils.splitPath("aaa/ddd/eee"))).isEqualTo(1);
    }

    @Test
    public void testAdd() throws Exception {
        Map<String, Resource> resources = new HashMap<>();

        assertThat(add(resources, "aaa/bbb").getPath()).isEqualTo("/aaa/bbb");
        assertThat(resources).hasSize(1);
        assertThat(add(resources, "bbb/ccc").getPath()).isEqualTo("/bbb/ccc");
        assertThat(resources).hasSize(2);
        assertThat(resources.get("/bbb/ccc").getChildren()).isEmpty();

        assertThat(add(resources, "aaa/ccc").getPath()).isEqualTo("/ccc");
        assertThat(resources).hasSize(2);
        assertThat(resources.containsKey("/aaa")).isTrue();
        assertThat(resources.get("/aaa").getChildren()).hasSize(2);
        assertThat(resources.get("/aaa").children.containsKey("/bbb")).isTrue();
        assertThat(resources.get("/aaa").children.containsKey("/ccc")).isTrue();
        assertThat(resources.containsKey("/bbb/ccc")).isTrue();
        assertThat(resources.get("/bbb/ccc").getChildren()).isEmpty();


        assertThat(add(resources, "aaa/ddd").getPath()).isEqualTo("/ddd");
        assertThat(resources).hasSize(2);
        assertThat(resources.containsKey("/aaa")).isTrue();
        assertThat(resources.get("/aaa").getChildren()).hasSize(3);
        assertThat(resources.get("/aaa").children.containsKey("/bbb")).isTrue();
        assertThat(resources.get("/aaa").children.containsKey("/ccc")).isTrue();
        assertThat(resources.get("/aaa").children.containsKey("/ddd")).isTrue();
        assertThat(resources.containsKey("/bbb/ccc")).isTrue();
        assertThat(resources.get("/bbb/ccc").getChildren()).isEmpty();

        assertThat(add(resources, "/bbb").getPath()).isEqualTo("/bbb");
        assertThat(resources).hasSize(2);
        assertThat(resources.containsKey("/aaa")).isTrue();
        assertThat(resources.get("/aaa").getChildren()).hasSize(3);
        assertThat(resources.containsKey("/bbb")).isTrue();
        assertThat(resources.get("/bbb").getChildren()).hasSize(1);
        assertThat(resources.get("/bbb").children.containsKey("/ccc")).isTrue();

        Resource resource = add(resources, "ccc/ddd");
        assertThat(add(resources, "/ccc/ddd")).isSameAs(resource);
    }

    @Test
    public void testCreateResource() {
        RestApi api = new RestApi();

        assertThat(api.getResources()).isEmpty();
        assertThat(api.getResources()).isEmpty();

        Resource resource = api.createResource("aaa/bbb");
        assertThat(resource.getPath()).isEqualTo("/aaa/bbb");
        assertThat(api.getResources()).hasSize(1);
        assertThat(api.getResources().iterator().next()).isSameAs(resource);
    }

    @Test
    public void shouldUseBaseUriWhenCreateResource() {
        RestApi api = new RestApi();
        assertThat(api.getResources()).isEmpty();

        api.setBaseUri("aaa/bbb");
        assertThat(api.createResource("aaa/bbb/ccc/ddd").getPath()).isEqualTo("/ccc/ddd");
        assertThat(api.getResources()).hasSize(1);
    }

    @Test
    public void testDocumentation() {
        RestApi api = new RestApi();

        api.setDocumentation(null, null);
        assertThat(api.getDocTitle()).isNull();
        assertThat(api.getDocContent()).isNull();

        api.setDocumentation("", null);
        assertThat(api.getDocTitle()).isNull();
        assertThat(api.getDocContent()).isNull();

        api.setDocumentation(null, "");
        assertThat(api.getDocTitle()).isNull();
        assertThat(api.getDocContent()).isNull();

        api.setDocumentation("  ", null);
        assertThat(api.getDocTitle()).isNull();
        assertThat(api.getDocContent()).isNull();

        api.setDocumentation(null, "  ");
        assertThat(api.getDocTitle()).isNull();
        assertThat(api.getDocContent()).isNull();

        api.setDocumentation("docTitle", null);
        assertThat(api.getDocTitle()).isNull();
        assertThat(api.getDocContent()).isNull();

        api.setDocumentation(null, "docContent");
        assertThat(api.getDocTitle()).isNull();
        assertThat(api.getDocContent()).isNull();

        api.setDocumentation("docTitle", "docContent");
        assertThat(api.getDocTitle()).isEqualTo("docTitle");
        assertThat(api.getDocContent()).isEqualTo("docContent");
    }

    // TODO if does not belong, then just add paths
//    @Test
//    public void shouldThrowExceptionWhenResourcePathDoesNotBelongToBaseUri() {
//        RestApi api = new RestApi();
//        assertThat(api.getResources()).isEmpty();
//
//        api.setBaseUri("aaa/bbb");
//        assertThatThrownBy(() -> api.createResource("bbb/ccc")).isExactlyInstanceOf(RamlProcessingException.class);
//        assertThat(api.getResources()).isEmpty();
//    }

    // ========== static ==========

    @SuppressWarnings("MethodCanBeVariableArityMethod")
    private static int getEqualPartsAmount(String[] parts1, String[] parts2) throws Exception {
        return ReflectionUtils.invokeStaticMethod(RestApi.class, "getEqualPartsAmount", String[].class, String[].class, parts1, parts2);
    }

    private static String findLongestPrefix(String str1, String str2) throws Exception {
        return ReflectionUtils.invokeStaticMethod(RestApi.class, "findLongestPrefix", String.class, String.class, str1, str2);
    }

    private static Resource add(Map<String, Resource> resources, String path) throws Exception {
        return ReflectionUtils.invokeStaticMethod(RestApi.class, "add", Map.class, String.class, resources, path);
    }
}
