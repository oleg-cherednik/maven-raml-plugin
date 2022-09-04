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

import cop.raml.it.foo.dto.RoleDTO;
import cop.raml.it.foo.exception.FooException;
import cop.raml.it.foo.security.SecurityActionType;
import cop.raml.it.foo.utils.PathUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST end point for role operations
 * <p>
 * {@url {@link PathUtils#ROLE}}
 * {@name Role}
 */
@RestController
public class RoleController {
    /**
     * Retrieve list of available roles
     *
     * @return list of {@link RoleDTO} objects
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.ROLE_GET_LIST, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RoleDTO> listRoles() throws FooException {
        return null;
    }

    /**
     * Retrieve list of available user types and their permissions
     *
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.AVAILABLE_USER_TYPES, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, List<SecurityActionType>> listAvailableUserTypes() throws FooException {
        return null;
    }
}
