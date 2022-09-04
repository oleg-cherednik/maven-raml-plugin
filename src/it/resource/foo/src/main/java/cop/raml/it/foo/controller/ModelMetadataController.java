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
package cop.raml.it.foo.controller;

import cop.raml.it.foo.utils.PathUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST end point for model item metadata operations
 */
@RestController
public class ModelMetadataController {
    /**
     * Retrieve metadata for given model {@code modelUuid}
     *
     * @param modelUuid model uuid
     * @return not {@code null} map with metadata
     */
    @RequestMapping(value = PathUtils.MODEL_METADATA, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> getModelMetadata(@PathVariable String modelUuid) {
        return null;
    }

    /**
     * Add metadata for given model {@code modelUuid}. Optionally {@code comment} can be specified.
     *
     * @param modelUuid model uuid
     * @param metadata  metadata
     */
    @RequestMapping(value = PathUtils.MODEL_METADATA_CREATE, method = RequestMethod.POST)
    public void addModelMetadata(@PathVariable String modelUuid, @RequestBody Map<String, String> metadata) {
    }

    /**
     * Delete existed metadata specified in {@code metadata} for given model {@code modelUuid}. Optionally {@code
     * reason} can be specified.
     *
     * @param modelUuid model uuid
     * @param metadata  metadata
     * @param reason    optional reason
     */
    @RequestMapping(value = PathUtils.MODEL_METADATA_DELETE, method = RequestMethod.DELETE)
    public void deleteModelMetadata(@PathVariable String modelUuid, @RequestBody List<String> metadata,
            @RequestParam(required = false) String reason) {
    }

    /**
     * Update metadata for given model {@code modelUuid} with new {@code metadata} values data. Optionally {@code
     * comment} can be specified.
     *
     * @param modelUuid model uuid
     * @return current metadata for given {@code modelUuid}
     */
    @RequestMapping(value = PathUtils.MODEL_METADATA_UPDATE, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> updateModelMetadata(@PathVariable String modelUuid, @RequestBody Map<String, String> metadata) {
        return null;
    }
}
