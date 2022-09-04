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

import cop.raml.RamlVersion;
import cop.raml.utils.ThreadLocalContext;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static cop.raml.processor.Config.Builder.PATTERN_JAVA;
import static cop.raml.processor.Config.Builder.PATTERN_SPRING;
import static cop.raml.processor.Config.Builder.PATTERN_SUN;

/**
 * @author Oleg Cherednik
 * @since 10.12.2016
 */
public final class Config {
    public static final Config NULL = builder().build();
    public static final String YAML = "raml_config.yml";

    public static final String KEY_API = "api";
    public static final String KEY_API_TITLE = "title";
    public static final String KEY_API_BASE_URI = "baseUri";
    public static final String KEY_API_VERSION = "version";
    public static final String KEY_API_MEDIA_TYPE = "mediaType";

    public static final String KEY_API_DOC = "doc";
    public static final String KEY_API_DOC_TITLE = "title";
    public static final String KEY_API_DOC_CONTENT = "content";

    public static final String KEY_RAML = "raml";
    public static final String KEY_RAML_VERSION = "version";
    public static final String KEY_RAML_SHOW_EXAMPLE = "showExample";
    public static final String KEY_RAML_STOP_ON_ERROR = "stopOnError";
    public static final String KEY_RAML_DEV = "dev";
    public static final String KEY_RAML_SKIP = "skip";
    public static final String KEY_RAML_SKIP_DEFAULT = "default";
    public static final String KEY_RAML_SKIP_PATTERNS = "patterns";

    // api
    private final String apiTitle;
    private final String apiBaseUri;
    private final String apiVersion;
    private final String apiMediaType;

    // api.documentation
    private final String docTitle;
    private final String docContent;

    // raml
    private final RamlVersion ramlVersion;
    private final boolean ramlShowExample;
    private final boolean ramlStopOnError;
    private final boolean ramlDev;
    private final List<Pattern> ramlSkip;

    public static Builder builder() {
        return new Builder();
    }

    private Config(Builder builder) {
        // api
        apiTitle = builder.apiTitle;
        apiBaseUri = builder.apiBaseUri;
        apiVersion = builder.apiVersion;
        apiMediaType = builder.apiMediaType;

        // api.documentation
        docTitle = builder.docTitle;
        docContent = builder.docContent;

        // raml
        ramlVersion = builder.ramlVersion;
        ramlShowExample = builder.ramlShowExample;
        ramlStopOnError = builder.ramlStopOnError;
        ramlDev = builder.ramlDev;
        ramlSkip = builder.getRamlSkipPatterns();
    }

    // api

    @NotNull
    public String apiTitle() {
        return apiTitle;
    }

    public String apiBaseUri() {
        return apiBaseUri;
    }

    public String apiVersion() {
        return apiVersion;
    }

    public String apiMediaType() {
        return apiMediaType;
    }

    // api.documentation

    public String docTitle() {
        return docTitle;
    }

    public String docContent() {
        return docContent;
    }

    // raml

    public RamlVersion ramlVersion() {
        return ramlVersion;
    }

    public boolean ramlShowExample() {
        return ramlShowExample;
    }

    public boolean ramlStopOnError() {
        return ramlStopOnError;
    }

    public boolean ramlDev() {
        return ramlDev;
    }

    public boolean ramlSkip(String className) {
        if (StringUtils.isNotBlank(className))
            for (Pattern pattern : ramlSkip)
                if (pattern.matcher(className).matches())
                    return true;

        return false;
    }

    // ========== static ==========

    public static Config get() {
        return ThreadLocalContext.getConfig();
    }

    public static boolean ramlSkipClassName(String className) {
        if (StringUtils.isBlank(className))
            return false;
        return PATTERN_JAVA.matcher(className).matches() || PATTERN_SUN.matcher(className).matches() || PATTERN_SPRING.matcher(className).matches();
    }

    @NotNull
    public static Map<String, Object> createBase() {
        Map<String, Object> api = new LinkedHashMap<>();
        api.put(KEY_API_TITLE, "Title");

        Map<String, Object> raml = new LinkedHashMap<>();
        raml.put(KEY_RAML_VERSION, RamlVersion.RAML_0_8.getId());
        raml.put(KEY_RAML_SHOW_EXAMPLE, true);

        Map<String, Object> skip = new LinkedHashMap<>();
        raml.put(KEY_RAML_SKIP, skip);
        skip.put(KEY_RAML_SKIP_DEFAULT, true);

        Map<String, Object> map = new LinkedHashMap<>();
        map.put(KEY_API, api);
        map.put(KEY_RAML, raml);

        return map;
    }

    // ========== Builder ==========

    public static final class Builder {
        static final Pattern PATTERN_JAVA = Pattern.compile("java\\..+");
        static final Pattern PATTERN_SUN = Pattern.compile("com\\.sun\\..+");
        static final Pattern PATTERN_SPRING = Pattern.compile("org\\.springframework\\..+");

        // api
        private String apiTitle;
        private String apiBaseUri;
        private String apiVersion;
        private String apiMediaType;
        // api.documentation
        private String docTitle;
        private String docContent;
        // raml
        private RamlVersion ramlVersion = RamlVersion.RAML_0_8;
        private boolean ramlShowExample = true;
        private boolean ramlStopOnError;
        private boolean ramlDev;
        private final Set<String> ramlSkip = new LinkedHashSet<>();

