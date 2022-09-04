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
 * REST end point for user operations
 *
 * {@url {@link PathUtils#USER}}
 * {@name User}
 */
@RestController
public class UserController {
    /**
     * Retrieve all users.
     *
     * @return list of {@link UserDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.USER_LIST, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDTO> listUsers() throws FooException {
        return null;
    }

    /**
     * Retrieve active users.
     *
     * @return list of {@link UserDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = "/services/user/active", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDTO> listActiveUsers() throws FooException {
        return null;
    }

    /**
     * Retrieve inactive users.
     *
     * @return list of {@link UserDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = "/services/user/inactive", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDTO> listInactiveUsers() throws FooException {
        return null;
    }

    /**
     * Create user based on given {@code userDTO}.
     *
     * @param userDTO user data
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.USER_CREATE, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void createUser(@RequestBody UserDTO userDTO) throws FooException {
    }

    /**
     * Update user with {@code userId} base ond given {@code userDTO}. {@code user.userId} will be replaced with given
     * {@code userId}.
     *
     * @param userId user id
     * @param user   user data
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.USER_UPDATE, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateUser(@PathVariable String userId, @RequestBody UserDTO user) throws FooException {
    }

    /**
     * Deactivate user with given {@code userId}.
     *
     * @param userId user id
     * @throws FooException in case of any error
     */
    @RequestMapping(value = "/services/user/{userId}/deactivate", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deactivateUser(@PathVariable String userId) throws FooException {
    }

    /**
     * Activate user with given {@code userId}.
     *
     * @param userId user id
     * @throws FooException in case of any error
     */
    @RequestMapping(value = "/services/user/{userId}/activate", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public void activateUser(@PathVariable String userId) throws FooException {
    }

    /**
     * Retrieve user with given {@code userId}.
     *
     * @param userId user id
     * @return {@link UserDTO} object
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.USER_ONE, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO getUser(@PathVariable String userId) throws FooException {
        return null;
    }

    /**
     * Retrieve current user
     *
     * @return {@link UserDTO} object
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.USER_CURRENT, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO getCurrent() throws FooException {
        return null;
    }
}
