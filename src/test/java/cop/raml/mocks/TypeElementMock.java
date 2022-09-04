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

import org.apache.commons.io.FilenameUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Oleg Cherednik
 * @since 03.01.2017
 */
public class TypeElementMock extends ElementMock implements TypeElement {
    private Name qualifiedName;

    public TypeElementMock(String qualifiedName, ElementKind kind) {
        setKind(kind);
        setQualifiedName(qualifiedName);
    }

    public TypeElementMock setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName != null ? new NameMock(qualifiedName) : null;
        setName(qualifiedName != null ? FilenameUtils.getName(qualifiedName) : null);
        return this;
    }

    // ========== AnnotatedConstructMock ==========

    @Override
    public <A extends Annotation> TypeElementMock addAnnotation(A annotation) {
        return (TypeElementMock)super.addAnnotation(annotation);
    }

    // ========== ElementMock ==========

    @Override
    public TypeElementMock setDocComment(String docComment) {
        return (TypeElementMock)super.setDocComment(docComment);
    }

    @Override
    public TypeElementMock setEnclosingElement(Element owner) {
        return (TypeElementMock)super.setEnclosingElement(owner);
    }

    @Override
    public TypeElementMock addEnclosedElement(Element element) {
        return (TypeElementMock)super.addEnclosedElement(element);
    }

    @Override
    public TypeElementMock setKind(ElementKind kind) {
        return (TypeElementMock)super.setKind(kind);
    }

    // ========== TypeElement ==========

    @Override
    public NestingKind getNestingKind() {
        return null;
    }

    @Override
    public Name getQualifiedName() {
        return qualifiedName;
    }

    @Override
    public TypeMirror getSuperclass() {
        return null;
    }

    @Override
    public List<? extends TypeMirror> getInterfaces() {
        return null;
    }

    @Override
    public List<? extends TypeParameterElement> getTypeParameters() {
        return null;
    }

    // ========== Object ==========

    @Override
    public String toString() {
        return qualifiedName != null ? qualifiedName.toString() : super.toString();
    }
}
