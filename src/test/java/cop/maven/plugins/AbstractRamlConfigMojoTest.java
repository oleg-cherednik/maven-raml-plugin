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

import cop.maven.plugins.mocks.LogMock;
import cop.maven.plugins.mocks.MojoExecutionMock;
import cop.raml.TestUtils;
import cop.raml.processor.Config;
import cop.raml.utils.ReflectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Resource;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Oleg Cherednik
 * @since 13.02.2017
 */
@SuppressWarnings({ "serial", "AmbiguousMethodCall", "InnerClassTooDeeplyNested" })
public class AbstractRamlConfigMojoTest {
    private RamlMojoMock mojo;

    @BeforeMethod
    public void init() throws Exception {
        mojo = new RamlMojoMock();
    }

    @Test
    public void shouldReturnNullWhenYamlNotFound() throws Exception {
        setYaml(mojo, "raml.yaml");

        setBuildResources(mojo, Collections.singletonList(TestUtils.createResource(TestUtils.createTempDir().getAbsolutePath())));
        assertThat(getYaml(mojo)).isNull();
        assertThat(((LogMock)mojo.getLog()).getInfoContent()).isEqualTo("no raml configuration file found");
    }

    @Test
    public void shouldReturnYamlFileWhenYamlOptionSet() throws Exception {
        setYaml(mojo, "raml.yml");
        File dir = TestUtils.createTempDir();
        String path = TestUtils.createTempFile(dir, "raml.yml").getParentFile().getAbsolutePath();

        setBuildResources(mojo, new ArrayList<Resource>() {{
            add(TestUtils.createResource(TestUtils.createTempDir(dir, "foo").getAbsolutePath()));
            add(TestUtils.createResource(path));
        }});

        assertThat(getYaml(mojo).getAbsolutePath()).isEqualTo(FilenameUtils.concat(path, "raml.yml"));
        assertThat(((LogMock)mojo.getLog()).getInfoContent()).startsWith("found raml configuration file");
    }

    @Test
    public void shouldAddApiPartWhenApiSet() throws Exception {
        setApi(mojo, new HashMap<String, Object>() {{
            put(Config.KEY_API_TITLE, "aaa");
            put(Config.KEY_API_BASE_URI, "uri");
            put(Config.KEY_API_VERSION, "version");
            put(Config.KEY_API_MEDIA_TYPE, "bbb");

            put(Config.KEY_API_DOC, new HashMap<String, Object>() {{
                put(Config.KEY_API_DOC_TITLE, "ccc");
                put(Config.KEY_API_DOC_CONTENT, "ddd");
            }});
        }});

        Map<String, Object> map = new HashMap<>();
        applyApiPart(mojo, map);

        assertThat(map).hasSize(1);

        map = (Map<String, Object>)map.get(Config.KEY_API);
        assertThat(map).hasSize(5);
        assertThat(map.get(Config.KEY_API_TITLE)).isEqualTo("aaa");
        assertThat(map.get(Config.KEY_API_BASE_URI)).isEqualTo("uri");
        assertThat(map.get(Config.KEY_API_VERSION)).isEqualTo("version");
        assertThat(map.get(Config.KEY_API_MEDIA_TYPE)).isEqualTo("bbb");

        map = (Map<String, Object>)map.get(Config.KEY_API_DOC);
        assertThat(map).hasSize(2);
        assertThat(map.get(Config.KEY_API_DOC_TITLE)).isEqualTo("ccc");
        assertThat(map.get(Config.KEY_API_DOC_CONTENT)).isEqualTo("ddd");
    }

    @Test
    public void shouldAddApiDefaultContentWhenApiNotSet() throws Exception {
        for (Map<String, Object> api : Arrays.asList(null, new HashMap<String, Object>())) {
            setApi(mojo, api);

            Map<String, Object> map = new HashMap<>();
            applyApiPart(mojo, map);

            assertThat(map).hasSize(1);

            map = (Map<String, Object>)map.get(Config.KEY_API);
            assertThat(map).hasSize(3);
            assertThat(map.get(Config.KEY_API_TITLE)).isEqualTo("${project.name}");
            assertThat(map.get(Config.KEY_API_BASE_URI)).isEqualTo("${project.url}");

            map = (Map<String, Object>)map.get(Config.KEY_API_DOC);
            assertThat(map).hasSize(2);
            assertThat(map.get(Config.KEY_API_DOC_TITLE)).isEqualTo("Public");
            assertThat(map.get(Config.KEY_API_DOC_CONTENT)).isEqualTo("${project.description}");
        }
    }

