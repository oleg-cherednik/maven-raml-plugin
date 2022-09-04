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
package cop.maven.plugins;

import cop.raml.utils.ReflectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

/**
 * @author Oleg Cherednik
 * @since 31.01.2017
 */
@SuppressWarnings("InstanceMethodNamingConvention")
public class MavenUtilsTest {

    @BeforeClass
    private static void coberturaFix() {
        ReflectionUtils.invokeConstructor(MavenUtils.class);
    }

    @Test
    public void testGetDistinctChildrenAmount() throws Exception {
        assertThat(getDistinctChildrenAmount(new Xpp3Dom("aaa"))).isZero();
        assertThat(getDistinctChildrenAmount(createNodeChildren("aaa", "one", "two", "two"))).isEqualTo(2);
    }

    @Test
    public void testIsMap() throws Exception {
        assertThat(isMap(createValueNode("aaa", "bbb"))).isFalse();
        assertThat(isMap(null)).isFalse();
        assertThat(isMap(new Xpp3Dom("aaa"))).isFalse();
        assertThat(isMap(createNodeChildren("aaa", "param", "param", "param"))).isFalse();
        assertThat(isMap(createNodeChildren("nouns", "noun", "noun", "noun"))).isFalse();
        assertThat(isMap(createNodeChildren("aaa", "bbb", "bbb", "bbb"))).isTrue();
        assertThat(isMap(createNodeChildren("aaa", "bbb", "ccc", "ddd"))).isTrue();
        assertThatThrownBy(() -> isMap(createNodeChildren("aaa", "bbb", "bbb", "ccc", "ccc"))).isInstanceOf(Exception.class);
    }

    @Test
    public void isArray() throws Exception {
        assertThat(isArray(createValueNode("aaa", "bbb"))).isFalse();
        assertThat(isArray(null)).isFalse();
        assertThat(isArray(new Xpp3Dom("aaa"))).isFalse();
        assertThat(isArray(createNodeChildren("aaa", "param", "param", "param"))).isTrue();
        assertThat(isArray(createNodeChildren("nouns", "noun", "noun", "noun"))).isTrue();
        assertThat(isArray(createNodeChildren("aaa", "bbb", "bbb", "bbb"))).isFalse();
        assertThat(isArray(createNodeChildren("aaa", "bbb", "ccc", "ddd"))).isFalse();
        assertThatThrownBy(() -> isArray(createNodeChildren("aaa", "bbb", "bbb", "ccc", "ccc"))).isInstanceOf(Exception.class);
    }

    @Test
    public void testIsPrimitive() throws Exception {
        assertThat(isPrimitive(null)).isFalse();
        assertThat(isPrimitive(new Xpp3Dom("aaa"))).isFalse();
        assertThat(isPrimitive(createValueNode("aaa", "bbb"))).isTrue();
        assertThat(isPrimitive(createArray("name", "param", "one", "two", "three"))).isFalse();
    }

    @Test
    public void testReadArray() throws Exception {
        assertThat(readArray(createNodeChildren("aaa", "bbb", "bbb", "bbb"))).isNull();
        assertThat(readArray(createArray("name", "param", "one", "two", "three"))).containsExactly("one", "two", "three");
        assertThatThrownBy(() -> readArray(createArray("name", "param", "one", null, "three"))).isInstanceOf(Exception.class);
    }

    @Test
    public void shouldThrowExceptionWhenArrayChildHasChildren() {
        Xpp3Dom node = createArray("name", "param", "one", "two", "three");
        node.getChild(0).addChild(createValueNode("aaa", "bbb"));
        assertThatThrownBy(() -> readArray(node)).isInstanceOf(Exception.class);
    }

    @Test
    public void testReadPrimitive() throws Exception {
        assertThat(readPrimitive("aaa")).isEqualTo("aaa");
        assertThat(readPrimitive("true")).isEqualTo(true);
        assertThat(readPrimitive("false")).isEqualTo(false);
    }

