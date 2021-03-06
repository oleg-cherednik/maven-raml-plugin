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
package cop.raml.utils.example;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import cop.raml.processor.Config;
import cop.raml.utils.ElementUtils;
import cop.raml.utils.ThreadLocalContext;
import cop.raml.utils.javadoc.Macro;
import cop.raml.utils.javadoc.tags.TagLink;
import cop.utils.json.JsonUtils;
import org.apache.commons.lang3.ArrayUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * @author Oleg Cherednik
 * @since 01.01.17
 */
final class JsonExample implements Example {
    private final Map<String, Object> cache = new HashMap<>();
    private final Random random = new Random();

    /**
     * Retrieve example for given {@code element}. First of all it checks existed {@link #cache} and if example for given {@code element} was already
     * in cache, then return it. Then it tries to generate example for simple types (e.g. primitives, Date, UUID). If examples is still not found,
     * then use {@link Config#ramlSkip} filter to filter out some element types and invoke {@link #getTypeExample(TypeElement, Set)}. Put result to
     * cache if it is not {@code null}.
     *
     * @param element element for example
     * @param visited visited element types (it's used to prevent cycle references)
     * @return example object ({@link Map} or {@link Set}) or {@code null} if example is not found
     */
    private Object getElementExample(@NotNull Element element, @NotNull Set<String> visited) {
        try {
            ThreadLocalContext.getImportScanner().setCurrentElement(element);
            TypeMirror type = ElementUtils.getType(element);
            String className = type.toString();

            Object res;

            if (cache.containsKey(className))
                res = cache.get(className);
            else if (ElementUtils.isEnum(type))
                res = getEnumExample(type);
            else if ((res = getAutoGeneratedElementExample(element, random)) == null) {
                if (String.class.getName().equals(className))
                    res = element.getSimpleName().toString();
                else if (!Config.get().ramlSkip(type.toString())) {
                    TypeElement obj = element instanceof TypeElement ? (TypeElement)element : ElementUtils.asElement(type);

                    if ((res = getTypeExample(obj, visited)) != null)
                        cache.putIfAbsent(className, res);
                }
            }

            if (res == null)
                return null;
            if (ElementUtils.isArray(element) || ElementUtils.isCollection(element))
                return Collections.singleton(res);
            return res;
        } catch(Exception ignored) {
            return null;
        }
    }

    /**
     * Retrieves example for given {@code obj} which is not simple element, e.g. class with variables and method.
     * To build example of this object, read recursively all it's variables (each of them could be not simple element as well), build example and put
     * it into a map. Only not {@code null} example a placed into the map.
     * Additionally we consider {@link JsonIgnoreProperties} and {@link JsonIgnore} annotations.
     *
     * @param obj     not {@code null} complex element
     * @param visited not {@code null} visited object (use it to avoid cycle references)
     * @return {@code null} or not empty map of element name tp element example structure
     */
    private Map<String, Object> getTypeExample(@NotNull TypeElement obj, @NotNull Set<String> visited) {
        String name = obj.toString();

        if (visited.contains(name))
            return null;

        visited.add(name);

        try {
            Set<String> ignored = getIgnoredFields(obj);
            Map<String, Object> map = new LinkedHashMap<>();
            String elementName;
            Object res;

            for (Element element : obj.getEnclosedElements()) {
                if (element.getKind() != ElementKind.FIELD)
                    continue;
                if (ElementUtils.isStatic(element))
                    continue;
                if (ignored.contains(elementName = element.getSimpleName().toString()))
                    continue;
                if ((res = getElementExample(element, visited)) != null)
                    map.put(elementName, res);
            }

            return map;
        } finally {
            visited.remove(name);
        }
    }

    // ========== Example ==========

    @Override
    public String example(Element element, boolean arr) {
        if (element == null)
            return null;

        Object res = getElementExample(element, new HashSet<>());

        if (arr && !(res instanceof Collection))
            res = Collections.singleton(res);

        return toJson(res);
    }

    // ========== static ==========

    /**
     * Retrieves example for given {@code element} only if given {@code element} is primitive or declared, when example can be auto generated.
     * First, look at element's javadoc and read {@link Macro#EXAMPLE}. Use it if it exists.
     * Second, look at element's javadoc and read {@link TagLink} macro, if it exists, then read linked element's javadoc and use {@link
     * Macro#EXAMPLE} if it exists.
     * Third, generate random values using {@link #getPrimitiveExample(TypeKind, Random)} and {@link #getDeclaredExample(String, String)}. Otherwise
     * returns {@code null}.
     *
     * @param element element object
     * @param random  not {@code null} random generator
     * @return variable example (could be any types or {@code null})
     */
    private static Object getAutoGeneratedElementExample(@NotNull Element element, @NotNull Random random) throws Exception {
        String doc;

        if (Macro.EXAMPLE.exists(doc = ThreadLocalContext.getDocComment(element)))
            return Macro.EXAMPLE.get(doc);
        if (Macro.EXAMPLE.exists(doc = ThreadLocalContext.getDocComment(TagLink.create(doc))))
            return Macro.EXAMPLE.get(doc);

        TypeMirror type = ElementUtils.getType(element);
        Object res = getPrimitiveExample(type.getKind(), random);
        String varName = element instanceof VariableElement ? element.toString() : null;

        return res == null ? getDeclaredExample(type.toString(), varName) : res;
    }

