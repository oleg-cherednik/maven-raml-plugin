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

import cop.raml.TestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oleg Cherednik
 * @since 01.02.2017
 */
public class RamlMojoIT {
    @BeforeClass
    public static void checkMavenHome() {
        assertThat(getMavenHome()).isNotEmpty();
    }

    @Test
    public void testRamlGoal() throws Exception {
        File pom = getResourceFile("foo/pom.xml");
        assertThat(pom.exists()).isTrue();
        assertThat(runMaven(pom, "raml:raml").getExitCode()).isZero();

        String actualConfig = readResource("/foo/target/docs/raml_config.yml");
        String actualRaml = readResource("/foo/target/docs/foo_1.0.raml");

        String expectedConfig = readResource("/raml_config.yml");
        String expectedRaml = readResource("/foo_1.0.raml");

        TestUtils.assertThatEquals(actualConfig, expectedConfig);
        TestUtils.assertThatEquals(actualRaml, expectedRaml);
    }

    private static String readResource(String name) throws IOException {
        return IOUtils.toString(RamlMojoIT.class.getResourceAsStream(name), "UTF-8");
    }

    // ========== static ==========

    private static File getResourceFile(String name) {
        return new File(FilenameUtils.concat("src/it/resource", name)).getAbsoluteFile();
    }

    @SuppressWarnings("CallToSystemGetenv")
    private static String getMavenHome() {
        return StringUtils.defaultString(System.getenv("MAVEN_HOME"), System.getenv("M2_HOME"));
    }

    private static InvocationResult runMaven(File pom, String goal) throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(pom);
        request.setGoals(Arrays.asList("clean", goal));

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(getMavenHome()));
        invoker.setWorkingDirectory(pom.getParentFile());
        return invoker.execute(request);
    }
}
