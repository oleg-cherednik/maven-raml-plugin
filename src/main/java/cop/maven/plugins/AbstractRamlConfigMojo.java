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

import cop.raml.processor.Config;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.yaml.snakeyaml.Yaml;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Oleg Cherednik
 * @since 27.12.2016
 */
abstract class AbstractRamlConfigMojo extends AbstractRestToRamlMojo {
    /** yaml configuration in the root of resource directory **/
    @Parameter(property = "yaml", defaultValue = Config.YAML, required = true)
    private String yaml;
    @Parameter
    private Map<String, Object> api;
    @Parameter
    private Map<String, Object> raml;

    /**
     * Choose actual yaml configuration file. Returns {@code null} if not configuration found.
     *
     * @return yaml configuration {@link File} or {@code null} if not found
     */
    private File getYaml() {
        // TODO check multiple configuration files in case of multiple resources
        for (Resource resource : (List<Resource>)project.getResources()) {
            File file = new File(resource.getDirectory(), yaml);

            if (file.isFile()) {
                getLog().info("found raml configuration file: " + file.getAbsolutePath());
                return file;
            }
        }

        getLog().info("no raml configuration file found");

        return null;
    }

    private void applyMavenPomConfig(@NotNull Map<String, Object> map) {
        applyApiPart(map);
        applyRamlPart(map);
    }

    private void applyApiPart(@NotNull Map<String, Object> map) {
        checkMavenApiPart();
        Map<String, Object> api = getOrCreateMap(map, Config.KEY_API);

        put(api, this.api, Config.KEY_API_TITLE);
        put(api, this.api, Config.KEY_API_BASE_URI);
        put(api, this.api, Config.KEY_API_VERSION);
        put(api, this.api, Config.KEY_API_MEDIA_TYPE);

        Map<String, Object> doc = getOrCreateMap(api, Config.KEY_API_DOC);
        Map<String, Object> src = (Map<String, Object>)this.api.getOrDefault(Config.KEY_API_DOC, Collections.emptyMap());
        put(doc, src, Config.KEY_API_DOC_TITLE);
        put(doc, src, Config.KEY_API_DOC_CONTENT);
    }

    private void applyRamlPart(@NotNull Map<String, Object> map) {
        raml = CollectionUtils.size(raml) > 0 ? raml : new LinkedHashMap<>();

        Map<String, Object> raml = getOrCreateMap(map, Config.KEY_RAML);
        put(raml, this.raml, Config.KEY_RAML_VERSION);
        put(raml, this.raml, Config.KEY_RAML_SHOW_EXAMPLE);
        put(raml, this.raml, Config.KEY_RAML_STOP_ON_ERROR);
        put(raml, this.raml, Config.KEY_RAML_DEV);

        if (this.raml.containsKey(Config.KEY_RAML_SKIP)) {
            Map<String, Object> skip = getOrCreateMap(raml, Config.KEY_RAML_SKIP);
            Map<String, Object> src = (Map<String, Object>)this.raml.getOrDefault(Config.KEY_RAML_SKIP, Collections.emptyMap());
            put(skip, src, Config.KEY_RAML_SKIP_DEFAULT);
            put(skip, src, Config.KEY_RAML_SKIP_PATTERNS);
        }
    }

    private void checkMavenApiPart() {
        api = CollectionUtils.size(api) > 0 ? api : new LinkedHashMap<>();
        api.putIfAbsent(Config.KEY_API_TITLE, "${project.name}");
        api.putIfAbsent(Config.KEY_API_BASE_URI, "${project.url}");

        Map<String, Object> doc = (Map<String, Object>)api.get(Config.KEY_API_DOC);

        if (doc == null)
            api.put(Config.KEY_API_DOC, doc = new LinkedHashMap<>());

        doc.putIfAbsent(Config.KEY_API_DOC_TITLE, "Public");
        doc.putIfAbsent(Config.KEY_API_DOC_CONTENT, "${project.description}");
    }

    private Xpp3Dom readConfigNode(@NotNull String name) {
        Xpp3Dom[] children = mojoExecution.getConfiguration().getChildren(name);
        Xpp3Dom child = children.length > 0 ? children[0] : null;
        return child != null && child.getChildCount() > 0 ? child : null;
    }

    private void put(@NotNull Map<String, Object> map, Map<String, Object> src, @NotNull String key) {
        if (CollectionUtils.sizeIsEmpty(src))
            return;

        Object value = src.get(key);

        if (value instanceof String) {
            if (StringUtils.isNotBlank((CharSequence)value))
                value = evaluate((String)value);
            if (StringUtils.isNotBlank((CharSequence)value))
                map.put(key, value);
        } else if (value != null)
            map.put(key, value);
    }

    private void applyYamlConfig(@NotNull Map<String, Object> map) throws IOException {
        File file = getYaml();

        if (file == null)
            return;

        try (InputStream in = new FileInputStream(file)) {
            addOrReplaceYaml(map, (Map<String, Object>)new Yaml().load(in));
        }
    }

    // ========== AbstractRestToRamlMojo ==========

    @NotNull
    @Override
    protected String readConfiguration() throws IOException {
        // manual properties parsing, because maven doesn't support map as map's value
        api = MavenUtils.readMap(readConfigNode("api"));
        raml = MavenUtils.readMap(readConfigNode("raml"));

        Map<String, Object> map = Config.createBase();

        applyMavenPomConfig(map);
        applyYamlConfig(map);

        return new Yaml().dumpAsMap(map);
    }

    // ========== static ==========

    private static Map<String, Object> getOrCreateMap(@NotNull Map<String, Object> map, @NotNull String key) {
        Map<String, Object> res = (Map<String, Object>)map.get(key);

        if (res == null)
            map.put(key, res = new LinkedHashMap<>());

        return res;
    }

    private static Map<String, Object> addOrReplaceYaml(@NotNull Map<String, Object> res, @NotNull Map<String, Object> src) {
        if (res == null || src == null)
            return res;

        for (Map.Entry<String, Object> srcEntry : src.entrySet()) {
            if (srcEntry.getValue() instanceof Map && res.get(srcEntry.getKey()) instanceof Map) {
                Map<String, Object> dstChild = (Map<String, Object>)res.get(srcEntry.getKey());
                Map<String, Object> srcChild = (Map<String, Object>)srcEntry.getValue();
                res.put(srcEntry.getKey(), addOrReplaceYaml(dstChild, srcChild));
            } else if (srcEntry.getValue() instanceof List && res.get(srcEntry.getKey()) instanceof List) {
                List<Object> dstChild = (List<Object>)res.get(srcEntry.getKey());
                List<Object> srcChild = (List<Object>)srcEntry.getValue();
                dstChild.addAll(srcChild.stream().filter(item -> !dstChild.contains(item)).collect(Collectors.toList()));
            } else
                res.put(srcEntry.getKey(), srcEntry.getValue());
        }

        return res;
    }
}
