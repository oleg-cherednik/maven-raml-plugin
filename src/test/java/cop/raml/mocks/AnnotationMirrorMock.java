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

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Oleg Cherednik
 * @since 25.01.2017
 */
public class AnnotationMirrorMock implements AnnotationMirror {
    private final Map<ExecutableElement, AnnotationValue> values = new LinkedHashMap<>();
    private final DeclaredType type;

    public AnnotationMirrorMock() throws ClassNotFoundException {
        this(new DeclaredTypeMock(MockUtils.createElement(int.class)));
    }

    public AnnotationMirrorMock(DeclaredType type) {
        this.type = type;
    }

    public AnnotationMirrorMock addValue(ExecutableElement element, AnnotationValue value) {
        values.put(element, value);
        return this;
    }
    // ========== AnnotationMirror ==========

    @Override
    public DeclaredType getAnnotationType() {
        return type;
    }

    @Override
    public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValues() {
        return values;
    }
}