        private Builder() {
            parse(createBase());
        }

        public Config build() {
            return new Config(this);
        }

        public Builder parse(String yaml) {
            if (StringUtils.isNoneBlank(yaml))
                parse((Map<String, Object>)new Yaml().load(yaml));

            return this;
        }

        public Builder parse(Map<String, Object> map) {
            if (map != null) {
                parseApi((Map<String, Object>)map.get(KEY_API));
                parseRaml((Map<String, Object>)map.get(KEY_RAML));
            }

            return this;
        }

        private void parseApi(Map<String, Object> api) {
            if (api == null)
                return;

            apiTitle((String)api.get(KEY_API_TITLE));
            apiBaseUri((String)api.get(KEY_API_BASE_URI));
            apiVersion((String)api.get(KEY_API_VERSION));
            apiMediaType((String)api.get(KEY_API_MEDIA_TYPE));

            parseDoc((Map<String, Object>)api.get(KEY_API_DOC));
        }

        private void parseDoc(Map<String, Object> doc) {
            if (doc == null)
                return;

            doc((String)doc.get(KEY_API_DOC_TITLE), (String)doc.get(KEY_API_DOC_CONTENT));
        }

        private void parseRaml(Map<String, Object> raml) {
            if (raml == null)
                return;

            String ramlVersion = (String)raml.get(KEY_RAML_VERSION);

            ramlVersion(ramlVersion != null ? RamlVersion.parseId(ramlVersion) : null);
            ramlShowExample(raml.get(KEY_RAML_SHOW_EXAMPLE) == Boolean.TRUE);
            ramlStopOnError(raml.get(KEY_RAML_STOP_ON_ERROR) == Boolean.TRUE);
            ramlDev(raml.get(KEY_RAML_DEV) == Boolean.TRUE);

            parseSkip((Map<String, Object>)raml.get(KEY_RAML_SKIP));
        }

        private void parseSkip(Map<String, Object> skip) {
            if (skip == null)
                return;

            if (skip.containsKey(KEY_RAML_SKIP_DEFAULT)) {
                boolean def = (boolean)skip.get(KEY_RAML_SKIP_DEFAULT);

                for (String skipPattern : Arrays.asList(PATTERN_JAVA.pattern(), PATTERN_SUN.pattern(), PATTERN_SPRING.pattern())) {
                    if (def)
                        ramlSkipPattern(skipPattern);
                    else
                        ramlSkip.remove(skipPattern);
                }
            }

            if (skip.containsKey(KEY_RAML_SKIP_PATTERNS))
                for (String skipPattern : (Iterable<String>)skip.get(KEY_RAML_SKIP_PATTERNS))
                    ramlSkipPattern(skipPattern);
        }

        // api

        public Builder apiTitle(String apiTitle) {
            if (StringUtils.isNotBlank(apiTitle))
                this.apiTitle = apiTitle;
            return this;
        }

        public Builder apiBaseUri(String apiBaseUri) {
            if (StringUtils.isNotBlank(apiBaseUri))
                this.apiBaseUri = apiBaseUri;
            return this;
        }

        public Builder apiVersion(String apiVersion) {
            if (StringUtils.isNotBlank(apiVersion))
                this.apiVersion = apiVersion;
            return this;
        }

        public Builder apiMediaType(String apiMediaType) {
            if (StringUtils.isNotBlank(apiMediaType))
                this.apiMediaType = apiMediaType;
            return this;
        }

        // api.documentation

        public Builder doc(String docTitle, String docContent) {
            if (StringUtils.isNotBlank(docTitle) && StringUtils.isNotBlank(docContent)) {
                this.docTitle = docTitle;
                this.docContent = docContent;
            }
            return this;
        }

        // raml

        public Builder ramlVersion(RamlVersion ramlVersion) {
            if (ramlVersion != null)
                this.ramlVersion = ramlVersion;
            return this;
        }

        public Builder ramlShowExample(boolean ramlShowExample) {
            this.ramlShowExample = ramlShowExample;
            return this;
        }

        public Builder ramlStopOnError(boolean ramlStopOnError) {
            this.ramlStopOnError = ramlStopOnError;
            return this;
        }

        public Builder ramlDev(boolean ramlDev) {
            this.ramlDev = ramlDev;
            return this;
        }

        public Builder ramlSkipPattern(String skipPattern) {
            if (StringUtils.isNotBlank(skipPattern))
                ramlSkip.add(skipPattern);
            return this;
        }

        private List<Pattern> getRamlSkipPatterns() {
            if (ramlSkip.isEmpty())
                return Collections.emptyList();

            List<Pattern> patterns = new ArrayList<>(ramlSkip.size());

            for (String pattern : ramlSkip) {
                if (PATTERN_JAVA.pattern().equals(pattern))
                    patterns.add(PATTERN_JAVA);
                else if (PATTERN_SUN.pattern().equals(pattern))
                    patterns.add(PATTERN_SUN);
                else if (PATTERN_SPRING.pattern().equals(pattern))
                    patterns.add(PATTERN_SPRING);
                else
                    patterns.add(Pattern.compile(pattern));
            }

            return Collections.unmodifiableList(patterns);
        }
    }
}
