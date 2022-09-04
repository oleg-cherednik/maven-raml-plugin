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

import cop.raml.it.foo.dto.model.ModelDTO;
import cop.raml.it.foo.exception.FooException;
import cop.raml.it.foo.utils.PathUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * REST end point for model operations
 * <p>
 * {@url {@link PathUtils#MODEL}}
 * {@name Model}
 */
@RestController
public class ModelController {
    /**
     * Retrieve all available models
     *
     * @return not {@code null} list of available models {@link ModelDTO}
     * @throws FooException in case of any problem
     */
    @RequestMapping(value = PathUtils.MODEL_LIST, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ModelDTO> getModels() throws FooException {
        return null;
    }

    /**
     * Retrieve all available models
     *
     * @return not {@code null} list of available models {@link ModelDTO}
     * @throws FooException in case of any problem
     */
    @RequestMapping(value = PathUtils.MODEL_FIND, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ModelDTO> getModelsFilter(@RequestBody Map<String, Collection<String>> metadata) throws FooException {
        return null;
    }

    /**
     * Retrieve {@link ModelDTO} with given {@code modelUuid}
     *
     * @param modelUuid model uuid
     * @return {@link ModelDTO} with given {@code modelUuid} or {@code null} if model with given {@code modelUuid} is not
     * found
     * @throws FooException in case of any problem
     */
    @RequestMapping(value = PathUtils.MODEL_ONE, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ModelDTO getModel(@PathVariable String modelUuid) throws FooException {
        return null;
    }

    /**
     * Create new {@link ModelDTO} with given {@code model} data. Optionally {@code comment} can be specified. This time
     * it uses only {@link ModelDTO#name} and {@link ModelDTO#metadata}. All other model's parts should be created using other
     * services.
     *
     * @param model model data
     * @return created {@link ModelDTO}
     * @throws FooException in case of any problem
     */
    @RequestMapping(value = PathUtils.MODEL_CREATE, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ModelDTO createModel(@RequestBody ModelDTO model) {
        return null;
    }

    /**
     * Update {@link ModelDTO} with new {@code model} data. Optionally {@code comment} can be specified.
     *
     * @param model model with updated parameters
     * @return updated {@link ModelDTO}
     */
    @RequestMapping(value = PathUtils.MODEL_UPDATE, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ModelDTO updateModel(@RequestBody ModelDTO model) {
        return null;
    }

    /**
     * Delete existed {@link ModelDTO} with given {@code modelUuid}. Optionally {@code reason} can be specified.
     *
     * @param modelUuid model uuid
     * @param reason    optional reason
     */
    @RequestMapping(value = PathUtils.MODEL_DELETE, method = RequestMethod.DELETE)
    public void deleteModel(@PathVariable String modelUuid, @RequestParam(required = false) String reason) {
    }
}