    @Test
    public void shouldAddRamlPartWhenRamlSet() throws Exception {
        setRaml(mojo, new HashMap<String, Object>() {{
            put(Config.KEY_RAML_VERSION, "aaa");
            put(Config.KEY_RAML_SHOW_EXAMPLE, "bbb");
            put(Config.KEY_RAML_STOP_ON_ERROR, "ccc");
            put(Config.KEY_RAML_DEV, "ddd");

            put(Config.KEY_RAML_SKIP, new HashMap<String, Object>() {{
                put(Config.KEY_RAML_SKIP_DEFAULT, "eee");
                put(Config.KEY_RAML_SKIP_PATTERNS, "fff");
            }});
        }});

        Map<String, Object> map = new HashMap<>();
        applyRamlPart(mojo, map);

        assertThat(map).hasSize(1);

        map = (Map<String, Object>)map.get(Config.KEY_RAML);
        assertThat(map).hasSize(5);
        assertThat(map.get(Config.KEY_RAML_VERSION)).isEqualTo("aaa");
        assertThat(map.get(Config.KEY_RAML_SHOW_EXAMPLE)).isEqualTo("bbb");
        assertThat(map.get(Config.KEY_RAML_STOP_ON_ERROR)).isEqualTo("ccc");
        assertThat(map.get(Config.KEY_RAML_DEV)).isEqualTo("ddd");

        map = (Map<String, Object>)map.get(Config.KEY_RAML_SKIP);
        assertThat(map).hasSize(2);
        assertThat(map.get(Config.KEY_RAML_SKIP_DEFAULT)).isEqualTo("eee");
        assertThat(map.get(Config.KEY_RAML_SKIP_PATTERNS)).isEqualTo("fff");
    }

    @Test
    public void shouldAddRamlDefaultContentWhenRamlNotSet() throws Exception {
        for (Map<String, Object> raml : Arrays.asList(null, new HashMap<String, Object>())) {
            setRaml(mojo, raml);

            Map<String, Object> map = new HashMap<>();
            applyRamlPart(mojo, map);

            assertThat(map).hasSize(1);
            assertThat((Map<String, Object>)map.get(Config.KEY_RAML)).isEmpty();
        }
    }

    @Test
    public void testApplyMavenPomConfig() throws Exception {
        setApi(mojo, new HashMap<String, Object>() {{
            put(Config.KEY_API_TITLE, "aaa_");
            put(Config.KEY_API_BASE_URI, "uri_");
            put(Config.KEY_API_VERSION, "version_");
            put(Config.KEY_API_MEDIA_TYPE, "bbb_");

            put(Config.KEY_API_DOC, new HashMap<String, Object>() {{
                put(Config.KEY_API_DOC_TITLE, "ccc_");
                put(Config.KEY_API_DOC_CONTENT, "ddd_");
            }});
        }});

        setRaml(mojo, new HashMap<String, Object>() {{
            put(Config.KEY_RAML_VERSION, "aaaa");
            put(Config.KEY_RAML_SHOW_EXAMPLE, "bbbb");
            put(Config.KEY_RAML_STOP_ON_ERROR, "cccc");
            put(Config.KEY_RAML_DEV, "dddd");

            put(Config.KEY_RAML_SKIP, new HashMap<String, Object>() {{
                put(Config.KEY_RAML_SKIP_DEFAULT, "eeee");
                put(Config.KEY_RAML_SKIP_PATTERNS, "ffff");
            }});
        }});

        Map<String, Object> map = new HashMap<>();
        applyMavenPomConfig(mojo, map);

        assertThat(map).hasSize(2);

        Map<String, Object> api = (Map<String, Object>)map.get(Config.KEY_API);
        assertThat(api).hasSize(5);
        assertThat(api.get(Config.KEY_API_TITLE)).isEqualTo("aaa_");
        assertThat(api.get(Config.KEY_API_BASE_URI)).isEqualTo("uri_");
        assertThat(api.get(Config.KEY_API_VERSION)).isEqualTo("version_");
        assertThat(api.get(Config.KEY_API_MEDIA_TYPE)).isEqualTo("bbb_");

        api = (Map<String, Object>)api.get(Config.KEY_API_DOC);
        assertThat(api).hasSize(2);
        assertThat(api.get(Config.KEY_API_DOC_TITLE)).isEqualTo("ccc_");
        assertThat(api.get(Config.KEY_API_DOC_CONTENT)).isEqualTo("ddd_");

        Map<String, Object> raml = (Map<String, Object>)map.get(Config.KEY_RAML);
        assertThat(raml).hasSize(5);
        assertThat(raml.get(Config.KEY_RAML_VERSION)).isEqualTo("aaaa");
        assertThat(raml.get(Config.KEY_RAML_SHOW_EXAMPLE)).isEqualTo("bbbb");
        assertThat(raml.get(Config.KEY_RAML_STOP_ON_ERROR)).isEqualTo("cccc");
        assertThat(raml.get(Config.KEY_RAML_DEV)).isEqualTo("dddd");

        raml = (Map<String, Object>)raml.get(Config.KEY_RAML_SKIP);
        assertThat(raml).hasSize(2);
        assertThat(raml.get(Config.KEY_RAML_SKIP_DEFAULT)).isEqualTo("eeee");
        assertThat(raml.get(Config.KEY_RAML_SKIP_PATTERNS)).isEqualTo("ffff");
    }

