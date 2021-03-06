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
import java.util.UUID;

public class CustomTableDTO {
    private long id;
    private Scope scope;
    private String name;
    private UUID stepUuid;
    private long stepParamId;
    private String stepParamName;
    private String userId;
    private List<Item> columns;
    private List<Item> rows;

    public enum Scope {
        USER,
        GLOBAL
    }

    public static class Item {
        private long id;
        private int number;
        private String name;
        private String alias;
        private boolean hidden;
        private int orderType;
    }
}
