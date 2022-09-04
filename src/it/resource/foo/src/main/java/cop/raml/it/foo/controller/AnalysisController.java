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

import cop.raml.it.foo.dto.AnalysisDTO;
import cop.raml.it.foo.dto.ProjectDTO;
import cop.raml.it.foo.dto.TornadoPlotDTO;
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
 * REST end point for analysis operations
 *
 * {@url {@link PathUtils#ANALYSIS}}
 * {@name Project Analysis}
 */
@RestController
public class AnalysisController {
    /**
     * Retrieve all analyses related to the given {@code projectId}.
     *
     * @param projectId {@link ProjectDTO#id}
     * @return {@status 201} list of {@type arr}{@link AnalysisDTO}
     * asdf
     * sadf
     * sadf
     * <p>
     * <p>
     * asdf
     * asdf
     * @throws FooException in case of any error
     * @localRoot
     */
    @RequestMapping(value = PathUtils.ANALYSIS_LIST, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AnalysisDTO> getAnalysesByProjectId(@PathVariable long projectId) throws FooException {
        return null;
    }

    /**
     * Retrieve all analyses for given {@code projectId} with given {@code status}.
     *
     * @param projectId project id
     * @return list of {@link AnalysisDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.ANALYSIS_GET_BY_STATUS, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AnalysisDTO> getAnalysesByProjectIdAndStatus(@PathVariable long projectId, @PathVariable String status) throws FooException {
        return null;
    }

    /**
     * Retrieve all analyses for given {@code projectId} with given {@code tagName}.
     *
     * @param projectId project id
     * @param tagName   tag name
     * @return list of {@link AnalysisDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.ANALYSIS_GET_BY_TAG, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AnalysisDTO> getAnalysesByTagName(@PathVariable long projectId, @RequestParam String tagName) throws FooException {
        return null;
    }

    /**
     * Retrieve scenarios for given {@code projectId} with given {@code playlistId}.
     *
     * @param projectId  project id
     * @param playlistId playlist id
     * @return list of {@link AnalysisDTO} objects
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.ANALYSIS_GET_BY_PLAYLIST, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AnalysisDTO> getAnalysisByPlaylistId(@PathVariable long projectId, @RequestParam long playlistId)
            throws FooException {
        return null;
    }

    /**
     * Add analysis to project with given {@code projectId}.
     *
     * @param projectId   project id
     * @param analysisDTO analysis
     * @return {@link AnalysisDTO} object
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.ANALYSIS_CREATE, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public AnalysisDTO createAnalysis(@PathVariable long projectId, @RequestBody AnalysisDTO analysisDTO) throws FooException {
        return null;
    }

    /**
     * Clone analysis to the given project {@code projectId} based on {@code analysis} data.
     *
     * @param projectId   project id
     * @param analysisDTO analysis
     * @return {@link AnalysisDTO} object
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.ANALYSIS_COPY, method = RequestMethod.POST,
            produces = { MediaType.APPLICATION_JSON_VALUE })
    public AnalysisDTO cloneAnalysis(@PathVariable long projectId, @RequestBody AnalysisDTO analysisDTO) throws FooException {
        return null;
    }

    /**
     * Update analysis.
     *
     * @param analysisDTO analysis
     * @return {@link AnalysisDTO} object
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.ANALYSIS_UPDATE, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public AnalysisDTO updateAnalysis(@PathVariable long projectId, @RequestBody AnalysisDTO analysisDTO) throws FooException {
        return null;
    }

    /**
     * Get analysis with given {@code analysisId}.
     *
     * @param analysisId analysis id
     * @return {@link AnalysisDTO} object
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.ANALYSIS_ONE, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public AnalysisDTO getAnalysis(@PathVariable long projectId, @PathVariable long analysisId) throws FooException {
        return null;
    }

    /**
     * Returns tornado plot for given analysis {@code analysisId}. Optionally {@code field} can be specified.
     *
     * @param analysisId analysis id (required) {@name Analysis Identification} {@default 666} {@example 777}
     * @param field      field name (optional) {@default 666} {@example 777}
     * @return the analysis info
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.ANALYSIS_GET_TORNADO, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public TornadoPlotDTO generateTornadoPlot(@PathVariable long projectId, @PathVariable long analysisId,
            @RequestParam(required = false) String field) throws FooException {
        return null;
    }

    /**
     * Delete analysis with given {@code analysisId} for given {@code projectId}.
     *
     * @param projectId  project id
     * @param analysisId analysis id
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.ANALYSIS_DELETE, method = RequestMethod.DELETE)
    public void analysisDelete(@PathVariable long projectId, @PathVariable long analysisId) throws FooException {
    }

    /**
     * Retrieves list of deleted analysis for given {@code projectId}.
     *
     * @param projectId project id
     * @return list of {@link AnalysisDTO} that was deleted
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.ANALYSIS_GET_DELETED, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AnalysisDTO> getDeletedAnalysis(@PathVariable long projectId) throws FooException {
        return null;
    }

    /**
     * Rename given analysis {@code analysisId} for given {@code projectId}. {@code name} is specified in the body.
     *
     * @param projectId  project id
     * @param analysisId analysis id
     * @param name       new analysis name
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.ANALYSIS_RENAME, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public void renameAnalysis(@PathVariable long projectId, @PathVariable long analysisId, @RequestBody String name)
            throws FooException {
    }
}
