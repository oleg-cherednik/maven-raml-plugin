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

import cop.raml.it.foo.dto.ProjectFileDTO;
import cop.raml.it.foo.exception.FooException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

/**
 * REST end point for operations with project's files
 */
@RestController
public class ProjectFileController {
    /**
     * POST /services/project/{projectId}/file --> Save file to project with given {@code projectId} and {@code notes}.
     *
     * @param projectId project id
     * @param file      {@link MultipartFile} file
     * @param notes     notes to file
     * @return {@link ProjectFileDTO}
     * @throws FooException
     */
    @RequestMapping(value = "/services/project/{projectId}/file", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ProjectFileDTO saveProjectFile(@PathVariable("projectId") long projectId, @RequestParam MultipartFile file,
                                          @RequestParam(required = false, defaultValue = "") String notes) throws FooException {
        return null;
    }

    /**
     * GET /services/project/{projectId}/file --> Get file list of project with given {@code projectId}.
     *
     * @param projectId project id
     * @return list of {@link ProjectFileDTO}
     * @throws FooException
     */
    @RequestMapping(value = "/services/project/{projectId}/file", method = RequestMethod.GET)
    public List<ProjectFileDTO> getProjectFilesList(@PathVariable("projectId") long projectId) throws FooException {
        return null;
    }

    /**
     * GET /services/project/{projectId}/file/{uuid} --> Get project file with given {@code projectId} and {@code UUID}.
     *
     * @param response  {@link HttpServletResponse} http response
     * @param projectId project id
     * @param uuid      {@link UUID}
     * @throws FooException
     */
    @RequestMapping(value = "/services/project/{projectId}/file/{UUID}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getProjectFile(HttpServletResponse response, @PathVariable("projectId") long projectId,
                               @PathVariable("UUID") UUID uuid) throws FooException {
    }

    /**
     * DELETE /services/project/{projectId}/file/{uuid} --> Remove file from project by {@code projectId} and {@code UUID}.
     *
     * @param projectId project id
     * @param uuid      {@link UUID}
     * @throws FooException
     */
    @RequestMapping(value = "/services/project/{projectId}/file/{UUID}", method = RequestMethod.DELETE)
    public void deleteProjectFile(@PathVariable("projectId") long projectId, @PathVariable("UUID") UUID uuid) throws FooException {
    }

    /**
     * POST /services/project/{projectId}/file/{uuid} --> Update file in project.
     * @param projectId project id
     * @param uuid {@link UUID}
     * @param file {@link MultipartFile}
     * @param notes notes
     * @throws FooException
     */
    @RequestMapping(value = "/services/project/{projectId}/file/{UUID}", method = RequestMethod.POST)
    public ProjectFileDTO updateProjectFile(@PathVariable("projectId") long projectId, @PathVariable("UUID") UUID uuid,
                                  @RequestParam MultipartFile file, @RequestParam(required = false, defaultValue = "") String notes)
            throws FooException {
        return null;
    }
}
