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

import cop.raml.mocks.tools.JCCompilationUnit;
import cop.raml.mocks.tools.JCTree;
import cop.raml.mocks.tools.Pair;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Oleg Cherednik
 * @since 12.12.2016
 */
public class ElementsMock implements Elements {
    private final Map<CharSequence, TypeElement> typeElements = new HashMap<>();
    private final Pair<String, JCCompilationUnit> root = new Pair<>("key", new JCCompilationUnit());

    public void addTypeElement(TypeElement typeElement) {
        typeElements.put(typeElement.toString(), typeElement);
    }

    @SuppressWarnings("unused")
    public Pair<String, JCCompilationUnit> getTreeAndTopLevel(Element element) {
        return root.snd.isEmpty() ? null : root;
    }

    public void addImport(String str) {
        root.snd.addNode(new JCTree(str != null ? String.format("import %s;", str) : null));
    }

    public void addStaticImport(String str) {
        root.snd.addNode(new JCTree(str != null ? String.format("import static %s;", str) : null));
    }

    // ========== Elements ==========

    @Override
    public PackageElement getPackageElement(CharSequence name) {
        return null;
    }

    @Override
    public TypeElement getTypeElement(CharSequence name) {
        return typeElements.get(name);
    }

    @Override
    public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(AnnotationMirror a) {
        return null;
    }

    @Override
    public String getDocComment(Element element) {
        return element instanceof ElementMock ? ((ElementMock)element).getDocComment() : null;
    }

    @Override
    public boolean isDeprecated(Element element) {
        return false;
    }

    @Override
    public Name getBinaryName(TypeElement type) {
        return null;
    }

    @Override
    public PackageElement getPackageOf(Element type) {
        return null;
    }

    @Override
    public List<? extends Element> getAllMembers(TypeElement type) {
        return null;
    }

    @Override
    public List<? extends AnnotationMirror> getAllAnnotationMirrors(Element e) {
        return null;
    }

    @Override
    public boolean hides(Element hider, Element hidden) {
        return false;
    }

    @Override
    public boolean overrides(ExecutableElement overrider, ExecutableElement overridden, TypeElement type) {
        return false;
    }

    @Override
    public String getConstantExpression(Object value) {
        return null;
    }

    @Override
    public void printElements(Writer w, Element... elements) {

    }

    @Override
    public Name getName(CharSequence cs) {
        return null;
    }

    @Override
    public boolean isFunctionalInterface(TypeElement type) {
        return false;
    }
}
