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

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Oleg Cherednik
 * @since 04.01.2017
 */
public class RoundEnvironmentMock implements RoundEnvironment {
    private final Set<Element> rootElements = new HashSet<>();
    private final Set<Element> rootElementsAnnotatedWith = new HashSet<>();

    public void addRootElement(Element rootElement) {
        rootElements.add(rootElement);
    }

    public void addElementAnnotatedWith(Element element) {
        rootElementsAnnotatedWith.add(element);
    }

    public void clearRootElements() {
        rootElements.clear();
    }

    public void clearElementAnnotatedWith() {
        rootElementsAnnotatedWith.clear();
    }

    // ========== RoundEnvironment ==========

    @Override
    public boolean processingOver() {
        return false;
    }

    @Override
    public boolean errorRaised() {
        return false;
    }

    @Override
    public Set<? extends Element> getRootElements() {
        return rootElements;
    }

    @Override
    public Set<? extends Element> getElementsAnnotatedWith(TypeElement a) {
        return null;
    }

    @Override
    public Set<? extends Element> getElementsAnnotatedWith(Class<? extends Annotation> a) {
        return rootElementsAnnotatedWith;
    }
}
