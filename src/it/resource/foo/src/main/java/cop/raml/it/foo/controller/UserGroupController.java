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

import cop.raml.it.foo.dto.UserGroupDTO;
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
 * REST end point for user group operations
 * <p>
 * {@url {@link PathUtils#USER_GROUP}}
 * {@name User Group}
 */
@RestController
public class UserGroupController {
    /**
     * Retrieve all available user groups.
     *
     * @return list of {@link UserGroupDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.USER_GROUP_LIST, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserGroupDTO> listUserGroups() throws FooException {
        return null;
    }

    /**
     * Retrieve all user groups available for current user.
     *
     * @return list of {@link UserGroupDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.USER_GROUP_FOR_ASSIGNMENT, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserGroupDTO> listUserGroupsAvailableForAssignment() throws FooException {
        return null;
    }

    /**
     * Retrieve user group with given {@code userGroupId}.
     *
     * @return {@link UserGroupDTO} object
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.USER_GROUP_ONE, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserGroupDTO getUserGroup(@PathVariable long userGroupId) throws FooException {
        return null;
    }

    /**
     * Create user group based on given {@code userGroup}.
     *
     * @param userGroup user group data
     * @return created {@link UserGroupDTO} object
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.USER_GROUP_CREATE, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserGroupDTO createUserGroup(@RequestBody UserGroupDTO userGroup) throws FooException {
        return null;
    }

    /**
     * Update user group with {@code userGroupId} base ond given {@code userGroup}.
     *
     * @param userGroupId user group id
     * @param userGroup   user group data
     * @return updated {@link UserGroupDTO} object
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.USER_GROUP_UPDATE, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserGroupDTO updateUserGroup(@PathVariable long userGroupId, @RequestBody UserGroupDTO userGroup)
            throws FooException {
        return null;
    }

    /**
     * Delete user group with given {@code userGroupId}
     *
     * @param userGroupId user group id
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.USER_GROUP_DELETE, method = RequestMethod.DELETE)
    public void deleteUserGroup(@PathVariable long userGroupId) throws FooException {
    }
}
