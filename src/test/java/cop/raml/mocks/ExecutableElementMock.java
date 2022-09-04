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

import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oleg Cherednik
 * @since 09.01.2017
 */
public class ExecutableElementMock extends ElementMock implements ExecutableElement {
    private TypeMirror returnType;
    private final List<VariableElement> parameters = new ArrayList<>();

    public ExecutableElementMock(String name, TypeMirror type) {
        super(name, ElementKind.METHOD);
        setType(type);
    }

    public ExecutableElementMock setReturnType(TypeMirror returnType) {
        this.returnType = returnType;
        return this;
    }

    public ExecutableElementMock addParameter(VariableElement var) {
        if (var != null)
            parameters.add(var);
        return this;
    }

    // ========== ElementMock ==========

    @Override
    public ExecutableElementMock setDocComment(String docComment) {
        return (ExecutableElementMock)super.setDocComment(docComment);
    }

    @Override
    public ExecutableElementMock setSimpleName(String simpleName) {
        return (ExecutableElementMock)super.setSimpleName(simpleName);
    }

    @Override
    public ExecutableElementMock setEnclosingElement(Element owner) {
        return (ExecutableElementMock)super.setEnclosingElement(owner);
    }

    @Override
    public ExecutableElementMock setKind(ElementKind kind) {
        return (ExecutableElementMock)super.setKind(kind);
    }

    @Override
    public ExecutableElementMock setStatic(boolean isStatic) {
        return (ExecutableElementMock)super.setStatic(isStatic);
    }

    // ========== AnnotatedConstructMock ==========

    @Override
    public <A extends Annotation> ExecutableElementMock addAnnotation(A annotation) {
        return (ExecutableElementMock)super.addAnnotation(annotation);
    }

    // ========== ExecutableElement ==========

    @Override
    public List<? extends TypeParameterElement> getTypeParameters() {
        return null;
    }

    @Override
    public TypeMirror getReturnType() {
        return returnType;
    }

    @Override
    public List<? extends VariableElement> getParameters() {
        return parameters;
    }

    @Override
    public TypeMirror getReceiverType() {
        return null;
    }

    @Override
    public boolean isVarArgs() {
        return false;
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public List<? extends TypeMirror> getThrownTypes() {
        return null;
    }

    @Override
    public AnnotationValue getDefaultValue() {
        return null;
    }
}
