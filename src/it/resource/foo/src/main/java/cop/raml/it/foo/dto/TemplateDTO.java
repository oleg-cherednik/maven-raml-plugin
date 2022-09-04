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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TemplateDTO {

    private long id;
    private String name;

    private UserDTO createdBy;
    private Date createdOn;
    private UserDTO modifiedBy;
    private Date modifiedOn;

    private String version;
    private String notes;
    private String status;
    private UUID modelStepUUID;
    private Boolean isDeleted = false;

    // for project only
    private Boolean isDefault;

    // Additional fields
    private UUID modelUUID;
    private String modelName;
    private String modelStepName;

    private List<ProjectDTO> projects;

    private List<TemplateGroupDTO<TemplateParameterDTO>> tabs = new ArrayList<>();

}
