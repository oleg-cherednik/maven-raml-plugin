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

import cop.raml.it.foo.dto.EvaluationDTO;
import cop.raml.it.foo.dto.JobParameter;
import cop.raml.it.foo.dto.OutputParameterDTO;
import cop.raml.it.foo.exception.ExceptionBody;
import cop.raml.it.foo.exception.FooException;
import cop.raml.it.foo.utils.PathUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * REST end point for scenario evaluation operations
 * <p>
 * {@url {@link PathUtils#EVALUATION}}
 * {@name Evaluation}
 */
@RestController
public class EvaluationController {
    /**
     * Run scenarios with given {@code scenarioId} for given {@code projectId}.
     *
     * @param projectId  project id
     * @param scenarioId scenario id
     * @return {@link EvaluationDTO} object
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.EVALUATION_RUN_SCENARIO, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public EvaluationDTO runScenarios(@PathVariable long projectId, @PathVariable long scenarioId) throws FooException {
        return null;
    }

    /**
     * Run analysis with given {@code analysisId} for given {@code projectId}.
     *
     * @param projectId  project id
     * @param analysisId analysis id
     * @return list of {@link EvaluationDTO} object
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.EVALUATION_RUN_ANALYSIS, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EvaluationDTO> runAnalysis(@PathVariable long projectId, @PathVariable long analysisId) throws FooException {
        return null;
    }

    /**
     * Retrieve evaluation with given {@code evaluationId}.
     *
     * @param evaluationId evaluation id
     * @return {@link EvaluationDTO} object
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.EVALUATION_ONE, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public EvaluationDTO getEvaluationInfo(@PathVariable long evaluationId) throws FooException {
        return null;
    }

    @Deprecated
    @RequestMapping(value = PathUtils.EVALUATION_GET_OUTPUTS, method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    public Map<String, List<JobParameter>> getJobOutputsForJobs(@RequestBody Collection<String> uuids) throws FooException {
        return null;
    }

    /**
     * Retrieve all available output parameters for given job {@code uuids} grouped by job.
     *
     * @param uuids job uuids
     * @return list of {@link OutputParameterDTO} grouped by job uuid
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.EVALUATION_GET_OUTPUT, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, List<OutputParameterDTO>> getOutputsForJobs(@RequestBody Collection<String> uuids) throws FooException {
        return null;
    }

    /**
     * Retrieve job error with given {@code uuid}
     *
     * @param uuid job uuid
     * @return {@link ExceptionBody} object
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.EVALUATION_GET_ERROR, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ExceptionBody getJobError(@PathVariable String uuid) throws FooException {
        return null;
    }

    /**
     * Rerun jobs with given {@code uuids}.
     *
     * @param uuids job uuids
     * @return list of {@link EvaluationDTO} objects
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.EVALUATION_RERUN, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EvaluationDTO> rerunJobByUUIDs(@RequestBody List<String> uuids) throws FooException {
        return null;
    }

    /**
     * Rerun scenarios with given {@code scenarioId} for given {@code projectId}.
     *
     * @param projectId  project id
     * @param scenarioId scenario id
     * @return {@link EvaluationDTO} object
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.EVALUATION_RERUN_SCENARIO, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public EvaluationDTO rerunScenario(@PathVariable long projectId, @PathVariable long scenarioId) throws FooException {
        return null;
    }

    /**
     * Rerun analysis with given {@code analysisId} for given {@code projectId}.
     *
     * @param projectId  project id
     * @param analysisId analysis id
     * @return list of {@link EvaluationDTO} objects
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.EVALUATION_RERUN_ANALYSIS, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EvaluationDTO> rerunAnalysis(@PathVariable long projectId, @PathVariable long analysisId) throws FooException {
        return null;
    }

    /**
     * Suspend jobs with given {@code uuid}.
     *
     * @param uuids job uuids
     * @return list of current {@link EvaluationDTO}
     * @throws FooException
     */
    @RequestMapping(value = PathUtils.EVALUATION_SUSPEND, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EvaluationDTO> suspendJobs(@RequestBody List<String> uuids) throws FooException {
        return null;
    }

    /**
     * Suspend scenarios with given {@code scenarioId} for given {@code projectId}.
     *
     * @param projectId  project id
     * @param scenarioId scenario id
     * @return {@link EvaluationDTO} object
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.EVALUATION_SUSPEND_SCENARIO, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public EvaluationDTO suspendScenario(@PathVariable long projectId, @PathVariable long scenarioId) throws FooException {
        return null;
    }

    /**
     * Suspend analysis with given {@code analysisId} for given {@code projectId}.
     *
     * @param projectId  project id
     * @param analysisId analysis id
     * @return list of {@link EvaluationDTO} objects
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.EVALUATION_SUSPEND_ANALYSIS, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EvaluationDTO> suspendAnalysis(@PathVariable long projectId, @PathVariable long analysisId) throws FooException {
        return null;
    }
}
