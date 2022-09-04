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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Analysis DTO object description
 */
public class AnalysisDTO {
    public static final String NAME = "Name";
    public static final String NOTES = "Notes";
    public static final String TAGS = "Tags";

    private long id;
    private long projectId;

    private ScenarioDTO scenario;

    /**
     * Name of the analysis
     * <p>
     * {@example The Analysis}
     */
    private String name;
    private String notes;
    private String status;
    private Date createdOn;
    private UserDTO createdBy;
    private Date modifiedOn;
    private UserDTO modifiedBy;
    private Date startedOn;
    private UserDTO executor;
    private Date executedOn;

    private String strategyField;

    private String modelName;
    private String modelStepName;
    private UUID modelUUID;
    private UUID modelStepUUID;

    private String numberOfStrategies;
    private boolean disabled;
    private boolean isDeleted;

    private List<TemplateGroupDTO<AnalysisParameterDTO>> tabs = new ArrayList<>();

    private Set<EvaluationDTO> evaluations = new HashSet<>();

    private Set<TagDTO> tags;
}