    @Test
    public void shouldReturnFirstChildWhenExists() throws Exception {
        Xpp3Dom configuration = new Xpp3Dom("config");
        Xpp3Dom one1 = child(new Xpp3Dom("one"), "aaa");
        Xpp3Dom one2 = new Xpp3Dom("one");
        configuration.addChild(one1);
        configuration.addChild(one2);
        configuration.addChild(new Xpp3Dom("two"));

        mojo.mojoExecution = new MojoExecutionMock();
        mojo.mojoExecution.setConfiguration(configuration);

        assertThat(readConfigNode(mojo, "one")).isSameAs(one1);
    }

    @Test
    public void shouldReturnNullWhenNodeNotFoundExists() throws Exception {
        Xpp3Dom configuration = new Xpp3Dom("config");
        Xpp3Dom one1 = child(new Xpp3Dom("one"), "aaa");
        Xpp3Dom one2 = new Xpp3Dom("one");
        configuration.addChild(one1);
        configuration.addChild(one2);
        configuration.addChild(new Xpp3Dom("two"));

        mojo.mojoExecution = new MojoExecutionMock();
        mojo.mojoExecution.setConfiguration(configuration);

        assertThat(readConfigNode(mojo, "three")).isNull();
    }

    @Test
    public void shouldReturnNullWhenNodeWithNoChildren() throws Exception {
        Xpp3Dom configuration = new Xpp3Dom("config");
        Xpp3Dom one1 = new Xpp3Dom("one");
        Xpp3Dom one2 = new Xpp3Dom("one");
        configuration.addChild(one1);
        configuration.addChild(one2);
        configuration.addChild(new Xpp3Dom("two"));

        mojo.mojoExecution = new MojoExecutionMock();
        mojo.mojoExecution.setConfiguration(configuration);

        assertThat(readConfigNode(mojo, "one")).isNull();
    }

    @Test
    public void shouldIgnoreWhenSourceNotSet() throws Exception {
        Map<String, Object> map = new HashMap<>();
        put(mojo, map, null, "key");
        assertThat(map).isEmpty();

        put(mojo, map, Collections.emptyMap(), "key");
        assertThat(map).isEmpty();
    }

    @Test
    public void shouldAddStringValueWhenStringValue() throws Exception {
        Map<String, Object> src = new HashMap<>();
        src.put("key", "val");
        src.put("foo", "");

        Map<String, Object> map = new HashMap<>();

        put(mojo, map, src, "foo");
        assertThat(map).isEmpty();

        put(mojo, map, src, "key");
        assertThat(map).hasSize(1);
        assertThat(map.get("key")).isEqualTo("val");

        mojo.setEvaluate("");
        map.clear();
        put(mojo, map, src, "key");
        assertThat(map).isEmpty();
    }

    @Test
    public void shouldAddNotStringValueWhenNotStringValue() throws Exception {
        Map<String, Object> src = new HashMap<>();
        src.put("key", 666);

        Map<String, Object> map = new HashMap<>();

        put(mojo, map, src, "key");
        assertThat(map).hasSize(1);
        assertThat(map.get("key")).isEqualTo(666);
    }

    @Test
    public void shouldIgnoreWhenValueIsNull() throws Exception {
        Map<String, Object> src = new HashMap<>();
        src.put("key", 666);

        Map<String, Object> map = new HashMap<>();

        put(mojo, map, src, "foo");
        assertThat(map).isEmpty();
    }

    @Test
    public void shouldReturnResWhenInputIsNull() throws Exception {
        Map<String, Object> res = Collections.emptyMap();

        assertThat(addOrReplaceYaml(null, null)).isNull();
        assertThat(addOrReplaceYaml(res, null)).isSameAs(res);
    }

