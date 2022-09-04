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

import cop.raml.it.foo.dto.TemplateDTO;
import cop.raml.it.foo.exception.FooException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST end point for template operations
 */
@RestController
public class TemplateController {
    /**
     * Retrieve all available templates.
     *
     * @return not {@code null} list of available templates {@link TemplateDTO}
     * @throws FooException in case of any problem
     */
    @RequestMapping(value = "/services/template", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TemplateDTO> getTemplates() throws FooException {
        return null;
    }

    /**
     * Retrieve all available templates for given {@code stepUuid}.
     *
     * @param stepUuid modelStep uuid
     * @return not {@code null} list of available templates {@link TemplateDTO}
     * @throws FooException in case of any problem
     */
    @RequestMapping(value = "/services/step/{stepUuid}/template", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TemplateDTO> getTemplatesByModelStep(@PathVariable UUID stepUuid) throws FooException {
        return null;
    }


    /**
     * Retrieve PUBLISHED templates for given {@code modelStepUuid}.
     *
     * @param stepUuid modelStep uuid
     * @return not {@code null} list of available templates {@link TemplateDTO}
     * @throws FooException in case of any problem
     */
    @RequestMapping(value = "/services/step/{stepUuid}/template/published", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TemplateDTO> getPublishedTemplatesByModelStep(@PathVariable UUID stepUuid) throws FooException {
        return null;
    }

    /**
     * Retrieve all PUBLISHED templates for given {@code projectId}.
     *
     * @param projectId project ID
     * @return not {@code null} list of available templates {@link TemplateDTO}
     * @throws FooException in case of any problem
     */
    @RequestMapping(value = "/services/project/{projectId}/template", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TemplateDTO> getTemplatesByProject(@PathVariable long projectId) throws FooException {
        return null;
    }

    /**
     * Retrieve default template for given {@code projectId}.
     *
     * @param projectId project ID
     * @return available template {@link TemplateDTO}
     * @throws FooException in case of any problem
     */
    @RequestMapping(value = "/services/project/{projectId}/template/default", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public TemplateDTO getDefaultTemplateByProject(@PathVariable long projectId) throws FooException {
        return null;
    }


    /**
     * Retrieve details for given {@code templateId}.
     *
     * @param templateId template id
     * @return {@link TemplateDTO} object
     * @throws FooException in case of any error
     */
    @RequestMapping(value = "/services/template/{templateId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public TemplateDTO getTemplateDetails(@PathVariable long templateId) throws FooException {
        return null;
    }

    /**
     * Create template using given {@code template}.
     *
     * @param template template data
     * @return created {@link TemplateDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = "/services/template", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public TemplateDTO createTemplate(@RequestBody TemplateDTO template) throws FooException {
        return null;
    }

    /**
     * Update template with given {@code templateId} with new given {@code template}.
     *
     * @param templateId template id
     * @param template   new template
     * @return updated template in {@link TemplateDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = "/services/template/{templateId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public TemplateDTO updateTemplate(@PathVariable long templateId, @RequestBody TemplateDTO template) throws FooException {
        return null;
    }
}
