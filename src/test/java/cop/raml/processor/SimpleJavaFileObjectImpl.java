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
package cop.raml.processor;

import org.apache.commons.io.FileUtils;

import javax.tools.SimpleJavaFileObject;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

/**
 * @author Oleg Cherednik
 * @since 15.03.2016
 */
final class SimpleJavaFileObjectImpl extends SimpleJavaFileObject {
    private final File file;

    public SimpleJavaFileObjectImpl(File file, Kind kind) throws URISyntaxException {
        super(new URI("string:///" + file.getName()), kind);
        this.file = file;
    }

    // ========== FileObject ==========

    @Override
    public CharSequence getCharContent(boolean b) throws IOException {
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }
}
