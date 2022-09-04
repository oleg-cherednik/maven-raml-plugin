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
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;

/**
 * @author Oleg Cherednik
 * @since 29.12.2016
 */
public class TypeMirrorMock extends AnnotatedConstructMock implements TypeMirror {
    public Element tsym;
    private TypeKind kind;
    @SuppressWarnings({ "unused", "FieldCanBeLocal" })
    private TypeMirror elemtype;
    private String name;

    public TypeMirrorMock(Element tsym) {
        this(tsym, getTypeKind(tsym));
    }

    public TypeMirrorMock(Element tsym, TypeKind kind) {
        setElement(tsym);
        setKind(kind);
    }

    public TypeMirrorMock setElement(Element tsym) {
        this.tsym = tsym;
        return this;
    }

    public TypeMirrorMock setKind(TypeKind kind) {
        this.kind = kind;
        return this;
    }

    public TypeMirrorMock setElementType(TypeMirror elemtype) {
        this.elemtype = elemtype;
        return this;
    }

    public TypeMirrorMock setName(String name) {
        this.name = name;
        return this;
    }

    // ========== TypeMirror ==========

    @Override
    public TypeKind getKind() {
        return kind;
    }

    @Override
    public <R, P> R accept(TypeVisitor<R, P> v, P p) {
        return null;
    }

    // ========== Object ==========

    @Override
    public String toString() {
        if (name != null)
            return name;
        if (tsym != null)
            return tsym.toString();
        return "<unknown>";
    }

    // ========== static ==========

    public static TypeKind getTypeKind(Class<?> cls) {
        if (cls == Boolean.TYPE)
            return TypeKind.BOOLEAN;
        if (cls == Character.TYPE)
            return TypeKind.CHAR;
        if (cls == Byte.TYPE)
            return TypeKind.BYTE;
        if (cls == Short.TYPE)
            return TypeKind.SHORT;
        if (cls == Integer.TYPE)
            return TypeKind.INT;
        if (cls == Long.TYPE)
            return TypeKind.LONG;
        if (cls == Float.TYPE)
            return TypeKind.FLOAT;
        if (cls == Double.TYPE)
            return TypeKind.DOUBLE;
        if (cls == Void.TYPE)
            return TypeKind.VOID;
        return TypeKind.DECLARED;
    }

    private static TypeKind getTypeKind(Element element) {
        if (element == null)
            return null;

        switch (element.getKind()) {
            case CLASS:
            case INTERFACE:
                return TypeKind.DECLARED;
            default:
                return TypeKind.DECLARED;
        }
    }
}
