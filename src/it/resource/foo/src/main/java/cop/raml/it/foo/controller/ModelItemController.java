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

import cop.raml.it.foo.dto.model.ModelItem;
import cop.raml.it.foo.exception.FooException;
import cop.raml.it.foo.utils.PathUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

/**
 * REST end point for model item operations
 *
 * {@url {@link PathUtils#MODEL_ITEM}}
 * {@name Item}
 */
@RestController
public class ModelItemController {
    /**
     * Gets model item with given model step {@code stepUuid}.
     *
     * @param stepUuid model step uuid
     * @return the model step input parameters
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.MODEL_ITEM_GET_LIST, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ModelItem> getModelItems(@PathVariable UUID stepUuid)
            throws FooException {
        return null;
    }

    /**
     * Retrieve content of given model item {@code itemId} for given model step {@code stepUuid}.
     *
     * @param stepUuid model step uuid
     * @param itemId   model item id
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.MODEL_ITEM_GET_ONE, method = RequestMethod.GET)
    public void getModelItemContent(HttpServletResponse response, @PathVariable UUID stepUuid,
            @PathVariable UUID itemId) throws FooException {
    }
}
