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

import cop.raml.it.foo.dto.CustomTableDTO;
import cop.raml.it.foo.exception.CustomTableNotFoundException;
import cop.raml.it.foo.exception.EntityNotFoundException;
import cop.raml.it.foo.exception.FooException;
import cop.raml.it.foo.exception.NotEnoughPermissionException;
import cop.raml.it.foo.utils.PathUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

/**
 * REST end point for custom tables
 *
 * {@url {@link PathUtils#CUSTOM_TABLE}}
 * {@name Custom Table}
 */
@RestController
public class CustomTableController {
    /**
     * Returns all available custom tables for given {@code stepUuid} and {@code stepParam}.
     *
     * @return {@link ResponseEntity} object with found {@link CustomTableDTO} objects
     * <ul>
     * <li>{@link HttpStatus#OK} - at least one custom table was found</li>
     * <li>{@link HttpStatus#NO_CONTENT} - no custom tables found</li>
     * </ul>
     */
    @RequestMapping(value = PathUtils.CUSTOM_TABLE_MODEL_STEP_PARAM_LIST, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<List<CustomTableDTO>> getAllModelStepParamCustomTables(@PathVariable UUID stepUuid,
            @PathVariable String stepParam) {
        return null;
    }

    /**
     * Returns all available custom tables.
     *
     * @return {@link ResponseEntity} object with found {@link CustomTableDTO} objects
     * <ul>
     * <li>{@link HttpStatus#OK} - at least one custom table was found</li>
     * <li>{@link HttpStatus#NO_CONTENT} - no custom tables found</li>
     * </ul>
     */
    @RequestMapping(value = PathUtils.CUSTOM_TABLE_LIST, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<List<CustomTableDTO>> getAllCustomTables() {
        return null;
    }

    /**
     * Returns all custom tables for given {@code userId} and {@link CustomTableDTO.Scope#USER} scope.
     *
     * @param userId user is
     * @return {@link ResponseEntity} object with found {@link CustomTableDTO} objects
     * <ul>
     * <li>{@link HttpStatus#OK} - at least one custom table was found</li>
     * <li>{@link HttpStatus#NO_CONTENT} - no custom tables found</li>
     * </ul>
     */
    @RequestMapping(value = PathUtils.CUSTOM_TABLE_LIST_BY_USER, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CustomTableDTO>> getUserScopeCustomTables(@PathVariable String userId) {
        return null;
    }

    /**
     * Return all available custom tables with {@link CustomTableDTO.Scope#GLOBAL} scope
     *
     * @return {@link ResponseEntity} object with found {@link CustomTableDTO} objects
     * <ul>
     * <li>{@link HttpStatus#OK} - at least one custom table was found</li>
     * <li>{@link HttpStatus#NO_CONTENT} - no custom tables found</li>
     * </ul>
     */
    @RequestMapping(value = PathUtils.CUSTOM_TABLE_LIST_GLOBAL, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CustomTableDTO>> getGlobalScopeCustomTables() {
        return null;
    }

    /**
     * Return custom table with given {@code customTableId}.
     *
     * @param customTableId custom table id
     * @return {@link ResponseEntity} object with found {@link CustomTableDTO} objects
     * <ul>
     * <li>{@link HttpStatus#OK} - custom table with given id found</li>
     * <li>{@link HttpStatus#NOT_FOUND} - no custom tables with given id found</li>
     * </ul>
     */
    @RequestMapping(value = PathUtils.CUSTOM_TABLE_ONE, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomTableDTO> getCustomTableById(@PathVariable long customTableId)
            throws CustomTableNotFoundException, URISyntaxException {
        return null;
    }

    /**
     * Add given {@code customTable}.
     *
     * @param customTable custom table
     * @return {@link ResponseEntity} object with created custom table id in the body
     * <ul>
     * <li><i>status</i> - {@link HttpStatus#CREATED}</li>
     * <li><i>body</i> - created custom table id</li>
     * <li><i>header.location</i> - url for retrieve created custom table</li>
     * </ul>
     */
    @RequestMapping(value = PathUtils.CUSTOM_TABLE_CREATE, method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Long> addCustomTable(@RequestBody CustomTableDTO customTable) throws URISyntaxException, FooException {
        return null;
    }

    /**
     * Update existed custom table with given {@code customTableId} with given {@code customTable} data
     *
     * @param customTableId id of existed custom table
     * @param customTable   custom table data
     * @return <ul>
     * <li>{@link HttpStatus#OK} - custom table with given id was updated</li>
     * <li>{@link HttpStatus#NOT_FOUND} - custom tables with given id was not found</li>
     * </ul>
     */
    @RequestMapping(value = PathUtils.CUSTOM_TABLE_UPDATE, method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateCustomTable(@PathVariable long customTableId, @RequestBody CustomTableDTO customTable)
            throws FooException {
    }

    /**
     * Delete custom table with given {@code customTableId}.
     *
     * @param customTableId custom table id
     * @return <ul>
     * <li>{@link HttpStatus#OK} - custom table was removed or table with given id was not found</li>
     * <li>{@link HttpStatus#FORBIDDEN} - if user without <t>admin</t> role try to remove not own custom table</li>
     * </ul>
     */
    @RequestMapping(value = PathUtils.CUSTOM_TABLE_DELETE, method = RequestMethod.DELETE)
    public void deleteCustomTable(@PathVariable long customTableId) throws NotEnoughPermissionException {
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> error(EntityNotFoundException e) {
        return null;
    }

    @ExceptionHandler(NotEnoughPermissionException.class)
    public ResponseEntity<String> error(NotEnoughPermissionException e) {
        return null;
    }
}
