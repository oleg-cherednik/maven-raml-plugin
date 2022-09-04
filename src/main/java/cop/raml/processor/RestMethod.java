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

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Oleg Cherednik
 * @since 03.04.2016
 */
public class RestMethod implements BodyPart, DescriptionPart {
    /** GET, POST, PUT, DELETE, PATCH */
    private final String type;

    // root
//    private String protocols;
    // docs
    private String description;
    // parameters
//    private String baseUriParameters;
    private final AutoCreateMap<String, Parameter> queryParameters = new AutoCreateMap<>();
    //    private String headers;
    // responses
    private final AutoCreateMap<Integer, Response> responses = new AutoCreateMap<>();
    // security
//    private String securedBy;
    // body
    private final AutoCreateMap<String, Body> bodies = new AutoCreateMap<>();
    // traits and types
//    private String is;

    public RestMethod(String type) {
        Objects.requireNonNull(type);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public Parameter addQueryParameters(String name) {
        return queryParameters.put(name, Parameter::new);
    }

    public Collection<Parameter> getQueryParameters() {
        return queryParameters.values();
    }

    public Response addResponse(int status) {
        return responses.put(status, Response::new);
    }

    public Collection<Response> getResponses() {
        return responses.values().stream().filter(response -> !response.isEmpty()).collect(Collectors.toList());
    }

    public String getRequestMethod() {
        return type;
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
    public Body addBody(String mediaType) {
        return bodies.put(mediaType, Body::new);
    }

    @Override
    public Collection<Body> getBodies() {
        return bodies.values().stream().filter(body -> !body.isEmpty()).collect(Collectors.toList());
    }

    // ========== static ==========

    private static final String[] TYPES = { "GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE" };

    public static final Comparator<String> SORT_BY_TYPE = (type1, type2) -> {
        Objects.requireNonNull(type1);
        Objects.requireNonNull(type2);

        int t1 = -1;
        int t2 = -1;

        for (int i = 0; i < TYPES.length; i++) {
            t1 = TYPES[i].equalsIgnoreCase(type1) ? i : t1;
            t2 = TYPES[i].equalsIgnoreCase(type2) ? i : t2;
        }

        return Integer.compare(t1, t2);
    };
}
