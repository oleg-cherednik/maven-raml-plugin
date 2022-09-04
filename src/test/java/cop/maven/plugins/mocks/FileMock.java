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
package cop.maven.plugins.mocks;

import java.io.File;

/**
 * @author Oleg Cherednik
 * @since 12.02.17
 */
public class FileMock extends File {
    private static final long serialVersionUID = 3322772018316497311L;

    private final boolean file;
    private final String pathname;

    public static FileMock createFile(String pathname) {
        return new FileMock(pathname, true);
    }

    public static FileMock createDirectory(String pathname) {
        return new FileMock(pathname, false);
    }

    private FileMock(String pathname, boolean file) {
        super(pathname);
        this.pathname = pathname;
        this.file = file;
    }

    // ========== File ==========

    @Override

    public String getAbsolutePath() {
        return pathname;
    }

    @Override
    public boolean isDirectory() {
        return !file;
    }

    @Override
    public boolean isFile() {
        return file;
    }
}
