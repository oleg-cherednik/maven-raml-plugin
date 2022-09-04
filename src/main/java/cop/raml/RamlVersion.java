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
package cop.raml;

/**
 * @author Oleg Cherednik
 * @since 03.12.2016
 */
public enum RamlVersion {
    RAML_0_8("0.8", "raml_08.ftl"),
    RAML_1_0("1.0", "raml_10.ftl");

    private final String id;
    private final String template;

    RamlVersion(String id, String template) {
        this.id = id;
        this.template = template;
    }

    public String getId() {
        return id;
    }

    public String getTemplate() {
        return template;
    }

    // ========== static ==========

    public static RamlVersion parseId(String id) {
        for (RamlVersion ver : values())
            if (ver.id.equals(id))
                return ver;

        throw new EnumConstantNotPresentException(RamlVersion.class, id);
    }
}
