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

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * @author Oleg Cherednik
 * @since 08.01.2017
 */
public final class ProblemVariableElement extends VariableElementMock {
    public ProblemVariableElement(ElementKind kind) {
        super("<problem>", kind);
    }

    // ========== Element ==========

    @Override
    public TypeMirror asType() {
        throw new RuntimeException("asType()");
    }

    @Override
    public List<? extends Element> getEnclosedElements() {
        throw new RuntimeException("getEnclosedElements()");
    }
}
