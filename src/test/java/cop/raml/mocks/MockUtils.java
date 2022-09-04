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

import com.fasterxml.jackson.annotation.JsonIgnore;
import cop.raml.TestUtils;
import cop.raml.processor.Config;
import cop.raml.utils.ThreadLocalContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.validation.constraints.NotNull;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Oleg Cherednik
 * @since 04.01.2017
 */
public final class MockUtils {
    // TODO do remove it
    public static TypeElementMock createProject() {
        VariableElementMock id = new VariableElementMock("id", int.class);
        id.setDocComment(TestUtils.joinStrings(
                "Unique project id",
                "{@name Unique project id}",
                "{@example 666}"));
        VariableElementMock name = new VariableElementMock("name", String.class);
        name.setDocComment(TestUtils.joinStrings(
                "Name of the project",
                "{@example The Project}"));

        TypeElementMock typeElement = new TypeElementMock("cop.raml.dto.Project", ElementKind.CLASS);
        typeElement.addEnclosedElement(id);
        typeElement.addEnclosedElement(name);

        return typeElement;
    }

    public static VariableElementMock createVariable(String name, Class<?> cls) throws ClassNotFoundException {
        return createVariable(name, cls, false);
    }

    public static VariableElementMock createStaticVariable(String name, Class<?> cls) throws ClassNotFoundException {
        return createVariable(name, cls, true);
    }

    public static VariableElementMock createVariable(String name, Class<?> cls, boolean isStatic) throws ClassNotFoundException {
        return cls != null && StringUtils.isNotBlank(name) ? new VariableElementMock(name, createElement(cls).asType()).setStatic(isStatic) : null;
    }

    public static ExecutableElementMock createExecutable(Method method) {
        if (method == null)
            return null;

        boolean isStatic = Modifier.isStatic(method.getModifiers());
        String params = Arrays.stream(method.getParameterTypes())
                              .map(Class::getSimpleName)
                              .collect(Collectors.joining(","));
        String name = String.format("(%s)%s", params, method.getReturnType().getSimpleName());

        return new ExecutableElementMock(method.getName() + "()", createMethodElement(name).asType())
                .setStatic(isStatic).setSimpleName(method.getName());
    }

    public static ExecutableElementMock createExecutable(String name) {
        return new ExecutableElementMock(name + "()", createMethodElement(name).asType()).setSimpleName(name);
    }

    public static TypeElementMock createElement(@NotNull Class<?> cls) throws ClassNotFoundException {
        if (cls.isPrimitive())
            return createPrimitiveElement(cls);
        if (cls.isEnum())
            return createEnumElement(cls);
        if (cls.isArray())
            return setAnnotations(createArrayElement(cls.getComponentType()), cls);
        if (Collection.class.isAssignableFrom(cls))
            return setAnnotations(createCollectionElement(), cls);
        return createClassElement(cls);
    }

    private static TypeElementMock createPrimitiveElement(@NotNull Class<?> cls) {
        TypeElementMock element = new TypeElementMock(cls.getName(), ElementKind.CLASS);
        element.setType(new TypeMirrorMock(element, TypeMirrorMock.getTypeKind(cls)));
        return setAnnotations(element, cls);
    }

    private static TypeElementMock createClassElement(@NotNull Class<?> cls) throws ClassNotFoundException {
        TypeElementMock element = new TypeElementMock(cls.getName(), ElementKind.CLASS);
        element.setType(new TypeMirrorMock(element, TypeMirrorMock.getTypeKind(cls)));

        if (cls.getName().startsWith("cop.") || cls.getName().startsWith("spring.")) {
            VariableElementMock var;

            for (Field field : cls.getDeclaredFields())
                if ((var = createVariable(field.getName(), field.getType(), Modifier.isStatic(field.getModifiers()))) != null)
                    element.addEnclosedElement(setAnnotations(var, field));

            ExecutableElementMock exe;

            for (Method method : cls.getDeclaredMethods())
                if ((exe = createExecutable(method)) != null)
                    element.addEnclosedElement(setAnnotations(exe, method));
        }

        return setAnnotations(element, cls);
    }

