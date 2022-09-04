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

import cop.raml.it.foo.dto.UserDTO;
import cop.raml.it.foo.dto.UsernameAndPassword;
import cop.raml.it.foo.utils.PathUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST end point for authentication operations
 * <p>
 * {@url {@link PathUtils#AUTH}}
 * {@name Authentication}
 */
@RestController
public class AuthController {
    /**
     * Login with given username and password {@code auth}
     *
     * @param auth username and password
     * @return {@link UserDTO} or {@code null} id user with given username is not found
     */
    @RequestMapping(value = PathUtils.AUTH_LOGIN, method = RequestMethod.POST)
    public UserDTO authenticate(@RequestBody UsernameAndPassword auth) {
        return null;
    }

    /**
     * Logout current user
     */
    @RequestMapping(value = PathUtils.AUTH_LOGOUT, method = RequestMethod.POST)
    public void logout() {
    }
}
