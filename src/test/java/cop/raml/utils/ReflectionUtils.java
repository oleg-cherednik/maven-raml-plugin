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

import cop.raml.RamlVersion;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * I've copied this class from <a href="http://www.jroller.com/VelkaVrana/entry/modify_enum_with_reflection">Matej Tymes's Weblog</a>. After that I've
 * modified it.
 *
 * @author mtymes
 * @author Oleg Cherednik
 * @since: 25.01.2017
 */
public final class ReflectionUtils {
    public static <T> T getFieldValue(Object obj, String name) throws NoSuchFieldException, IllegalAccessException {
        Field field = getField(obj.getClass(), name);
        field.setAccessible(true);
        return (T)field.get(obj);
    }

    public static <T> T getStaticFieldValue(Class<?> cls, String name) throws NoSuchFieldException, IllegalAccessException {
        Field field = getField(cls, name);
        field.setAccessible(true);
        return (T)field.get(cls);
    }

    public static <T> T invokeMethod(Object obj, String name) throws Exception {
        assertThat(obj).isNotInstanceOf(Class.class);
        return invokeMethod(obj, name, null);
    }

    public static <T> T invokeMethod(Object obj, String name, Class<?> type, Object value) throws Exception {
        assertThat(obj).isNotInstanceOf(Class.class);
        return invokeMethod(obj, name, new Class<?>[] { type }, value);
    }

    public static <T> T invokeMethod(Object obj, String name, Class<?> type1, Class<?> type2, Object value1, Object value2) throws Exception {
        assertThat(obj).isNotInstanceOf(Class.class);
        return invokeMethod(obj, name, new Class<?>[] { type1, type2 }, value1, value2);
    }

    public static <T> T invokeMethod(Object obj, String name, Class<?> type1, Class<?> type2, Class<?> type3,
            Object value1, Object value2, Object value3) throws Exception {
        assertThat(obj).isNotInstanceOf(Class.class);
        return invokeMethod(obj, name, new Class<?>[] { type1, type2, type3 }, value1, value2, value3);
    }

    public static <T> T invokeMethod(Object obj, String name, Class<?>[] types, Object... values) throws Exception {
        try {
            Method method = getMethod(obj.getClass(), name, types);
            method.setAccessible(true);
            return (T)method.invoke(obj, values);
        } catch(InvocationTargetException e) {
            throw (Exception)e.getTargetException();
        }
    }

    public static <T> T invokeStaticMethod(Class<?> cls, String name) throws Exception {
        return invokeStaticMethod(cls, name, null);
    }

    public static <T> T invokeStaticMethod(Class<?> cls, String name, Class<?> type, Object value) throws Exception {
        return invokeStaticMethod(cls, name, new Class<?>[] { type }, value);
    }

    public static <T> T invokeStaticMethod(Class<?> cls, String name, Class<?> type1, Class<?> type2, Object value1, Object value2) throws Exception {
        return invokeStaticMethod(cls, name, new Class<?>[] { type1, type2 }, value1, value2);
    }

    public static <T> T invokeStaticMethod(Class<?> cls, String name, Class<?> type1, Class<?> type2, Class<?> type3,
            Object value1, Object value2, Object value3) throws Exception {
        return invokeStaticMethod(cls, name, new Class<?>[] { type1, type2, type3 }, value1, value2, value3);
    }

    public static <T> T invokeStaticMethod(Class<?> cls, String name, Class<?>[] types, Object... values) throws Exception {
        try {
            Method method = getMethod(cls, name, types);
            method.setAccessible(true);
            return (T)method.invoke(null, values);
        } catch(InvocationTargetException e) {
            throw (Exception)e.getTargetException();
        }
    }

    public static void setFieldValue(Object obj, String name, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = getField(obj.getClass(), name);
        boolean accessible = field.isAccessible();

        field.setAccessible(true);
        try {
            field.set(obj, value);
        } finally {
            field.setAccessible(accessible);
        }
    }

