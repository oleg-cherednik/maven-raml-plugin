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

import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.AnnotationMirror;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Oleg Cherednik
 * @since 03.01.2017
 */
public abstract class AnnotatedConstructMock implements AnnotatedConstruct {
    private final Map<Class<? extends Annotation>, Annotation> annotations = new HashMap<>();
    private final List<AnnotationMirror> annotationMirrors = new ArrayList<>();

    public <A extends Annotation> AnnotatedConstructMock addAnnotation(A annotation) {
        annotations.put(annotation.annotationType(), annotation);
        return this;
    }

    public AnnotatedConstructMock addAnnotationMirror(AnnotationMirror annotationMirror) {
        annotationMirrors.add(annotationMirror);
        return this;
    }

    // ========== AnnotatedConstruct ==========

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return annotationMirrors;
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return (A)annotations.get(annotationType);
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
        return null;
    }
}
