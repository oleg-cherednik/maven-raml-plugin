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
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Oleg Cherednik
 * @since 03.01.2017
 */
public class ElementMock extends AnnotatedConstructMock implements Element {
    protected ElementKind kind;
    protected TypeMirror type;
    protected Name simpleName;
    protected String name;
    protected String docComment;
    protected boolean isStatic;
    protected final List<Element> enclosedElements = new ArrayList<>();
    protected Element owner;

    public ElementMock() {
    }

    public ElementMock(String name, ElementKind kind) {
        setName(name);
        setKind(kind);
    }

    public ElementMock(String simpleName, Annotation annotation) {
        setName(simpleName);
        addAnnotation(annotation);
    }

    public ElementMock setEnclosingElement(Element owner) {
        this.owner = owner;
        return this;
    }

    public ElementMock setName(String name) {
        this.name = name;
        setSimpleName(FilenameUtils.getBaseName(name));
        return this;
    }

    public ElementMock setSimpleName(String simpleName) {
        this.simpleName = new NameMock(simpleName);
        return this;
    }

    public ElementMock setKind(ElementKind kind) {
        this.kind = kind;
        return this;
    }

    public ElementMock setType(TypeMirror type) {
        this.type = type;
        return this;
    }

    public ElementMock setStatic(boolean isStatic) {
        this.isStatic = isStatic;
        return this;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public ElementMock addEnclosedElement(Element element) {
        enclosedElements.add(element);

        if (element instanceof ElementMock)
            ((ElementMock)element).setEnclosingElement(this);

        return this;
    }

    public ElementMock setDocComment(String docComment) {
        this.docComment = docComment;
        return this;
    }

    public String getDocComment() {
        return docComment;
    }

    // ========== Element ==========

    @Override
    public TypeMirror asType() {
        return type;
    }

    @Override
    public ElementKind getKind() {
        return kind;
    }

    @Override
    public Set<Modifier> getModifiers() {
        return null;
    }

    @Override
    public Name getSimpleName() {
        return simpleName;
    }

    @Override
    public Element getEnclosingElement() {
        return owner;
    }

    @Override
    public List<? extends Element> getEnclosedElements() {
        return enclosedElements;
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> v, P p) {
        return null;
    }

    // ========== Object ==========

    @Override
    public String toString() {
        return name != null ? name : super.toString();
    }
}
