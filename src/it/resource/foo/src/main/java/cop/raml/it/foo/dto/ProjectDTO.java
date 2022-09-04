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

import cop.raml.it.foo.dto.model.ModelDTO;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Project DTO object description
 */
public class ProjectDTO {
    /**
     * Unique project id
     * {@display Unique project id}
     * {@pattern \d+}
     * {@example 666}
     */
    private long id;
    /**
     * Name of the project
     * {@example The Project}
     */
    private String name;
    private UserDTO createdBy;
    private UserDTO modifiedBy;
    private Date createdOn;
    private Date modifiedOn;
    private String notes;
    private String status;
    private UUID modelStepUUID;
    private Boolean isEditable;
    private Boolean isDeleted;
    private Set<UserGroupDTO> userGroups = new HashSet<>();
    private Set<UserDTO> owners = new HashSet<>();

    private Set<TemplateDTO> templates = new HashSet<>();

    /** {@link ModelDTO#uuid} */
    private String modelUUID;
    private String modelName;
    private String modelStepName;
    private Long scenarioCount;
    private Long analysesCount;
    private Integer completedScenarioCount;
    private Integer completedAnalysesCount;
}
