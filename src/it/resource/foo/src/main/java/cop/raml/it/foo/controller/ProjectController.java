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

import cop.raml.it.foo.dto.ProjectDTO;
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
 * REST end point for project operations
 * <p>
 * {@url {@link PathUtils#PROJECT}}
 * {@name Project}
 */
@RestController
public class ProjectController {

    /**
     * Retrieve all available projects.
     *
     * @return {@status 200}{@type arr}{@link ProjectDTO} sdffffffffdsafsdfs dfsadfffffffffffffffffffff fffffffffffffffffffff
     * fffffffffffffffffffffffffffffffffffff
     * <p>
     * sadf
     * saf
     * sad
     * f
     * asfd
     * <p>
     * <p>
     * asdf
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.PROJECT_LIST, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProjectDTO> getProjects() throws FooException {
        return null;
    }

    /**
     * Retrieve project with given {@code projectId}
     *
     * @param projectId project id
     * @return {@link ProjectDTO} object
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.PROJECT_ONE, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ProjectDTO getProject(@PathVariable long projectId) throws FooException {
        return null;
    }

    /**
     * Retrieve details for given {@code projectId}.
     *
     * @param projectId project id
     * @return {@link ProjectDTO} object
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.PROJECT_GET_DETAILS, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ProjectDTO getProjectDetails(@PathVariable long projectId) throws FooException {
        return null;
    }

    /**
     * Update project with given {@code projectId} with new given {@code project}.
     *
     * @param projectId project id
     * @param project   new project
     * @return updated project in {@link ProjectDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.PROJECT_UPDATE, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ProjectDTO updateProject(@PathVariable long projectId, @RequestBody ProjectDTO project) throws FooException {
        return null;
    }

    /**
     * Create project using given {@code project}.
     *
     * @param project project data
     * @return created {@link ProjectDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.PROJECT_CREATE, method = RequestMethod.POST, consumes = MediaType.APPLICATION_ATOM_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE)
    public ProjectDTO createProject(@RequestBody ProjectDTO project) throws FooException {
        return null;
    }

    /**
     * Copy project {@code project}.
     *
     * @param project project
     * @return {@link ProjectDTO}
     * @throws FooException
     * @throws CloneNotSupportedException
     */
    @RequestMapping(value = PathUtils.PROJECT_COPY, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ProjectDTO copyProject(@RequestBody ProjectDTO project) throws FooException, CloneNotSupportedException {
        return null;
    }

    /**
     * Delete project with given {@code projectId}.
     *
     * @param projectId project id
     */
    @RequestMapping(value = PathUtils.PROJECT_DELETE, method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteProject(@PathVariable long projectId) {
    }

    /**
     * Retrieve deleted projects
     *
     * @return list of {@link ProjectDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.PROJECT_GET_DELETED, method = RequestMethod.GET, consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProjectDTO> getDeletedProjects() throws FooException {
        return null;
    }

    /**
     * Change status to {@code status} for given project {@code projectId}.
     *
     * @param projectId project id
     * @param status    new status
     * @return updated project in {@link ProjectDTO}
     * @throws FooException the exception
     */
    @RequestMapping(value = PathUtils.PROJECT_CHANGE_STATUS, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ProjectDTO changeProjectStatus(@PathVariable long projectId, @PathVariable String status) throws FooException {
        return null;
    }
}
