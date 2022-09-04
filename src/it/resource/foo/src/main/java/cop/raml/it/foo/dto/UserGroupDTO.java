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
package cop.raml.it.foo.dto;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class UserGroupDTO { //TEAM
    public static final String NAME = "Name";
    public static final String USERS = "Users";

    private long id;
    private String name;

    private List<UserDTO> users;

    private UserDTO createdBy;
    private Date createdOn;
    private UserDTO modifiedBy;
    private Date modifiedOn;

    private List<ProjectDTO> projects;

    private List<GroupAssignmentDTO> assignments;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserGroupDTO that = (UserGroupDTO) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(modifiedOn, that.modifiedOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, modifiedOn);
    }
}