    /**
     * Generates random example for given primitive {@code kind} using given {@code random} generator.
     *
     * @param kind   primitive kind type
     * @param random not {@code null} random generator
     * @return generated random example for given primitive type
     */
    private static Object getPrimitiveExample(TypeKind kind, @NotNull Random random) {
        if (kind == TypeKind.BOOLEAN)
            return random.nextBoolean();
        if (kind == TypeKind.BYTE)
            return (byte)random.nextInt(255);
        if (kind == TypeKind.SHORT)
            return (short)random.nextInt(1000);
        if (kind == TypeKind.INT)
            return random.nextInt(1000);
        if (kind == TypeKind.LONG)
            return (long)random.nextInt(1000);
        if (kind == TypeKind.CHAR)
            return (char)('A' + random.nextInt(28));
        if (kind == TypeKind.FLOAT)
            return (float)random.nextInt(1000);
        if (kind == TypeKind.DOUBLE)
            return (double)random.nextInt(1000);
        return null;
    }

    /**
     * Generate random example for {@link TypeKind#DECLARED} type base on given {@code varName} and {@code className}.
     *
     * @param varName   variable name
     * @param className full class name (e.g. java.lang.String)
     * @return generated random example for given {@code className}
     */
    private static Object getDeclaredExample(String className, String varName) {
        if (UUID.class.getName().equals(className))
            return UUID.randomUUID();
        if (varName != null && String.class.getName().equals(className) && "uuid".equals(varName.toLowerCase()))
            return String.valueOf(UUID.randomUUID());
        if (Date.class.getName().equals(className))
            return System.currentTimeMillis();
        return null;
    }

    /**
     * Returns all ignored fields for given {@code typeElement}. This time support only {@link JsonIgnoreProperties} and {@link JsonIgnore}
     * annotations. But in the following article <a href="http://www.baeldung.com/jackson-ignore-properties-on-serialization">Jackson Ignore
     * Properties on Marshalling</a>, more ways to ignore properties can be found, but there're not supported this time.
     *
     * @param typeElement type element
     * @return not {@code null} list of ignored fields
     */
    @NotNull
    private static Set<String> getIgnoredFields(TypeElement typeElement) {
        if (typeElement == null)
            return Collections.emptySet();

        Set<String> res = new HashSet<>();
        JsonIgnoreProperties annotation = typeElement.getAnnotation(JsonIgnoreProperties.class);

        if (annotation != null && ArrayUtils.isNotEmpty(annotation.value()))
            Collections.addAll(res, annotation.value());

        JsonIgnore ann;

        for (Element element : typeElement.getEnclosedElements())
            if ((ann = element.getAnnotation(JsonIgnore.class)) != null && ann.value())
                res.add(element.toString());

        return res;
    }

    /**
     * Convert given {@code example} to json. If given {@code example} is not a string, then {@link JsonUtils#writePrettyValue(Object)} will be
     * invoked, or string representation of the {@code example} otherwise.
     * In case of any provlems, {@code null} will be returned without any notification.
     *
     * @param example example object (string or not string)
     * @return string json representation of the example in pretty print format
     */

    private static String toJson(Object example) {
        try {
            if (example == null)
                return null;
            if (example instanceof String)
                return example.toString();
            return JsonUtils.writePrettyValue(example);
        } catch(JsonProcessingException ignored) {
            // TODO write out warning message
            return null;
        }
    }

    /**
     * Returns string representation of the given {@code element} when this is enum (i.e. {@link TypeElement#getKind()} == {@link ElementKind#ENUM}).
     * If enum contains at least one constant, then method returns {@link Object#toString()} for first enum constant. If enum doesn't contain any
     * constant, then method return name for enum itself.
     *
     * @param type not {@code null} enum element type
     * @return not {@code null} enum element string representation
     */
    @NotNull
    private static String getEnumExample(@NotNull TypeMirror type) {
        if (!ElementUtils.isEnum(type))
            return null;

        try {
            Element element = ElementUtils.asElement(type);

            for (Element parent : element.getEnclosedElements())
                if (parent.getKind() == ElementKind.ENUM_CONSTANT)
                    return parent.getSimpleName().toString();

            return element.getSimpleName().toString();
        } catch(Exception ignored) {
            return null;
        }
    }
}
