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
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotNull;

/**
 * @author Oleg Cherednik
 * @since 20.06.2016
 */
public final class TagReturn {
    public static final TagReturn NULL = new TagReturn(null);

    private final int status;
    private final String text;
    private final TagLink link;
    private final boolean array;

    public static Builder createBuilder() {
        return new Builder();
    }

    private TagReturn(Builder builder) {
        status = builder != null ? builder.status : -1;
        text = builder != null ? builder.text : null;
        link = builder != null ? builder.link : TagLink.NULL;
        array = builder != null && Boolean.TRUE.equals(builder.array);
    }

    public int getStatus() {
        return status;
    }

    public String getText() {
        return text;
    }

    @NotNull
    public TagLink getLink() {
        return link;
    }

    public boolean isArray() {
        return array;
    }

    // ========== Object ==========

    @Override
    public String toString() {
        if (this == NULL)
            return "<null>";

        StringBuilder buf = new StringBuilder();

        if (status > 0)
            Utils.comma(buf).append("status=").append(status);
        if (text != null)
            Utils.comma(buf).append("text='").append(text).append('\'');
        if (link != TagLink.NULL)
            Utils.comma(buf).append(link);
        if (array)
            Utils.comma(buf).append("(array)");

        return buf.toString();
    }

    // ========== builder ==========

    public static final class Builder {
        private int status = -1;
        private String text;
        private TagLink link = TagLink.NULL;
        private Boolean array;

        private Builder() {
        }

        public TagReturn build() {
            return status > 0 || text != null || link != TagLink.NULL || array != null ? new TagReturn(this) : NULL;
        }

        public Builder status(String status) {
            this.status = StringUtils.isNotBlank(status) ? Integer.parseInt(status) : HttpStatus.OK.value();
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder link(TagLink link) {
            this.link = link != null ? link : TagLink.NULL;
            return this;
        }

        public Builder array(boolean array) {
            this.array = array;
            return this;
        }
    }
}
