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

import cop.raml.it.foo.dto.model.ModelStepParameterDTO;
import cop.raml.it.foo.exception.FooException;
import cop.raml.it.foo.exception.ModelStepParameterNotFoundException;
import cop.raml.it.foo.utils.PathUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

/**
 * REST end point for model step parameters operations
 */
@RestController
public class ModelStepParameterController {
    /**
     * Retrieve all available model steps parameters for given {@code stepUuid}. To
     *
     * @param stepUuid model step uuid
     * @return {@link ResponseEntity} object with found {@link ModelStepParameterDTO} objects
     * <ul>
     * <li>{@link HttpStatus#OK} - at least one parameter was found</li>
     * <li>{@link HttpStatus#NO_CONTENT} - no parameters for given model step were found</li>
     * </ul>
     */
    @RequestMapping(value = PathUtils.MODEL_STEP_PARAMETER_LIST, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ModelStepParameterDTO>> getModelStepParameters(@PathVariable UUID stepUuid) {
        return null;
    }

    /**
     * Retrieve {@link ModelStepParameterDTO} for given {@code stepUuid} and given {@code paramId}
     *
     * @param stepUuid model step uuid
     * @return {@link ResponseEntity} object with found {@link ModelStepParameterDTO} object
     * <ul>
     * <li>{@link HttpStatus#OK} - model step parameter with given id for given step found</li>
     * <li>{@link HttpStatus#NOT_FOUND} - no model step parameter with given id found</li>
     * </ul>
     */
    @RequestMapping(value = PathUtils.MODEL_STEP_PARAMETER_ONE, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ModelStepParameterDTO> getModelStepParameter(@PathVariable UUID stepUuid,
            @PathVariable long paramId) throws ModelStepParameterNotFoundException {
        return null;
    }

    /**
     * Add new {@code parameters} to the given {@code stepUuid}.
     *
     * @param stepUuid   model step uuid
     * @param parameters list of model step parameters data in the body
     * @return {@link ResponseEntity} object with successfully created parameters
     * <ul>
     * <li>{@link HttpStatus#CREATED} - all given parameters were successfully created</li>
     * <li>{@link HttpStatus#NO_CONTENT} - if no parameters in the body specified</li>
     * <li>{@link HttpStatus#INTERNAL_SERVER_ERROR} - in case of error in part</li>
     * <li><i>header.location</i> - url to retrieve first created parameter in the response list (if any)</li>
     * </ul>
     */
    @RequestMapping(value = PathUtils.MODEL_STEP_PARAMETER_CREATE, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ModelStepParameterDTO>> createModelStepParameters(@PathVariable UUID stepUuid,
            @RequestBody List<ModelStepParameterDTO> parameters) throws URISyntaxException, FooException {
        return null;
    }

    /**
     * Update existed model step parameters with new {@code parameters} data for given {@code stepUuid}. If {@link
     * ModelStepParameterDTO#metadata} is empty, then all existed metadata will be removed for this parameter.
     *
     * @param stepUuid   model step uuid
     * @param parameters list of new data for model step parameters
     * @return {@link ResponseEntity} object with successfully updated parameters id
     * <ul>
     * <li>{@link HttpStatus#OK} - all given parameters were successfully updated</li>
     * <li>{@link HttpStatus#NO_CONTENT} - if no parameters in the body specified</li>
     * <li>{@link HttpStatus#INTERNAL_SERVER_ERROR} - in case of error in part</li>
     * <li><i>header.location</i> - url to retrieve first updated parameter in the response list (if any)</li>
     * </ul>
     */
    @RequestMapping(value = PathUtils.MODEL_STEP_PARAMETER_UPDATE, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ModelStepParameterDTO>> updateModelStepParameters(@PathVariable UUID stepUuid,
            @RequestBody List<ModelStepParameterDTO> parameters) throws URISyntaxException {
        return null;
    }

    /**
     * Delete existed multiple {@link ModelStepParameterDTO} for given {@code stepUuid} and {@code paramIds}. Optionally
     * {@code reason} can be specified.
     *
     * @param stepUuid model step uuid
     * @param reason   optional reason
     */
    @RequestMapping(value = PathUtils.MODEL_STEP_PARAMETER_DELETE, method = RequestMethod.DELETE)
    public void deleteModelSteps(@PathVariable UUID stepUuid, @RequestBody List<Long> paramIds,
            @RequestParam(required = false) String reason) {
    }

    /**
     * Retrieve input parameters for given model step {@code stepUuid}.
     *
     * @param stepUuid model step uuid
     * @return list of {@link ModelStepParameterDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.MODEL_STEP_GET_INPUTS, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ModelStepParameterDTO> getModelStepInputParametersDeprecated(@PathVariable UUID stepUuid)
            throws FooException {
        return null;
    }

    /**
     * Retrieve output parameters for given model step {@code stepUuid}.
     *
     * @param stepUuid model step uuid
     * @return list of {@link ModelStepParameterDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.MODEL_STEP_GET_OUTPUTS, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ModelStepParameterDTO> getModelStepOutputParametersDeprecated(@PathVariable UUID stepUuid)
            throws FooException {
        return null;
    }

    @RequestMapping(value = PathUtils.MODEL_STEP_GET_OUTPUT, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ModelStepParameterDTO getModelStepOutputParameter(@PathVariable UUID stepUuid, @PathVariable long paramId)
            throws FooException {
        return null;
    }
}