    @Test
    public void shouldAddOrReplaceMapWhenSourceHasMap() throws Exception {
        Map<String, Object> res = new HashMap<String, Object>() {{
            put("a", new HashMap<String, Object>() {{
                put("b", "b_val");
                put("bb", "bb_val");
            }});
            put("aaa", "aaa_val");
        }};

        Map<String, Object> src = new HashMap<String, Object>() {{
            put("a", new HashMap<String, Object>() {{
                put("b", "b_val_b");
                put("bbb", "bbb_val_bbb");
            }});
            put("aa", new HashMap<String, Object>() {{
                put("b", "b_val_b");
            }});
        }};

        Map<String, Object> map = addOrReplaceYaml(res, src);
        assertThat(map).isSameAs(res);
        assertThat(map).hasSize(3);

        map = (Map<String, Object>)res.get("a");
        assertThat(map).hasSize(3);
        assertThat(map).containsOnlyKeys("b", "bb", "bbb");
        assertThat(map.get("b")).isEqualTo("b_val_b");
        assertThat(map.get("bb")).isEqualTo("bb_val");
        assertThat(map.get("bbb")).isEqualTo("bbb_val_bbb");

        map = (Map<String, Object>)res.get("aa");
        assertThat(map).hasSize(1);
        assertThat(map.get("b")).isEqualTo("b_val_b");

        assertThat(res.get("aaa")).isEqualTo("aaa_val");
    }

    @Test
    public void shouldAddListWhenSourceHasList() throws Exception {
        Map<String, Object> res = new HashMap<String, Object>() {{
            put("a", new ArrayList<Object>() {{
                add("b");
                add("bb");
            }});
            put("aaa", new ArrayList<Object>() {{
                add("b");
                add("bb");
            }});
        }};

        Map<String, Object> src = new HashMap<String, Object>() {{
            put("a", Arrays.asList("b", "bb", "bbb"));
            put("aa", Collections.singletonList("bb"));
        }};

        Map<String, Object> map = addOrReplaceYaml(res, src);
        assertThat(map).isSameAs(res);
        assertThat(map).hasSize(3);

        assertThat((List<Object>)map.get("a")).containsExactly("b", "bb", "bbb");
        assertThat((List<Object>)map.get("aa")).containsExactly("bb");
        assertThat((List<Object>)map.get("aaa")).containsExactly("b", "bb");
    }

    @Test
    public void testGetOrCreateMap() throws Exception {
        Map<String, Object> map = new HashMap<>();

        assertThat(getOrCreateMap(map, "foo")).isEmpty();
        assertThat(map).containsKeys("foo");

        map.put("foo", new HashMap<>());
        ((Map<String, Object>)map.get("foo")).put("key", "val");
        assertThat(getOrCreateMap(map, "foo")).isSameAs(map.get("foo"));
    }

    @Test
    public void shouldIgnoreWhenYamlConfigNotSet() throws Exception {
        setYaml(mojo, "raml_config.yml");
        File file = TestUtils.createTempFile(TestUtils.createTempDir(), "raml.yml");

        FileUtils.write(file, TestUtils.joinStrings(
                "api:",
                "  title: aaa",
                "  baseUri: ccc",
                "  version: ddd",
                "  mediaType: eee"),
                Charset.forName("UTF-8"));

        setBuildResources(mojo, new ArrayList<Resource>() {{
            add(TestUtils.createResource(file.getParentFile().getAbsolutePath()));
        }});

        Map<String, Object> map = new HashMap<>();
        applyYamlConfig(mojo, map);
        assertThat(map).isEmpty();
    }

    @Test
    public void shouldReadYamlWhenYamlConfigSet() throws Exception {
        setYaml(mojo, "raml.yml");
        File file = TestUtils.createTempFile(TestUtils.createTempDir(), "raml.yml");

        FileUtils.write(file, TestUtils.joinStrings(
                "api:",
                "  title: aaa",
                "  baseUri: ccc",
                "  version: ddd",
                "  mediaType: eee"),
                Charset.forName("UTF-8"));

        setBuildResources(mojo, new ArrayList<Resource>() {{
            add(TestUtils.createResource(file.getParentFile().getAbsolutePath()));
        }});

        Map<String, Object> map = new HashMap<>();
        applyYamlConfig(mojo, map);
        assertThat(map).containsOnlyKeys("api");
    }

