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

import java.util.List;

public class OutputParameterDTO {

    private long jobParameterId;
    private String name;
    private String label;
    private String value;
    private String type;
    private String group;
    private List<String> rowHeading;
    private List<String> columnHeading;
    private List<String> columnTypes;
    private String rowLabel;
    private String columnLabel;
    private String units;
    private String stepParameterId;

    /**
     * Instantiates a new output parameter entity.
     */
    public OutputParameterDTO() {
    }

    public long getJobParameterId() {
        return jobParameterId;
    }

    public void setJobParameterId(long jobParameterId) {
        this.jobParameterId = jobParameterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<String> getRowHeading() {
        return rowHeading;
    }

    public void setRowHeading(List<String> rowHeading) {
        this.rowHeading = rowHeading;
    }

    public List<String> getColumnHeading() {
        return columnHeading;
    }

    public void setColumnHeading(List<String> columnHeading) {
        this.columnHeading = columnHeading;
    }

    public List<String> getColumnTypes() {
        return columnTypes;
    }

    public void setColumnTypes(List<String> columnTypes) {
        this.columnTypes = columnTypes;
    }

    public String getRowLabel() {
        return rowLabel;
    }

    public void setRowLabel(String rowLabel) {
        this.rowLabel = rowLabel;
    }

    public String getColumnLabel() {
        return columnLabel;
    }

    public void setColumnLabel(String columnLabel) {
        this.columnLabel = columnLabel;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getStepParameterId() {
        return stepParameterId;
    }

    public void setStepParameterId(String stepParameterId) {
        this.stepParameterId = stepParameterId;
    }
}
