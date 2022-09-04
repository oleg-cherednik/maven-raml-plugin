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

import com.fasterxml.jackson.core.JsonProcessingException;
import cop.raml.RamlVersion;
import cop.raml.TestUtils;
import cop.raml.utils.ReflectionUtils;
import cop.utils.json.JsonUtils;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Oleg Cherednik
 * @since 31.01.2017
 */
@SuppressWarnings("ALL")
public class ConfigTest {
    @Test
    public void testConstants() {
        assertThat(Config.YAML).isEqualTo("raml_config.yml");

        assertThat(Config.KEY_API).isEqualTo("api");
        assertThat(Config.KEY_API_TITLE).isEqualTo("title");
        assertThat(Config.KEY_API_BASE_URI).isEqualTo("baseUri");
        assertThat(Config.KEY_API_VERSION).isEqualTo("version");
        assertThat(Config.KEY_API_MEDIA_TYPE).isEqualTo("mediaType");

        assertThat(Config.KEY_API_DOC).isEqualTo("doc");
        assertThat(Config.KEY_API_DOC_TITLE).isEqualTo("title");
        assertThat(Config.KEY_API_DOC_CONTENT).isEqualTo("content");

        assertThat(Config.KEY_RAML).isEqualTo("raml");
        assertThat(Config.KEY_RAML_VERSION).isEqualTo("version");
        assertThat(Config.KEY_RAML_SHOW_EXAMPLE).isEqualTo("showExample");
        assertThat(Config.KEY_RAML_STOP_ON_ERROR).isEqualTo("stopOnError");
        assertThat(Config.KEY_RAML_DEV).isEqualTo("dev");
        assertThat(Config.KEY_RAML_SKIP).isEqualTo("skip");
        assertThat(Config.KEY_RAML_SKIP_DEFAULT).isEqualTo("default");
        assertThat(Config.KEY_RAML_SKIP_PATTERNS).isEqualTo("patterns");
    }

    @Test
    public void testCreateBase() {
        Map<String, Object> map = Config.createBase();
        assertThat(map).hasSize(2);

        Map<String, Object> part = (Map<String, Object>)map.get(Config.KEY_API);
        assertThat(part).hasSize(1);
        assertThat(part.get(Config.KEY_API_TITLE)).isEqualTo("Title");

        part = (Map<String, Object>)map.get(Config.KEY_RAML);
        assertThat(part).hasSize(3);
        assertThat(part.get(Config.KEY_RAML_VERSION)).isEqualTo(RamlVersion.RAML_0_8.getId());
        assertThat(part.get(Config.KEY_RAML_SHOW_EXAMPLE)).isEqualTo(true);

        part = (Map<String, Object>)part.get(Config.KEY_RAML_SKIP);
        assertThat(part).hasSize(1);
        assertThat(part.get(Config.KEY_RAML_SKIP_DEFAULT)).isEqualTo(true);
    }

    @Test
    public void testRamlVersion() {
        assertThat(Config.builder().build().ramlVersion()).isSameAs(RamlVersion.RAML_0_8);
        assertThat(Config.builder().ramlVersion(null).build().ramlVersion()).isSameAs(RamlVersion.RAML_0_8);
        assertThat(Config.builder().ramlVersion(RamlVersion.RAML_1_0).build().ramlVersion()).isSameAs(RamlVersion.RAML_1_0);
    }

    @Test
    public void shouldIgnoreBlankFieldsWhenBuildConfig() throws Exception {
        Config.Builder builder = Config.builder();
        builder.apiTitle(null).apiTitle("").apiTitle("  ");
        builder.apiBaseUri(null).apiBaseUri("").apiBaseUri("  ");
        builder.apiVersion(null).apiVersion("").apiVersion("  ");
        builder.apiMediaType(null).apiMediaType("").apiMediaType("  ");
        builder.ramlSkipPattern(null).ramlSkipPattern("").ramlSkipPattern("  ");

        Config config = builder.build();
        assertThat(config.apiTitle()).isEqualTo("Title");
        assertThat(config.apiBaseUri()).isNull();
        assertThat(config.apiVersion()).isNull();
        assertThat(config.apiMediaType()).isNull();
        assertThat(getRamlSkip(config)).containsExactly("java\\..+", "com\\.sun\\..+", "org\\.springframework\\..+");
    }

    @Test
    public void shouldSetBooleanWhenBuildConfig() {
        Config.Builder builder = Config.builder();
        builder.ramlShowExample(true);
        builder.ramlStopOnError(true);
        builder.ramlDev(true);

        Config config = builder.build();
        assertThat(config.ramlShowExample()).isTrue();
        assertThat(config.ramlStopOnError()).isTrue();
        assertThat(config.ramlDev()).isTrue();
    }

