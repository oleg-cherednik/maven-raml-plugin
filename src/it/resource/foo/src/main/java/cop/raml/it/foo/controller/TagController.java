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

import cop.raml.it.foo.dto.TagDTO;
import cop.raml.it.foo.exception.FooException;
import cop.raml.it.foo.utils.PathUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST end point for tag operations
 *
 * {@url {@link PathUtils#TAG}}
 * {@name Tag}
 */
@RestController
public class TagController {
    /**
     * Retrieve all available tags
     *
     * @return list of {@link TagDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.TAG_LIST, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TagDTO> getTags() throws FooException {
        return null;
    }

    /**
     * Retrieve all available tas with name tag name that contains {@code value}.
     *
     * @param value tag name part
     * @return list of {@link TagDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.TAG_SEARCH, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TagDTO> getTags(@PathVariable String value) throws FooException {
        return null;
    }

    /**
     * Retrieve tags for given {@code scenarioId}.
     *
     * @param scenarioId scenario id
     * @return list of {@link TagDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.TAG_GET_BY_SCENARIO, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TagDTO> getTagsByScenarioId(@PathVariable long scenarioId) throws FooException {
        return null;
    }

    /**
     * Retrieve tags for given {@code analysisId}.
     *
     * @param analysisId analysis id
     * @return list of {@link TagDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.TAG_GET_BY_ANALYSIS, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TagDTO> getTagsByAnalysisId(@PathVariable long analysisId) throws FooException {
        return null;
    }
}
