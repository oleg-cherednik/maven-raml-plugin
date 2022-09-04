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

import cop.raml.it.foo.exception.FooException;
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
import java.util.UUID;

/**
 * REST end point for model step metadata operations
 */
@RestController
public class ModelStepMetadataController {
    /**
     * Retrieve metadata for given model step {@code stepUuid}
     *
     * @param stepUuid model step uuid
     * @return not {@code null} map with metadata
     */
    @RequestMapping(value = PathUtils.MODEL_STEP_METADATA, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> getModelStepMetadata(@PathVariable UUID stepUuid) {
        return null;
    }

    /**
     * Add metadata for given model step {@code stepUuid}. Optionally {@code comment} can be specified.
     *
     * @param stepUuid model step uuid
     * @param metadata metadata
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.MODEL_STEP_METADATA_CREATE, method = RequestMethod.POST)
    public void addModelStepMetadata(@PathVariable UUID stepUuid, @RequestBody Map<String, String> metadata) throws FooException {
    }

    /**
     * Delete existed metadata specified in {@code metadata} for given model step {@code stepUuid}. Optionally {@code
     * reason} can be specified.
     *
     * @param stepUuid model step uuid
     * @param metadata metadata
     * @param reason   optional reason
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.MODEL_STEP_METADATA_DELETE, method = RequestMethod.DELETE)
    public void deleteModelStepMetadata(@PathVariable UUID stepUuid, @RequestBody List<String> metadata,
            @RequestParam(required = false) String reason) throws FooException {
    }

    /**
     * Update metadata for given model step {@code stepUuid} with new {@code metadata} values data. Optionally {@code
     * comment} can be specified.
     *
     * @param stepUuid model step uuid
     * @param metadata list of new data for model step parameters (if it's <b>empty</b>, then all metadata for given
     *                 model step will be removed)
     * @return current metadata for given {@code stepUuid}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.MODEL_STEP_METADATA_UPDATE, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> updateModelStepMetadata(@PathVariable UUID stepUuid, @RequestBody Map<String, String> metadata) throws FooException {
        return null;
    }
}
