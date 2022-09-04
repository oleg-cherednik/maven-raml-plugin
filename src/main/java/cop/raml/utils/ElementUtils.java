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
package cop.raml.utils;

import cop.raml.processor.exceptions.RamlProcessingException;
import cop.raml.utils.javadoc.Macro;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Oleg Cherednik
 * @since 20.05.2016
 */
public final class ElementUtils {
    /**
     * Retrieve type as {@link TypeMirror} for given {@code element}. If {@code element} is not an array or collection, then this method just invokes
     * {@link Element#asType()}. Otherwise it returns array type or collection's element type.
     * It is impossible for collection to find out element type of it in the runtime, therefore {@link Macro#TYPE} should be set, otherwise {@link
     * RamlProcessingException} will be thrown.
     *
     * @param element element
     * @return not {@code null} element, array or collection type
     * @throws Exception in case of any error
     */
    @NotNull
    public static TypeMirror getType(Element element) throws Exception {
        String doc = ThreadLocalContext.getDocComment(element);

        if (isCollection(element)) {
            if (Macro.TYPE.exists(doc))
                return ThreadLocalContext.getImportScanner().getElement(Macro.TYPE.get(doc)).asType();

            throw new RamlProcessingException("Collection element type cannot be automatically defined. Do use {@type} macro");
        }

        return isArray(element) ? getArrayType(element.asType()) : element.asType();
    }

    public static <T extends Element> T asElement(@NotNull TypeMirror type) throws Exception {
        Field field = type.getClass().getField("tsym");
        field.setAccessible(true);
        return (T)field.get(type);
    }

    private static TypeMirror getArrayType(TypeMirror type) throws Exception {
        Field field = type.getClass().getDeclaredField("elemtype");
        field.setAccessible(true);
        return (TypeMirror)field.get(type);
    }

    public static boolean isStatic(Element element) {
        try {
            return element != null && (boolean)element.getClass().getMethod("isStatic").invoke(element);
        } catch(IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
            return false;
        }
    }

    public static boolean isEnum(TypeMirror type) {
        try {
            return type != null && asElement(type).getKind() == ElementKind.ENUM;
        } catch(Exception ignored) {
            return false;
        }
    }

    public static boolean isArray(Element element) {
        return element != null && element.asType().getKind() == TypeKind.ARRAY;
    }

    public static boolean isCollection(Element element) {
        return element != null && "<any>".equals(element.asType().toString());
    }

    private ElementUtils() {
    }
}
