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

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;

/**
 * TODO Remove some constuctors
 *
 * @author Oleg Cherednik
 * @since 03.01.2017
 */
public class VariableElementMock extends ElementMock implements VariableElement {
    private Object constantValue;

    public VariableElementMock(String simpleName, Class<?> cls) {
        this(simpleName, cls.getName());
    }

    public VariableElementMock(String simpleName, Class<?> cls, String docComment) {
        this(simpleName, cls.getName(), docComment);
    }

    public VariableElementMock(String simpleName, String type, String docComment) {
        this(simpleName, type);
        setKind(ElementKind.FIELD);
        setDocComment(docComment);
    }

    public VariableElementMock(String simpleName, String type) {
        setKind(ElementKind.FIELD);
        setName(simpleName);
//        setType(new TypeMirrorMock().setName(type));
    }

    public VariableElementMock(String name, TypeMirror type) {
        setKind(ElementKind.FIELD);
        setName(name);
        setType(type);
    }

    public VariableElementMock(String name, ElementKind kind) {
        setKind(kind);
        setName(name);
    }

    public VariableElementMock setConstantValue(Object constantValue) {
        this.constantValue = constantValue;
        return this;
    }

    // ========== AnnotatedConstructMock ==========

    @Override
    public <A extends Annotation> VariableElementMock addAnnotation(A annotation) {
        return (VariableElementMock)super.addAnnotation(annotation);
    }

    // ========== ElementMock ==========

    @Override
    public VariableElementMock setStatic(boolean isStatic) {
        return (VariableElementMock)super.setStatic(isStatic);
    }

    // ========== VariableElement ==========

    @Override
    public Object getConstantValue() {
        return constantValue;
    }

    // ========== Object ==========

    @Override
    public String toString() {
        return name != null ? name : super.toString();
    }
}
