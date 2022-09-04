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

import cop.raml.it.foo.dto.ScenarioDTO;
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

/**
 * REST end point for scenario operations
 */
@RestController
public class ScenarioController {
    /**
     * Retrieve available scenarios for given {@code projectId}.
     *
     * @param projectId project id
     * @return list of {@link ScenarioDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.SCENARIO_LIST, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ScenarioDTO> getScenariosByProjectId(@PathVariable long projectId) throws FooException {
        return null;
    }

    /**
     * Retrieve available scenarios for given {@code projectId} with given {@code status}.
     *
     * @param projectId project id
     * @param status    status
     * @return list of {@link ScenarioDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.SCENARIO_GET_BY_STATUS, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ScenarioDTO> getScenariosByProjectIdAndStatus(@PathVariable long projectId, @PathVariable String status)
            throws FooException {
        return null;
    }

    /**
     * Retrieve scenario with {@code scenarioId} for given {@code projectId}.
     *
     * @param projectId  project id
     * @param scenarioId scenario id
     * @return {@link ScenarioDTO} object
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.SCENARIO_ONE, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ScenarioDTO getScenario(@PathVariable long projectId, @PathVariable long scenarioId) throws FooException {
        return null;
    }

    /**
     * Retrieve scenarios for given {@code projectId} with given {@code tagName}.
     *
     * @param projectId project id
     * @param tagName   tag name
     * @return list of {@link ScenarioDTO} objects
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.SCENARIO_GET_BY_TAG, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ScenarioDTO> getScenarioByTagName(@PathVariable long projectId, @RequestParam String tagName) throws FooException {
        return null;
    }

    /**
     * Retrieve scenarios for given {@code projectId} with given {@code playlistId}.
     *
     * @param projectId  project id
     * @param playlistId playlist id
     * @return list of {@link ScenarioDTO} objects
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.SCENARIO_GET_BY_PLAYLIST, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ScenarioDTO> getScenarioByPlaylistId(@PathVariable long projectId, @PathVariable long playlistId) throws FooException {
        return null;
    }

    /**
     * Create scenario base on {@code scenario} data for given project {@code projectId}.
     *
     * @param projectId project id
     * @param scenario  scenarioDTO data
     * @return {@link ScenarioDTO} object
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.SCENARIO_CREATE, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ScenarioDTO createScenario(@PathVariable long projectId, @RequestBody ScenarioDTO scenario) throws FooException {
        return null;
    }

    /**
     * Update scenario with given {@code scenarioId} for given {@code projectId} with {@code scenario} data.
     *
     * @param projectId  project id
     * @param scenarioId scenario id
     * @param scenario   scenarioDTO data
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.SCENARIO_UPDATE, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ScenarioDTO updateScenario(@PathVariable long projectId, @PathVariable long scenarioId, @RequestBody ScenarioDTO scenario) throws
            FooException {
        return null;
    }

    /**
     * Delete scenario with given {@code scenarioId} for given {@code projectId}. {@code force} flag allows to delete forcibly.
     *
     * @param projectId  project id
     * @param scenarioId scenario id
     * @param force      flag for forced removal
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.SCENARIO_DELETE, method = RequestMethod.DELETE)
    public void scenarioDelete(@PathVariable long projectId, @PathVariable long scenarioId,
                               @RequestParam(value = "force", required = false, defaultValue = "false") boolean force) throws FooException {
    }

    /**
     * Retrieves deleted scenarios for given {@code projectId}.
     *
     * @param projectId project id
     * @return list of {@link ScenarioDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.SCENARIO_GET_DELETED, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ScenarioDTO> getDeletedScenarios(@PathVariable long projectId) throws FooException {
        return null;
    }

    /**
     * Clone scenario to the given project {@code projectId} based on {@code scenario} data.
     *
     * @param projectId project id
     * @param scenario  scenario data
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.SCENARIO_COPY, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ScenarioDTO cloneScenario(@PathVariable long projectId, @RequestBody ScenarioDTO scenario) throws FooException {
        return null;
    }

    /**
     * Rename given scenario {@code scenarioId} for given {@code projectId}. {@code name} is specified in the body.
     *
     * @param projectId  project id
     * @param scenarioId scenario id
     * @param name       scenario name
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.SCENARIO_RENAME, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public void renameScenario(@PathVariable long projectId, @PathVariable long scenarioId, @RequestBody String name)
            throws FooException {
    }
}
