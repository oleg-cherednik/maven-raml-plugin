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
package cop.raml.utils.javadoc.tags;

import cop.raml.utils.Utils;

import javax.validation.constraints.NotNull;

/**
 * @author Oleg Cherednik
 * @since 20.06.2016
 */
public final class TagParam {
    public static final TagParam NULL = new TagParam(null);

    private final String text;
    private final String name;
    private final String pattern;
    private final String anEnum;
    private final String def;
    private final String example;
    private final TagLink link;
    private final boolean required;

    public static Builder builder() {
        return new Builder();
    }

    private TagParam(Builder builder) {
        text = builder != null ? builder.text : null;
        name = builder != null ? builder.name : null;
        pattern = builder != null ? builder.pattern : null;
        anEnum = builder != null ? builder.anEnum : null;
        def = builder != null ? builder.def : null;
        example = builder != null ? builder.example : null;
        link = builder != null ? builder.link : TagLink.NULL;
        required = builder != null && builder.required == Boolean.TRUE;
    }

    public String getText() {
        return text;
    }

    public String getName() {
        return name;
    }

    public String getPattern() {
        return pattern;
    }

    public String getEnum() {
        return anEnum;
    }

    public String getDefault() {
        return def;
    }

    public String getExample() {
        return example;
    }

    @NotNull
    public TagLink getLink() {
        return link;
    }

    public boolean isRequired() {
        return required;
    }

    // ========== Object ==========

    @Override
    public String toString() {
        if (this == NULL)
            return "<null>";

        StringBuilder buf = new StringBuilder();

        if (text != null)
            Utils.comma(buf).append("text=").append(text);
        if (name != null)
            Utils.comma(buf).append("name=").append(name);
        if (pattern != null)
            Utils.comma(buf).append("pattern=").append(pattern);
        if (anEnum != null)
            Utils.comma(buf).append("enum=").append(anEnum);
        if (def != null)
            Utils.comma(buf).append("default=").append(def);
        if (example != null)
            Utils.comma(buf).append("example=").append(example);
        if (link != TagLink.NULL)
            Utils.comma(buf).append(link);

        Utils.comma(buf).append(required ? "(required)" : "(optional)");

        return buf.toString();
    }

    // ========== builder ==========

    public static final class Builder {
        private String text;
        private String name;
        private String pattern;
        private String anEnum;
        private String def;
        private String example;
        private TagLink link = TagLink.NULL;
        private boolean required;

        private Builder() {
        }

        public TagParam build() {
            boolean empty = text == null;
            empty &= name == null;
            empty &= pattern == null;
            empty &= def == null;
            empty &= example == null;
            empty &= anEnum == null;
            empty &= link == TagLink.NULL;

            return empty ? NULL : new TagParam(this);
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder pattern(String pattern) {
            this.pattern = pattern;
            return this;
        }

        public Builder anEnum(String anEnum) {
            this.anEnum = anEnum;
            return this;
        }

        public Builder def(String def) {
            this.def = def;
            return this;
        }

        public Builder example(String example) {
            this.example = example;
            return this;
        }

        public Builder link(TagLink link) {
            this.link = link != null ? link : TagLink.NULL;
            return this;
        }

        public Builder required(boolean required) {
            this.required = required;
            return this;
        }
    }
}
