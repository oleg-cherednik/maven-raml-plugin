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

import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utils to values access to internal fields using org.jvnet.sorcerer.sorcerer-javac package.
 * We cannot put it directly to the project dependency, because it supports only Java 1.7.
 *
 * @author Oleg Cherednik
 * @since 19.05.2016
 */
public final class SorcererJavacUtils {
    private static final Pattern IMPORT = Pattern.compile("import\\s+(?<path>\\w+[\\w\\.\\$]+\\*?)\\s*;");

    /**
     * Retrieve all imported objects as list of string.
     * com.sun.tools.javac.model.JavacElements
     *
     * @param classElement class element
     * @return not {@code null} set of string
     */
    @NotNull
    public static Set<String> getImports(@NotNull Element classElement) {
        try {
            Object treeTop = getTreeAndTopLevel(classElement);

            if (treeTop == null)
                return Collections.emptySet();

            Matcher matcher;
            Set<String> imports = new LinkedHashSet<>();

            for (Object def : getDefinitions(getPairSecond(treeTop)))
                if ((matcher = IMPORT.matcher(def.toString())).find())
                    imports.add(matcher.group("path"));

            return imports;
        } catch(Exception ignored) {
            return Collections.emptySet();
        }
    }

    /**
     * @param classElement class element
     * @return instance of {@code com.sun.tools.javac.utils.Pair} with first element is {@code com.sun.tools.javac.tree.JCTree$JCClassDecl} and second
     * one is {@code com.sun.tools.javac.tree.JCTRee$JCCompilationUnit}
     * @throws Exception exception
     */
    private static Object getTreeAndTopLevel(Element classElement) throws Exception {
        Elements elements = ThreadLocalContext.getElementUtils();
        Method method = elements.getClass().getDeclaredMethod("getTreeAndTopLevel", Element.class);
        method.setAccessible(true);
        return method.invoke(elements, classElement);
    }

    /**
     * @param obj instance of {@code com.sun.tools.javac.util.Pair}
     * @return instance of {@code com.sun.tools.javac.treeJCTree$JCCompilationUnit}
     * @throws Exception exception
     */
    private static Object getPairSecond(Object obj) throws Exception {
        Field field = obj.getClass().getDeclaredField("snd");
        field.setAccessible(true);
        return field.get(obj);
    }

    /**
     * @param obj instance of {@code com.sun.tools.javac.treeJCTree$JCCompilationUnit}
     * @return list of {@code com.sun.tools.javac.tree.JCTree}
     * @throws Exception exception
     */
    private static List<Object> getDefinitions(Object obj) throws Exception {
        Field field = obj.getClass().getDeclaredField("defs");
        field.setAccessible(true);
        return (List<Object>)field.get(obj);
    }

    private SorcererJavacUtils() {
    }
}