    @Test
    public void testRamlSkip() {
        Config config = Config.builder().ramlSkipPattern("aaa").ramlSkipPattern("bbb").build();
        assertThat(config.ramlSkip(null)).isFalse();
        assertThat(config.ramlSkip("")).isFalse();
        assertThat(config.ramlSkip("    ")).isFalse();
        assertThat(config.ramlSkip("aaa")).isTrue();
        assertThat(config.ramlSkip("bbb")).isTrue();
        assertThat(config.ramlSkip("ccc")).isFalse();
    }

    @Test
    public void testDoc() {
        Config config = Config.builder().doc(null, null).build();
        assertThat(config.docTitle()).isNull();
        assertThat(config.docContent()).isNull();

        config = Config.builder().doc("", null).build();
        assertThat(config.docTitle()).isNull();
        assertThat(config.docContent()).isNull();

        config = Config.builder().doc(null, "").build();
        assertThat(config.docTitle()).isNull();
        assertThat(config.docContent()).isNull();

        config = Config.builder().doc("  ", "").build();
        assertThat(config.docTitle()).isNull();
        assertThat(config.docContent()).isNull();

        config = Config.builder().doc("  ", "  ").build();
        assertThat(config.docTitle()).isNull();
        assertThat(config.docContent()).isNull();

        config = Config.builder().doc("aaa", "").build();
        assertThat(config.docTitle()).isNull();
        assertThat(config.docContent()).isNull();

        config = Config.builder().doc("", "bbb").build();
        assertThat(config.docTitle()).isNull();
        assertThat(config.docContent()).isNull();

        config = Config.builder().doc("aaa", "bbb").build();
        assertThat(config.docTitle()).isEqualTo("aaa");
        assertThat(config.docContent()).isEqualTo("bbb");
    }

    @Test
    public void testRamlSkipClassName() {
        assertThat(Config.ramlSkipClassName(null)).isFalse();
        assertThat(Config.ramlSkipClassName("")).isFalse();
        assertThat(Config.ramlSkipClassName("  ")).isFalse();
        assertThat(Config.ramlSkipClassName("aaa")).isFalse();
        assertThat(Config.ramlSkipClassName("java.aaa")).isTrue();
        assertThat(Config.ramlSkipClassName("com.sun.aaa")).isTrue();
        assertThat(Config.ramlSkipClassName("org.springframework.aaa")).isTrue();
    }

    @Test
    public void shouldIgnoreBlankYamlWhenParseConfig() throws JsonProcessingException {
        Config.Builder builder = Config.builder();
        String expected = JsonUtils.writeValue(builder.build());

        assertThat(JsonUtils.writeValue(builder.parse((String)null).build())).isEqualTo(expected);
        assertThat(JsonUtils.writeValue(builder.parse("").build())).isEqualTo(expected);
        assertThat(JsonUtils.writeValue(builder.parse("  ").build())).isEqualTo(expected);
    }

    @Test
    public void shouldIgnoreBlankMapWhenParseConfig() throws JsonProcessingException {
        Config.Builder builder = Config.builder();
        String expected = JsonUtils.writeValue(builder.build());

        assertThat(JsonUtils.writeValue(builder.parse((Map<String, Object>)null).build())).isEqualTo(expected);
        assertThat(JsonUtils.writeValue(builder.parse(Collections.emptyMap()).build())).isEqualTo(expected);
    }

    @Test
    public void shouldParseYamlWhenParseConfig() throws Exception {
        String yaml = TestUtils.joinStrings(
                "api:",
                "  title: aaa",
                "  baseUri: ccc",
                "  version: ddd",
                "  mediaType: eee",
                "  doc:",
                "    title: Public",
                "    content: |-",
                "      docContent",
                "raml:",
                "  version: '0.8'",
                "  showExample: true",
                "  skip:",
                "    default: true",
                "    patterns:",
                "    - aaa",
                "    - bbb",
                "    - ccc");

        Config config = Config.builder().parse(yaml).build();
        assertThat(config.apiTitle()).isEqualTo("aaa");
        assertThat(config.apiBaseUri()).isEqualTo("ccc");
        assertThat(config.apiVersion()).isEqualTo("ddd");
        assertThat(config.apiMediaType()).isEqualTo("eee");
        assertThat(config.docTitle()).isEqualTo("Public");
        assertThat(config.docContent()).isEqualTo("docContent");
        assertThat(config.ramlVersion()).isSameAs(RamlVersion.RAML_0_8);
        assertThat(config.ramlShowExample()).isTrue();
        assertThat(config.ramlStopOnError()).isFalse();
        assertThat(config.ramlDev()).isFalse();
        assertThat(getRamlSkip(config)).hasSize(6);
    }

