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

import cop.raml.it.foo.dto.model.ModelStepDTO;
import cop.raml.it.foo.dto.model.ModelStepParameterDTO;
import cop.raml.it.foo.exception.FooException;
import cop.raml.it.foo.exception.ModelItemNotFoundException;
import cop.raml.it.foo.utils.PathUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST end point for model step operations
 * <p>
 * {@url {@link PathUtils#MODEL_STEP}}
 * {@name Step}
 */
@RestController
public class ModelStepController {
    /**
     * Retrieve all available model steps for given {@code modelUuid}.
     *
     * @param modelUuid model uuid
     * @return not {@code null} list of available models {@link ModelStepDTO}
     * @throws FooException in case of any problem
     */
    @RequestMapping(value = PathUtils.MODEL_STEPS, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ModelStepDTO> getModelSteps(@PathVariable String modelUuid) throws FooException {
        return null;
    }

    @RequestMapping(value = PathUtils.MODEL_STEP_FIND, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ModelStepDTO> getModelSteps(@RequestBody Map<String, Collection<String>> metadata) throws FooException {
        return null;
    }

    /**
     * Retrieve all model steps with status.
     *
     * @return not {@code null} list of available models {@link ModelStepDTO}
     * @throws FooException in case of any problem
     */
    @RequestMapping(value = PathUtils.MODEL_STEP_LIST, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ModelStepDTO> getModelSteps() throws FooException {
        return null;
    }

    /**
     * Retrieve {@link ModelStepDTO} with given {@code stepUuid}
     *
     * @param stepUuid model step uuid
     * @return {@link ModelStepDTO} with given {@code stepUuid} or {@code null} if model step with given {@code stepUuid}
     * is not found
     * @throws FooException in case of any problem
     */
    @RequestMapping(value = PathUtils.MODEL_STEP_ONE, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ModelStepDTO getModelStep(@PathVariable UUID stepUuid) throws FooException {
        return null;
    }

    /**
     * Create new {@link ModelStepDTO} with given {@code modelStep} data. Optionally {@code comment} can be specified.
     *
     * @param modelStep model step data
     * @return created {@link ModelStepDTO}
     * @throws FooException in case of any problem
     */
    @RequestMapping(value = PathUtils.MODEL_STEP_CREATE, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ModelStepDTO createModelStep(@RequestBody ModelStepDTO modelStep) throws FooException {
        return null;
    }

    /**
     * Delete existed {@link ModelStepDTO} with given {@code stepUuid}. Optionally {@code reason} can be specified.
     *
     * @param stepUuid model step uuid
     * @param reason   optional reason
     */
    @RequestMapping(value = PathUtils.MODEL_STEP_DELETE, method = RequestMethod.DELETE)
    public void deleteModelStep(@PathVariable UUID stepUuid, @RequestParam(required = false) String reason) {
    }

    /**
     * Update {@link ModelStepDTO} with new {@code modelStep} data.
     *
     * @param stepUuid  model step uuid (it must be the same with uuid in the body)
     * @param modelStep new data for model step
     * @return updated {@link ModelStepDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.MODEL_STEP_UPDATE, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ModelStepDTO updateModelStep(@PathVariable UUID stepUuid, @RequestBody ModelStepDTO modelStep)
            throws FooException {
        return null;
    }

    /**
     * Update {@link ModelStepDTO} status with new {@code modelStep} status data.
     *
     * @param stepUuid model step uuid (it must be the same with uuid in the body)
     * @param status   new status for model step
     * @return updated {@link ModelStepDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.MODEL_STEP_CHANGE_STATUS, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ModelStepDTO updateModelStepStatus(@PathVariable UUID stepUuid, @PathVariable String status)
            throws FooException {
        return null;
    }

    /**
     * Retrieves content of the given model step {@code stepUuid}.
     *
     * @param stepUuid model step uuid
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.MODEL_STEP_GET_READONLY_FILES, method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void getReadOnlyModelStep(HttpServletResponse response, @PathVariable UUID stepUuid)
            throws FooException {
    }

    @ExceptionHandler(ModelItemNotFoundException.class)
    public ResponseEntity<ModelItemNotFoundException> handleModelItemNotFound(Exception ex, HttpServletRequest request) {
        return new ResponseEntity<>((ModelItemNotFoundException)ex, HttpStatus.BAD_REQUEST);
    }

    /**
     * @return {@link ResponseEntity} object with created model step {@link ModelStepDTO} with metadata and without
     * parameters
     * <ul>
     * <li>{@link HttpStatus#CREATED} - new model step was successfuly created</li>
     * <li>{@link HttpStatus#NOT_FOUND} - if model step with given {@code stepUuid} is not found</li>
     * <li>{@link HttpStatus#BAD_REQUEST} - if given model step is not <t>Excel</t> type</li>
     * <li>{@link HttpStatus#INTERNAL_SERVER_ERROR} - in case of error in part or problems in converter</li>
     * <li><i>header.location</i> - url for retrieve created model step</li>
     * </ul>
     */
    @RequestMapping(value = PathUtils.MODEL_STEP_CONVERT_JS, method = RequestMethod.POST)
    public ResponseEntity<ModelStepDTO> convertModelStepExcelToJavaScript(@PathVariable UUID stepUuid)
            throws FooException, IOException, URISyntaxException {
        return null;
    }

    /**
     * Convert given Excel document to the list of {@link ModelStepParameterDTO} and optionally (if {@code save} is {@code true}) save it (given
     * parameters replace all existed parameters for given model step). Saving is possible only if parameters don't have any errors.
     *
     * @param file     Excel document
     * @param stepUuid model step uuid
     * @param save     if {@code true}, then save results to database (optionally, default is {@code false})
     * @return not {@code null} list of {@link ModelStepParameterDTO} objects
     * @throws FooException
     */
    @ResponseBody
    @RequestMapping(value = PathUtils.MODEL_STEP_UPLOAD, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ModelStepParameterDTO> createModelStepParametersFromExcel(@RequestBody MultipartFile file, @PathVariable UUID stepUuid,
            @RequestParam(required = false) boolean save) throws FooException {
        return null;
    }

    /**
     * Retrieves content of the given model step {@code stepUuid}.
     *
     * @param stepUuid model step uuid
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.MODEL_STEP_INSPECT_SHEETS, method = RequestMethod.GET)
    public Object inspectModelStepGetSheets(@PathVariable UUID stepUuid)
            throws FooException {
        return null;
    }

    /**
     * Retrieves content of the given model step {@code stepUuid}.
     *
     * @param stepUuid model step uuid
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.MODEL_STEP_INSPECT_CELLS, method = RequestMethod.POST)
    public Object inspectModelStepGetCells(@PathVariable UUID stepUuid, @RequestBody String name)
            throws FooException {
        return null;
    }
}