    public static void setStaticFieldValue(Class<?> cls, String name, Object value) throws Exception {
        Field field = getField(cls, name);
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);

        if ((modifiersField.getInt(field) & Modifier.FINAL) > 0) {
            // sun.reflect.FieldAccessor
            Object fieldAccessor = invokeMethod(field, "getFieldAccessor", new Class<?>[] { Object.class }, cls);
            Object isReadOnly = getFieldValue(fieldAccessor, "isReadOnly");

            try {
                setFieldValue(fieldAccessor, "isReadOnly", false);
                invokeMethod(fieldAccessor, "set", new Class<?>[] { Object.class, Object.class }, cls, value);
            } finally {
                setFieldValue(fieldAccessor, "isReadOnly", isReadOnly);
            }
        } else
            field.set(null, value);
    }

    public static <T> T invokeConstructor(Class<T> cls) {
        try {
            Constructor<T> constructor = cls.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch(Exception ignored) {
            return null;
        }
    }

    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    public static <T> T invokeEnumConstructor(Class<T> cls, Class<?>[] types, Object... values) throws Exception {
        Constructor<?> constructor = cls.getDeclaredConstructor(types);
        constructor.setAccessible(true);

        // sun.reflect.ConstructorAccessor
        Object constructorAccessor = getFieldValue(constructor, "constructorAccessor");

        if (constructorAccessor == null) {
            invokeMethod(constructor, "acquireConstructorAccessor", EMPTY_CLASS_ARRAY, EMPTY_OBJECT_ARRAY);
            constructorAccessor = getFieldValue(constructor, "constructorAccessor");
        }

        return invokeMethod(constructorAccessor, "newInstance", new Class<?>[] { Object[].class }, new Object[] { values });
    }

    private static final String ENUM_VALUES = "$VALUES";

    @FunctionalInterface
    public interface ArrayCreate<T> {
        T[] create(int size);
    }

    public static <T extends Enum<?>> T addEnumConstant(Class<T> cls, String name, String id, String template, ArrayCreate<T> creator)
            throws Exception {
        T[] old = getStaticFieldValue(RamlVersion.class, ENUM_VALUES);
        T[] arr = creator.create(old.length + 1);
        System.arraycopy(old, 0, arr, 0, old.length);

        Class<?>[] types = { String.class, Integer.TYPE, String.class, String.class };
        Object[] values = { name, arr.length, id, template };
        arr[arr.length - 1] = invokeEnumConstructor(cls, types, values);

        setStaticFieldValue(RamlVersion.class, ENUM_VALUES, arr);

        return arr[arr.length - 1];
    }

    public static <T extends Enum<?>> void removeEnumConstant(T obj, ArrayCreate<T> creator) throws Exception {
        T[] old = getStaticFieldValue(obj.getClass(), ENUM_VALUES);
        T[] arr = creator.create(old.length - 1);
        int i = 0;

        for (T var : old)
            if (var != obj)
                arr[i++] = var;

        setStaticFieldValue(obj.getClass(), ENUM_VALUES, arr);
    }

    private static Field getField(Class<?> cls, String name) throws NoSuchFieldException {
        Field field = null;

        while (field == null && cls != null) {
            try {
                field = cls.getDeclaredField(name);
            } catch(NoSuchFieldException ignored) {
                cls = cls.getSuperclass();
            }
        }

        if (field == null)
            throw new NoSuchFieldException();

        return field;
    }

    private static Method getMethod(Class<?> cls, String name, Class<?>... types) throws NoSuchMethodException {
        Method method = null;

        while (method == null && cls != null) {
            try {
                method = cls.getDeclaredMethod(name, types);
            } catch(NoSuchMethodException ignored) {
                cls = cls.getSuperclass();
            }
        }

        if (method == null)
            throw new NoSuchMethodException();

        return method;
    }

    private ReflectionUtils() {
    }
}