    @Test
    @SuppressWarnings({ "serial", "InnerClassTooDeeplyNested" })
    public void shouldClearSkipWhenConfigSkipDefaultFalse() throws Exception {
        Config config = Config.builder().parse(new HashMap<String, Object>() {{
            put(Config.KEY_RAML, new HashMap<String, Object>() {{
                put(Config.KEY_RAML_SKIP, new HashMap<String, Object>() {{
                    put(Config.KEY_RAML_SKIP_DEFAULT, false);
                }});
            }});
        }}).build();

        assertThat(getRamlSkip(config)).isSameAs(Collections.emptyList());
    }

    @Test
    @SuppressWarnings({ "serial", "InnerClassTooDeeplyNested" })
    public void shouldTreatAsTrueWhenDefaultIsNotSet() throws Exception {
        Config config = Config.builder().parse(new HashMap<String, Object>() {{
            put(Config.KEY_RAML, new HashMap<String, Object>() {{
                put(Config.KEY_RAML_SKIP, new HashMap<String, Object>() {{
                    put(Config.KEY_RAML_SKIP_PATTERNS, Collections.singletonList("aaa"));
                }});
            }});
        }}).build();

        assertThat(getRamlSkip(config)).hasSize(4);
    }

    @Test
    public void shouldRewriteExitedDataWhenParseMap() throws Exception {
        Map<String, Object> map = new LinkedHashMap<String, Object>() {{
            put(Config.KEY_API, new LinkedHashMap<String, Object>() {{
                put(Config.KEY_API_TITLE, "KEY_API_TITLE");
                put(Config.KEY_API_BASE_URI, "KEY_API_BASE_URI");
                put(Config.KEY_API_VERSION, "KEY_API_VERSION");
                put(Config.KEY_API_MEDIA_TYPE, "KEY_API_MEDIA_TYPE");
                put(Config.KEY_API_DOC, new LinkedHashMap<String, String>() {{
                    put(Config.KEY_API_DOC_TITLE, "KEY_API_DOC_TITLE");
                    put(Config.KEY_API_DOC_CONTENT, "KEY_API_DOC_CONTENT");
                }});
            }});
            put(Config.KEY_RAML, new LinkedHashMap<String, Object>() {{
                put(Config.KEY_RAML_VERSION, RamlVersion.RAML_1_0.getId());
                put(Config.KEY_RAML_SHOW_EXAMPLE, false);
                put(Config.KEY_RAML_STOP_ON_ERROR, true);
                put(Config.KEY_RAML_DEV, true);
                put(Config.KEY_RAML_SKIP, new LinkedHashMap<String, Object>() {{
                    put(Config.KEY_RAML_SKIP_DEFAULT, false);
                    put(Config.KEY_RAML_SKIP_PATTERNS, Arrays.asList("aaa", "bbb"));
                }});
            }});
        }};

        Config config = Config.builder().parse(map).build();
        assertThat(config).isNotSameAs(Config.NULL);

        assertThat(config.apiTitle()).isEqualTo("KEY_API_TITLE");
        assertThat(config.apiBaseUri()).isEqualTo("KEY_API_BASE_URI");
        assertThat(config.apiVersion()).isEqualTo("KEY_API_VERSION");
        assertThat(config.apiMediaType()).isEqualTo("KEY_API_MEDIA_TYPE");

        assertThat(config.docTitle()).isEqualTo("KEY_API_DOC_TITLE");
        assertThat(config.docContent()).isEqualTo("KEY_API_DOC_CONTENT");

        assertThat(config.ramlVersion()).isSameAs(RamlVersion.RAML_1_0);
        assertThat(config.ramlShowExample()).isFalse();
        assertThat(config.ramlStopOnError()).isTrue();
        assertThat(config.ramlDev()).isTrue();

        assertThat(getRamlSkip(config)).containsExactly("aaa", "bbb");
    }

    // ========== static ==========

    private static List<String> getRamlSkip(Config config) throws Exception {
        Object res = ReflectionUtils.getFieldValue(config, "ramlSkip");

        if (res == null)
            return null;
        if (((Collection<?>)res).isEmpty())
            return (List<String>)res;
        return ((List<Pattern>)res).stream().map(Pattern::pattern).collect(Collectors.toList());
    }
}
