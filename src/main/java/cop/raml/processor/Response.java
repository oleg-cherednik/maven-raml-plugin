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

import cop.raml.utils.AutoCreateMap;
import cop.raml.utils.Utils;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author Oleg Cherednik
 * @since 29.04.2016
 */
public class Response implements BodyPart, DescriptionPart, Empty {
    private final int status;

    private String description;
    private final AutoCreateMap<String, Body> bodies = new AutoCreateMap<>();

    public Response(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    // ========== Empty ==========

    @Override
    public boolean isEmpty() {
        return description == null && bodies.values().stream().filter(body -> !body.isEmpty()).count() == 0;
    }

    // ========== DescriptionPart ==========

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getDescription(int offs) {
        return Utils.offs(description, offs, false);
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    // ========== BodyPart ==========

    @Override
    public Body addBody(@NotNull String mediaType) {
        return bodies.put(mediaType, Body::new);
    }

    @Override
    public Collection<Body> getBodies() {
        return bodies.values().stream().filter(body -> !body.isEmpty()).collect(Collectors.toList());
    }
}