    private static <T extends AnnotatedConstructMock> T setAnnotations(AnnotatedConstructMock mock, AnnotatedElement element) {
        if (mock != null && element != null)
            Arrays.stream(element.getAnnotations()).forEach(mock::addAnnotation);
        return (T)mock;
    }

    private static TypeElementMock createEnumElement(@NotNull Class<?> cls) {
        TypeElementMock element = new TypeElementMock(cls.getName(), ElementKind.ENUM);
        element.setType(new TypeMirrorMock(element, TypeKind.DECLARED));
        element.addEnclosedElement(new TypeElementMock("values()", ElementKind.METHOD));

        for (Object obj : cls.getEnumConstants())
            element.addEnclosedElement(new VariableElementMock(obj.toString(), ElementKind.ENUM_CONSTANT));

        return setAnnotations(element, cls);
    }

    private static TypeElementMock createArrayElement(@NotNull Class<?> cls) throws ClassNotFoundException {
        TypeElementMock element = new TypeElementMock("Array", ElementKind.CLASS);
        TypeMirrorMock type = new TypeMirrorMock(element, TypeKind.ARRAY);

        if (cls.isPrimitive())
            type.setElementType(createPrimitiveElement(cls).asType());
        else if (cls.isEnum())
            type.setElementType(createEnumElement(cls).asType());
        else
            type.setElementType(createClassElement(cls).asType());

        element.setType(type);

        return element;
    }

    private static TypeElementMock createCollectionElement() {
        TypeElementMock element = new TypeElementMock("<any>", ElementKind.CLASS);
        element.setType(new TypeMirrorMock(element, TypeKind.ERROR));
        return element;
    }

    public static TypeElementMock createMethodElement(String name) {
        TypeElementMock element = new TypeElementMock("Method", ElementKind.CLASS);
        element.setType(new TypeMirrorMock(element, TypeKind.EXECUTABLE).setName(name));
        return element;
    }

    public static void setImportScannerElement(TypeElement typeElement) {
        ((RoundEnvironmentMock)ThreadLocalContext.getRoundEnv()).addRootElement(typeElement);
        ThreadLocalContext.getImportScanner().scanRootElements();
        ((ElementsMock)ThreadLocalContext.getElementUtils()).addTypeElement(typeElement);
    }

    public static void initThreadLocalContext() {
        ThreadLocalContext.setImportScanner(new ImportScannerMock());
        ThreadLocalContext.setProcessingEnv(new ProcessingEnvironmentMock());
        ThreadLocalContext.setRoundEnv(new RoundEnvironmentMock());
        ThreadLocalContext.setConfig(Config.NULL);
    }

    // ========== data ==========

    @SuppressWarnings("unused")
    public enum Count {
        ONE,
        TWO,
        THREE
    }

    @SuppressWarnings({ "unused", "MethodMayBeStatic" })
    public static final class Scenario {
        private int id;
        private String name;
        private int[] ids;
        private Object obj;
        private Job job;

        @JsonIgnore
        private int counter;

        private static String nextId;

        public List<String> getAnalysesByProjectId(@PathVariable long projectId) {
            return null;
        }
    }

    @SuppressWarnings({ "unused", "MethodMayBeStatic" })
    public static final class AnalysisController {
        private long id;

        public List<String> getAnalysesByProjectId(@PathVariable long projectId) {
            return null;
        }

        @RequestMapping(value = "/status/{status}", method = GET, produces = APPLICATION_JSON_VALUE)
        public List<String> getAnalysesByProjectIdAndStatus(@PathVariable long projectId, @PathVariable String status) {
            return null;
        }

        @RequestMapping(value = "/tag", method = GET, produces = APPLICATION_JSON_VALUE)
        public List<String> getAnalysesByTagName(@PathVariable long projectId, @RequestParam String tagName) {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public static class Job {
        private int id;
        private String name;
    }

    private MockUtils() {
    }
}
