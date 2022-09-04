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

import cop.raml.it.foo.dto.WorkspaceDTO;
import cop.raml.it.foo.exception.FooException;
import cop.raml.it.foo.utils.PathUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST end point for collaboration workspace operations
 */
@RestController
public class WorkspaceController {
    /**
     * Retrieve all available collaboration workspaces
     *
     * @return list of {@link cop.raml.it.foo.dto.WorkspaceDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = "/services/workspace", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<WorkspaceDTO> getAllWorkspaces() throws FooException {
        return null;
    }

    /**
     * Retrieve all available collaboration workspaces available for current user
     *
     * @return list of {@link WorkspaceDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = "/services/workspace/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<WorkspaceDTO> getAllWorkspacesForCurrentUser() throws FooException {
        return null;
    }

    /**
     * Delete workspace with given {@code workspaceId}.
     *
     * @param workspaceId workspace id
     */
    @RequestMapping(value = "/services/workspace/{workspaceId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteWorkspace(@PathVariable long workspaceId) {
    }

    /**
     * Retrieves workspace using given {@code workspaceId}.
     *
     * @param workspaceId workspace id
     * @return retrieved {@link WorkspaceDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = "/services/workspace/{workspaceId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public WorkspaceDTO getWorkspace(@PathVariable long workspaceId) throws FooException {
        return null;
    }

    /**
     * Create workspace using given {@code workspaceDTO}.
     *
     * @param workspaceDTO workspace data
     * @return created {@link WorkspaceDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = "/services/workspace", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public WorkspaceDTO createWorkspace(@RequestBody WorkspaceDTO workspaceDTO) throws FooException {
        return null;
    }

    /**
     * Update workspace with given {@code workspaceId} with new given {@code workspace}.
     *
     * @param workspace workspace to update
     * @return updated workspace in {@link WorkspaceDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.WORKSPACE_UPDATE, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//    @RequestMapping(value = "/services/workspace/{workspaceId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public WorkspaceDTO updateWorkspace(@RequestBody WorkspaceDTO workspace) throws FooException {
        return null;
    }
}