    @Test
    public void shouldThrowExceptionWhenYamlIncorrect() throws Exception {
        setYaml(mojo, "raml.yml");
        File file = TestUtils.createTempFile(TestUtils.createTempDir(), "raml.yml");

        FileUtils.write(file, TestUtils.joinStrings("ont", "tow", "three"), Charset.forName("UTF-8"));

        setBuildResources(mojo, new ArrayList<Resource>() {{
            add(TestUtils.createResource(file.getParentFile().getAbsolutePath()));
        }});

        Map<String, Object> map = new HashMap<>();
        assertThatThrownBy(() -> applyYamlConfig(mojo, map)).isInstanceOf(Exception.class);
    }

    @Test
    public void testReadConfiguration() throws IOException {
        Xpp3Dom config = new Xpp3Dom("config") {{
            addChild(new Xpp3Dom(Config.KEY_API) {{
                addChild(createDom(Config.KEY_API_TITLE, "aaa"));
                addChild(createDom(Config.KEY_API_BASE_URI, "bbb"));
            }});
            addChild(new Xpp3Dom(Config.KEY_RAML) {{
                addChild(createDom(Config.KEY_RAML_VERSION, "1.0"));
            }});
        }};

        mojo.mojoExecution = new MojoExecutionMock();
        mojo.mojoExecution.setConfiguration(config);

        String actual = mojo.readConfiguration();
        String expected = StringUtils.trim(TestUtils.getResourceAsString("config.yml"));
        TestUtils.assertThatEquals(actual, expected);
    }

    // ========== static ==========

    private static Xpp3Dom createDom(String name, String value) {
        Xpp3Dom dom = new Xpp3Dom(name);
        dom.setValue(value);
        return dom;
    }

    private static void setBuildResources(AbstractRamlConfigMojo obj, List<Resource> resources) throws Exception {
        obj.project.getBuild().setResources(resources);
    }

    private static void setYaml(AbstractRamlConfigMojo obj, String yaml) throws Exception {
        ReflectionUtils.setFieldValue(obj, "yaml", yaml);
    }

    private static File getYaml(AbstractRamlConfigMojo obj) throws Exception {
        return ReflectionUtils.invokeMethod(obj, "getYaml");
    }

    private static void setApi(AbstractRamlConfigMojo obj, Map<String, Object> api) throws NoSuchFieldException, IllegalAccessException {
        ReflectionUtils.setFieldValue(obj, "api", api);
    }

    private static void setRaml(AbstractRamlConfigMojo obj, Map<String, Object> api) throws NoSuchFieldException, IllegalAccessException {
        ReflectionUtils.setFieldValue(obj, "raml", api);
    }

    private static void applyApiPart(AbstractRamlConfigMojo obj, Map<String, Object> map) throws Exception {
        ReflectionUtils.invokeMethod(obj, "applyApiPart", Map.class, map);
    }

    private static void applyRamlPart(AbstractRamlConfigMojo obj, Map<String, Object> map) throws Exception {
        ReflectionUtils.invokeMethod(obj, "applyRamlPart", Map.class, map);
    }

    private static void applyMavenPomConfig(AbstractRamlConfigMojo obj, Map<String, Object> map) throws Exception {
        ReflectionUtils.invokeMethod(obj, "applyMavenPomConfig", Map.class, map);
    }

    private static Xpp3Dom readConfigNode(AbstractRamlConfigMojo obj, String name) throws Exception {
        return ReflectionUtils.invokeMethod(obj, "readConfigNode", String.class, name);
    }

    private static Xpp3Dom child(Xpp3Dom parent, String name) {
        parent.addChild(new Xpp3Dom(name));
        return parent;
    }

    private static void put(AbstractRamlConfigMojo obj, Map<String, Object> map, Map<String, Object> src, String key) throws Exception {
        ReflectionUtils.invokeMethod(obj, "put", Map.class, Map.class, String.class, map, src, key);
    }

    private static Map<String, Object> addOrReplaceYaml(Map<String, Object> res, Map<String, Object> src) throws Exception {
        return ReflectionUtils.invokeStaticMethod(AbstractRamlConfigMojo.class, "addOrReplaceYaml", Map.class, Map.class, res, src);
    }

    private static Map<String, Object> getOrCreateMap(Map<String, Object> map, String key) throws Exception {
        return ReflectionUtils.invokeStaticMethod(AbstractRamlConfigMojo.class, "getOrCreateMap", Map.class, String.class, map, key);
    }

    private static void applyYamlConfig(AbstractRamlConfigMojo obj, Map<String, Object> map) throws Exception {
        ReflectionUtils.invokeMethod(obj, "applyYamlConfig", Map.class, map);
    }
}
