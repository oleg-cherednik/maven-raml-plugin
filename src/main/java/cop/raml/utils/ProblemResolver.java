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
import cop.raml.processor.exceptions.RamlProcessingException;
import cop.raml.utils.javadoc.Macro;
import cop.raml.utils.javadoc.tags.TagLink;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.List;

import static javax.tools.Diagnostic.Kind.WARNING;

/**
 * @author Oleg Cherednik
 * @since 17.12.2016
 */
public final class ProblemResolver {
    /**
     * Method param duplication
     *
     * @throws RamlProcessingException exception
     * @see ThreadLocalContext#getClassName()
     * @see ThreadLocalContext#getMethodName()
     * @see ThreadLocalContext#getMessager()
     * @see ThreadLocalContext#getParamName()
     */
    public static void methodParamDuplication() {
        String paramName = ThreadLocalContext.getParamName();
        String className = ThreadLocalContext.getClassName();
        String methodName = ThreadLocalContext.getMethodName();

        if (StringUtils.isBlank(paramName))
            return;

        String message = String.format("%s.%s() - parameter '%s' duplication", className, methodName, paramName);

        if (Config.get().ramlStopOnError())
            throw new RamlProcessingException(message);

        ThreadLocalContext.getMessager().printMessage(WARNING, message);
    }

    /**
     * Param link to itself
     *
     * @param link tag link
     * @throws RamlProcessingException exception
     * @see ThreadLocalContext#getMessager()
     */
    public static void paramLinksToItself(@NotNull TagLink link) {
        if (link == null || link == TagLink.NULL)
            return;

        // TODO fix message
        String message = link.toString();

        if (Config.get().ramlStopOnError())
            throw new RamlProcessingException(message);

        ThreadLocalContext.getMessager().printMessage(WARNING, message);
    }

    /**
     * Macro duplication
     *
     * @param macro macro
     * @throws RamlProcessingException exception
     * @see ThreadLocalContext#getClassName()
     * @see ThreadLocalContext#getMethodName()
     * @see ThreadLocalContext#getMessager()
     * @see ThreadLocalContext#getParamName()
     */
    public static void macroDuplication(@NotNull Macro macro) {
        if (macro == null)
            return;

        String paramName = ThreadLocalContext.getParamName();
        String className = ThreadLocalContext.getClassName();
        String methodName = ThreadLocalContext.getMethodName();
        String message = String.format("%s.%s() - parameter '%s', macro '%s' duplication", className, methodName, paramName, macro.getId());

        if (Config.get().ramlStopOnError())
            throw new RamlProcessingException(message);

        ThreadLocalContext.getMessager().printMessage(WARNING, message);
    }

    public static void ambiguousImportClassDefinition(String className, String importClassName, List<String> candidates, String candidate) {
        if (StringUtils.isBlank(className) || StringUtils.isBlank(importClassName))
            return;

        if (Config.get().ramlStopOnError())
            throw new RamlProcessingException(String.format(
                    "Cannot choose correct import for class name '%s' in class '%s'. Existed candidates: [%s]", importClassName, className,
                    String.join(",", candidates)));

        ThreadLocalContext.getMessager().printMessage(WARNING,
                String.format("Multiple candidates for class name '%s' in class '%s' found. Will use: '%s'", importClassName, className, candidate));
    }

    /**
     * @param def
     * @param anEnum
     * @throws RamlProcessingException exception
     * @see ThreadLocalContext#getClassName()
     * @see ThreadLocalContext#getMethodName()
     * @see ThreadLocalContext#getMessager()
     * @see ThreadLocalContext#getParamName()
     */
    public static void paramDefaultIsNotInEnum(String def, String anEnum) {
        if (def == null || anEnum == null)
            return;

        String paramName = ThreadLocalContext.getParamName();
        String className = ThreadLocalContext.getClassName();
        String methodName = ThreadLocalContext.getMethodName();
        String message = String.format("%s.%s() - parameter '%s', default value '%s' is not in enum set %s",
                className, methodName, paramName, def, anEnum);

        if (Config.get().ramlStopOnError())
            throw new RamlProcessingException(message);

        ThreadLocalContext.getMessager().printMessage(WARNING, message);
    }

    /**
     * @param anEnum
     * @param pattern
     * @throws RamlProcessingException exception
     * @see ThreadLocalContext#getClassName()
     * @see ThreadLocalContext#getMethodName()
     * @see ThreadLocalContext#getMessager()
     * @see ThreadLocalContext#getParamName()
     */
    public static void paramEnumConstantIsNotMatchPattern(String anEnum, String pattern) {
        if (StringUtils.isBlank(pattern) || StringUtils.isBlank(anEnum))
            return;

        String paramName = ThreadLocalContext.getParamName();
        String className = ThreadLocalContext.getClassName();
        String methodName = ThreadLocalContext.getMethodName();
        String message = String.format("%s.%s() - parameter '%s', at least one constant in enum %s is not match pattern '%s'",
                className, methodName, paramName, anEnum, pattern);

        if (Config.get().ramlStopOnError())
            throw new RamlProcessingException(message);

        ThreadLocalContext.getMessager().printMessage(WARNING, message);
    }

    /**
     * @param anEnum
     * @see ThreadLocalContext#getClassName()
     * @see ThreadLocalContext#getMethodName()
     * @see ThreadLocalContext#getMessager()
     * @see ThreadLocalContext#getParamName()
     */
    public static void paramEnumConstantDuplication(String anEnum) {
        if (StringUtils.isBlank(anEnum))
            return;

        String paramName = ThreadLocalContext.getParamName();
        String className = ThreadLocalContext.getClassName();
        String methodName = ThreadLocalContext.getMethodName();
        String message = String.format("%s.%s() - parameter '%s', enum %s contains constant duplication", className, methodName, paramName, anEnum);

        if (Config.get().ramlStopOnError())
            throw new RamlProcessingException(message);

        ThreadLocalContext.getMessager().printMessage(WARNING, message);
    }

    /**
     * @param type
     * @see ThreadLocalContext#getClassName()
     * @see ThreadLocalContext#getMethodName()
     * @see ThreadLocalContext#getMessager()
     * @see ThreadLocalContext#getParamName()
     */
    public static void paramEnumForNotStringType(String type) {
        if (StringUtils.isBlank(type))
            return;

        String paramName = ThreadLocalContext.getParamName();
        String className = ThreadLocalContext.getClassName();
        String methodName = ThreadLocalContext.getMethodName();
        String message = String.format("%s.%s() - parameter '%s', enum is not allowed for '%s' type", className, methodName, paramName, type);

        if (Config.get().ramlStopOnError())
            throw new RamlProcessingException(message);

        ThreadLocalContext.getMessager().printMessage(WARNING, message);
    }

    public static void mediaTypeNotDefined(String... mediaTypes) {
        if (ArrayUtils.isNotEmpty(mediaTypes))
            return;

        String className = ThreadLocalContext.getClassName();
        String methodName = ThreadLocalContext.getMethodName();
        String message = String.format("%s.%s() - cannot define media type", className, methodName);

        if (Config.get().ramlStopOnError())
            throw new RamlProcessingException(message);

        ThreadLocalContext.getMessager().printMessage(WARNING, message);
    }

    private ProblemResolver() {
    }
}
