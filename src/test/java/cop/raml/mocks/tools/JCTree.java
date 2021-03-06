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
package cop.raml.mocks.tools;

/**
 * @author Oleg Cherednik
 * @since 10.01.2017
 */
@SuppressWarnings("ClassNamingConvention")
public final class JCTree {
    private final String path;

    public JCTree(String path) {
        this.path = path;
    }

    // ========== Object ==========

    @Override
    public String toString() {
        if (path == null)
            throw new RuntimeException("path == null");
        return path;
    }
}