    @Test
    public void shouldReturnEmptyWhenArrayNode() {
        assertThat(MavenUtils.readMap(createArray("name", "param", "one", "two", "three"))).isSameAs(Collections.emptyMap());
    }

    @Test
    @SuppressWarnings("serial")
    public void shouldReturnMapWhenMapNode() {
        Xpp3Dom node = createMap("name", new LinkedHashMap<String, Object>() {{
            put("key1", "str");
            put("key2", "true");
            put("key3", new LinkedHashMap<String, Object>() {{
                put("it1", "str");
                put("it2", "777");
            }});
            put("key4", Arrays.asList("one", "two", "three"));
        }});

        Map<String, Object> map = MavenUtils.readMap(node);
        assertThat(map).containsOnlyKeys("key1", "key2", "key3", "key4");
        assertThat(map.get("key1")).isEqualTo("str");
        assertThat(map.get("key2")).isEqualTo(true);
        assertThat((Map<String, Object>)map.get("key3")).hasSize(2);
        assertThat((List<String>)map.get("key4")).hasSize(3);
    }

    @Test
    @SuppressWarnings("serial")
    public void shouldThrowExceptionWhenMapIsNotCorrect() {
        Xpp3Dom node = createMap("name", new LinkedHashMap<String, Object>() {{
            put("key1", "str1");
        }});

        Xpp3Dom child = createValueNode("key2", "val");
        child.addChild(new Xpp3Dom("sub"));
        node.addChild(child);

        assertThatThrownBy(() -> MavenUtils.readMap(node)).isInstanceOf(Exception.class);
    }

    // ========== static ==========

    private static Xpp3Dom createArray(String name, String key, String... values) {
        Xpp3Dom node = new Xpp3Dom(name);

        for (String value : values) {
            node.addChild(createValueNode(key, value));
        }

        return node;
    }

    private static Xpp3Dom createMap(String name, Map<String, Object> map) {
        Xpp3Dom root = new Xpp3Dom(name);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map)
                root.addChild(createMap(key, (Map<String, Object>)value));
            else if (value instanceof Collection)
                root.addChild(createArray(key, "param", ((Collection<String>)value).toArray(new String[((Collection<String>)value).size()])));
            else if (value.getClass().isArray())
                root.addChild(createArray(key, "param", (String[])value));
            else
                root.addChild(createValueNode(key, (String)value));
        }

        return root;
    }

    private static Xpp3Dom createValueNode(@NotNull String name, String value) {
        Xpp3Dom node = new Xpp3Dom(name);
        node.setValue(value);
        return node;
    }

    private static Xpp3Dom createNodeChildren(@NotNull String name, String... names) {
        Xpp3Dom node = new Xpp3Dom(name);

        if (ArrayUtils.isNotEmpty(names))
            for (String str : names)
                node.addChild(new Xpp3Dom(str));

        return node;
    }

    private static int getDistinctChildrenAmount(Xpp3Dom node) throws Exception {
        return ReflectionUtils.invokeStaticMethod(MavenUtils.class, "getDistinctChildrenAmount", Xpp3Dom.class, node);
    }

    private static boolean isMap(Xpp3Dom node) throws Exception {
        return ReflectionUtils.invokeStaticMethod(MavenUtils.class, "isMap", Xpp3Dom.class, node);
    }

    private static boolean isArray(Xpp3Dom node) throws Exception {
        return ReflectionUtils.invokeStaticMethod(MavenUtils.class, "isArray", Xpp3Dom.class, node);
    }

    private static boolean isPrimitive(Xpp3Dom node) throws Exception {
        return ReflectionUtils.invokeStaticMethod(MavenUtils.class, "isPrimitive", Xpp3Dom.class, node);
    }

    private static List<String> readArray(Xpp3Dom node) throws Exception {
        return ReflectionUtils.invokeStaticMethod(MavenUtils.class, "readArray", Xpp3Dom.class, node);
    }

    private static Object readPrimitive(String str) throws Exception {
        return ReflectionUtils.invokeStaticMethod(MavenUtils.class, "readPrimitive", String.class, str);
    }
}
