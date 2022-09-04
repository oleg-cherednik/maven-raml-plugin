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
package cop.raml.it.foo.dto.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum ModelStepTypeEnum {
    EXCEL("Excel"),
    MATHEMATICA("Mathematica"),
    EXCEL_NATIVE("ExcelNative"),
    EXCEL_POI("ExcelPOI"),
    JAVA_SCRIPT("JS");

    private final String id;

    ModelStepTypeEnum(final String id) {
        this.id = id;
    }

    public final String getId() {
        return id;
    }

    // ========== static ==========

    public static ModelStepTypeEnum parseString(String str) {
        str = str.toLowerCase();

        for (ModelStepTypeEnum type : values()) {
            if (type.id.toLowerCase().equals(str)) {
                return type;
            }
        }

        throw new EnumConstantNotPresentException(ModelStepTypeEnum.class, str);
    }

    public static List<String> createIdList() {
        List<String> ids = new ArrayList<>();

        for (ModelStepTypeEnum type : values()) {
            ids.add(type.id);
        }

        return Collections.unmodifiableList(ids);
    }
}
