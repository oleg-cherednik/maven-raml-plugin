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

import com.google.common.io.Files;
import cop.raml.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oleg Cherednik
 * @since 16.12.2016
 */
public final class TestUtils {
    public static final String LINE_SEPARATOR = "\n";   //System.getProperty("line.separator");
    public static final Pattern UUID = Pattern.compile(
            "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$");

    public static String joinStrings(CharSequence... elements) {
        return String.join(LINE_SEPARATOR, elements);
    }

    public static void assertThatEquals(String actual, String expected) {
        String[] arrActual = Utils.splitLine(actual);
        String[] arrExpected = Utils.splitLine(expected);

        for (int i = 0, len = Math.min(arrActual.length, arrExpected.length); i < len; i++) {
            if (!StringUtils.equals(arrActual[i], arrExpected[i]))
                System.err.println("Line: " + (i + 1));
            assertThat(arrActual[i]).isEqualTo(arrExpected[i]);
        }

        assertThat(arrActual.length).isEqualTo(arrExpected.length);
    }

    public static String getResourceAsString(String path) {
        try (InputStream in = TestUtils.class.getClassLoader().getResourceAsStream(path)) {
            return IOUtils.toString(in, StandardCharsets.UTF_8).trim();
        } catch(Exception ignored) {
            return null;
        }
    }

    public static File createTempDir() {
        File file = Files.createTempDir();
        file.deleteOnExit();
        return file;
    }

    public static File createTempDir(File parent, String name) {
        File file = new File(parent, name);
        file.deleteOnExit();
        file.mkdir();
        return file;
    }

    public static File createTempFile(File parent, String name) throws IOException {
        File file = new File(parent, name);
        file.deleteOnExit();
        FileUtils.write(file, "", Charset.forName("UTF-8"));
        return file;
    }

    public static Resource createResource(String path) {
        Resource resource = new Resource();
        resource.setDirectory(path);
        return resource;
    }

    private TestUtils() {
    }
}
