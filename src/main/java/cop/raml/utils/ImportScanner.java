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

import cop.raml.processor.Config;
import cop.raml.utils.javadoc.tags.TagLink;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementScanner8;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class is used to scan all imports in given class and retrieve valid {@link Element} by it's name.
 *
 * @author Oleg Cherednik
 * @since 06.05.2016
 */
public class ImportScanner extends ElementScanner8<Void, DirectoryStream.Filter<String>> {
    private final Set<String> types = new HashSet<>();
    private final Map<String, TypeElement> rootElements = new LinkedHashMap<>();
    // key1: class name; key2: import class name
    private final Map<String, Map<String, TypeElement>> imports = new HashMap<>();
    private String className;

    /**
     * Scan all root elements from the context {@link RoundEnvironment#getRootElements()} which is taken from {@link
     * ThreadLocalContext#getRoundEnv()}. All elements are stored from the previous method invoked will be cleared. All operation of import scanner
     * will be proceeded only with this elements set.
     * This method should be invoked at the beginning of the whole process once.
     */
    public void scanRootElements() {
        rootElements.clear();

        for (Element element : ThreadLocalContext.getRoundEnv().getRootElements())
            if (element instanceof TypeElement && !Config.ramlSkipClassName(element.toString()))
                rootElements.put(element.toString(), (TypeElement)element);
    }

    /**
     * Find class root element for given {@code element} and retrieve all imports are used in the java file. All imported elements will be stored in
     * the internal map.
     *
     * @param element some element
     */
    public void setCurrentElement(@NotNull Element element) {
        element = getClassRootElement(element);
        TypeKind kind = element.asType().getKind();

        if (kind != TypeKind.DECLARED && kind != TypeKind.ERROR)
            return;

        className = element.toString();

        if (imports.containsKey(className))
            return;

        for (String className : SorcererJavacUtils.getImports(element)) {
            if (className.endsWith(".*"))
                rootElements.entrySet().stream()
                            .filter(entry -> entry.getKey().startsWith(className.substring(0, className.length() - 1)))
                            .forEach(entry -> addImport(this.className, entry.getKey(), entry.getValue()));
            else if (rootElements.containsKey(className))
                addImport(this.className, className, rootElements.get(className));
            else {
                TypeElement typeElement = ThreadLocalContext.getElement(className);

                if (typeElement == null)
                    continue;
                if (Config.ramlSkipClassName(typeElement.toString()))
                    continue;

                // TODO values current project package and include include only related elements

                addImport(this.className, className, typeElement);
                rootElements.putIfAbsent(typeElement.toString(), typeElement);
            }
        }
    }

    private void addImport(String className, String importClassName, TypeElement importClassType) {
        Map<String, TypeElement> map = imports.get(className);

        if (map == null)
            imports.put(className, map = new HashMap<>());

        map.put(importClassName, importClassType);
    }

    public TypeElement getElement(TypeMirror type) {
        return getElement(type != null ? type.toString() : null);
    }

    private static final Pattern NAME_COLLECTION = Pattern.compile(".*<(?<className>.+)>");

    public TypeElement getElement(String className) {
        if (StringUtils.isBlank(className))
            return null;
        if (imports.containsKey(this.className) && imports.get(this.className).containsKey(className))
            return imports.get(this.className).get(className);
        if (rootElements.containsKey(className))
            return rootElements.get(className);

        TypeElement element;

        if ((element = getOneRootElementWithName(className)) != null)
            return element;
        if ((element = ThreadLocalContext.getElement(className)) != null) {
            rootElements.putIfAbsent(element.toString(), element);
            return element;
        }

        className = className.startsWith("java.lang.") ? className : String.format("java.lang.%s", className);

        if ((element = ThreadLocalContext.getElement(className)) != null) {
            rootElements.putIfAbsent(element.toString(), element);
            return element;
        }

        Matcher matcher = NAME_COLLECTION.matcher(className);

        if (!matcher.matches())
            return null;

        className = matcher.group("className");
        return "any".equals(className) ? null : getElement(matcher.group("className"));
    }

    public Element getElement(@NotNull TagLink link) {
        Element classElement = link != TagLink.NULL ? getElement(link.getClassName()) : null;

        if (classElement == null || link.getParam() == null)
            return classElement;

        for (Element paramElement : classElement.getEnclosedElements())
            if (link.getParam().equals(paramElement.getSimpleName().toString()))
                return paramElement;

        return null;
    }

    /**
     * This method returns {@link TypeElement} object for given {@code className}, where {@code className} can be simple or full name. First, it goes
     * to the current root element (main class in the current file), scan it's import declarations to values full name for given {@code className} and
     * it's {@link TypeElement}.
     * I don't expect, that we could find multiple import candidates, because in this case we definitely have java compilation error. But just in
     * cases, this situation I treat as error.
     *
     * @param className class element
     * @return type element object if at least one element with given {@code className} was found
     */
    private TypeElement getOneRootElementWithName(@NotNull String className) {
        List<String> elements = rootElements.keySet().stream()
                                            .filter(name -> name.endsWith(className))
                                            .collect(Collectors.toList());

        if (elements.isEmpty())
            return null;

        String element = elements.iterator().next();

        if (elements.size() > 1)
            ProblemResolver.ambiguousImportClassDefinition(className, this.className, elements, element);

        return rootElements.get(element);
    }

    public void clear() {
        types.clear();
        imports.clear();
        rootElements.clear();
    }

    private void addAcceptedType(String str, DirectoryStream.Filter<String> filter) {
        if (filter(str, filter))
            types.add(str);
    }

    // ========== ElementVisitor ==========

    @Override
    public Void visitExecutable(ExecutableElement element, DirectoryStream.Filter<String> filter) {
        if (element.getReturnType().getKind() == TypeKind.DECLARED)
            addAcceptedType(element.getReturnType().toString(), filter);
        return super.visitExecutable(element, filter);
    }

    @Override
    public Void visitTypeParameter(TypeParameterElement element, DirectoryStream.Filter<String> filter) {
        if (element.asType().getKind() == TypeKind.DECLARED)
            addAcceptedType(element.asType().toString(), filter);
        return super.visitTypeParameter(element, filter);
    }

    @Override
    public Void visitVariable(VariableElement element, DirectoryStream.Filter<String> filter) {
        if (element.asType().getKind() == TypeKind.DECLARED)
            addAcceptedType(element.asType().toString(), filter);
        return super.visitVariable(element, filter);
    }

    // ========== static ==========

    private static boolean filter(String str, DirectoryStream.Filter<String> filter) {
        try {
            return filter == null || filter.accept(str);
        } catch(IOException ignored) {
            // TODO put warning message to output
            return false;
        }
    }

    /**
     * Find out main class element in the java file. Given {@code element} can point to method or inner class. In each case, this method retrieves
     * only one class element (which has same name with java file).
     *
     * @param element some element
     * @return class element
     */
    private static Element getClassRootElement(Element element) {
        if (element == null)
            return null;

        Element prv = element;

        while (element != null && element.getKind() != ElementKind.PACKAGE) {
            prv = element;
            element = element.getEnclosingElement();

            if (element == null)
                prv = null;
        }

        return prv;
    }
}
