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
package cop.maven.plugins;

import cop.maven.plugins.mocks.LogMock;
import cop.maven.plugins.mocks.MavenProjectMock;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

/**
 * @author Oleg Cherednik
 * @since 14.02.2017
 */
public class RamlMojoMock extends RamlMojo {
    private String evaluate;
    private List<String> expr;

    public RamlMojoMock() {
        setLog(new LogMock());
        project = new MavenProjectMock();
    }

    public void setEvaluate(String evaluate) {
        this.evaluate = evaluate;
    }

    public void setEvaluate(String evaluate, String... expr) {
        this.evaluate = evaluate;
        this.expr = ArrayUtils.isEmpty(expr) ? null : Arrays.asList(expr);
    }

    public MavenProjectMock getProject() {
        return (MavenProjectMock)project;
    }

    // ========== AbstractRestToRamlMojo ==========

    @Override
    protected String evaluate(String expr) {
        if (evaluate != null)
            return this.expr == null || this.expr.contains(expr) ? evaluate : expr;
        return expr;
    }
}
