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
package cop.raml.mocks;

import javax.lang.model.element.Name;
import java.util.stream.IntStream;

/**
 * @author Oleg Cherednik
 * @since 30.12.2016
 */
public class NameMock implements Name {
    public static final String ERROR_MARKER = "<error>";

    private final String str;

    public NameMock(String str) {
        this.str = str;
    }

    // ========== Name ==========

    @Override
    public boolean contentEquals(CharSequence cs) {
        return false;
    }

    // ========== CharSequence ==========

    @Override
    public int length() {
        return 0;
    }

    @Override
    public char charAt(int index) {
        return 0;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return null;
    }

    @Override
    public IntStream chars() {
        return null;
    }

    @Override
    public IntStream codePoints() {
        return null;
    }

    // ========== Object ==========

    @Override
    public String toString() {
        if (ERROR_MARKER.equals(str))
            throw new RuntimeException(ERROR_MARKER);
        return str;
    }
}
