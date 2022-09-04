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
import org.apache.commons.lang3.StringUtils;

/**
 * @author Oleg Cherednik
 * @since 14.04.2016
 */
public class Body implements ExamplePart, RequiredPart, Empty {
    private final String mediaType;
    private final boolean defMediaType;

    private boolean required;
    private String schema;
    private String example;

    public Body(String mediaType) {
        this.mediaType = mediaType;
        defMediaType = mediaType.equals(Config.get().apiMediaType());
    }

    public boolean hasExample() {
        return example == null;
    }

    public String getMediaType() {
        return mediaType;
    }

    public boolean isDef() {
        return defMediaType;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    // ========== Empty ==========

    @Override
    public boolean isEmpty() {
        return example == null && defMediaType;
    }

    // ========== RequiredPart ==========

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public void setRequired(boolean required) {
        this.required = required;
    }

    // ========== ExamplePart ==========

    @Override
    public boolean isSingleLine() {
        return StringUtils.isBlank(example) || !example.contains("\n");
    }

    @Override
    public String getExample() {
        return example;
    }

    @Override
    public String getExample(int offs) {
        return Utils.offs(example, offs, true);
    }

    @Override
    public void setExample(String example) {
        this.example = example;
    }
}
