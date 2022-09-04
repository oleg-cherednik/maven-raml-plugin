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
import cop.raml.utils.javadoc.JavaDocUtils;
import cop.raml.utils.javadoc.tags.TagLink;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Oleg Cherednik
 * @since 18.06.2016
 */
public final class ThreadLocalContext {
    private static final ThreadLocal<ProcessingEnvironment> THREAD_LOCAL_PROCESSING_ENV = new ThreadLocal<>();
    private static final ThreadLocal<ImportScanner> THREAD_LOCAL_IMPORT_SCANNER = new ThreadLocal<>();
    private static final ThreadLocal<Config> THREAD_LOCAL_CONFIG = new ThreadLocal<>();
    private static final ThreadLocal<RoundEnvironment> THREAD_LOCAL_ROUND_ENVIRONMENT = new ThreadLocal<>();

    private static final ThreadLocal<String> THREAD_LOCAL_CLASS_NAME = new ThreadLocal<>();
    private static final ThreadLocal<String> THREAD_LOCAL_METHOD_NAME = new ThreadLocal<>();
    private static final ThreadLocal<String> THREAD_LOCAL_PARAM_NAME = new ThreadLocal<>();

    public static ProcessingEnvironment getProcessingEnv() {
        return THREAD_LOCAL_PROCESSING_ENV.get();
    }

    @NotNull
    public static void setProcessingEnv(ProcessingEnvironment processingEnv) {
        THREAD_LOCAL_PROCESSING_ENV.set(processingEnv);
    }

    @NotNull
    public static ImportScanner getImportScanner() {
        return THREAD_LOCAL_IMPORT_SCANNER.get();
    }

    public static void setImportScanner(ImportScanner importScanner) {
        THREAD_LOCAL_IMPORT_SCANNER.set(importScanner);
    }

    @NotNull
    public static Config getConfig() {
        Config config = THREAD_LOCAL_CONFIG.get();
        return config != null ? config : Config.NULL;
    }

    @NotNull
    public static void setConfig(@NotNull Config config) {
        THREAD_LOCAL_CONFIG.set(config);
    }

    @NotNull
    public static Messager getMessager() {
        return getProcessingEnv().getMessager();
    }

    @NotNull
    public static RoundEnvironment getRoundEnv() {
        return THREAD_LOCAL_ROUND_ENVIRONMENT.get();
    }

    @NotNull
    public static void setRoundEnv(RoundEnvironment roundEnv) {
        THREAD_LOCAL_ROUND_ENVIRONMENT.set(roundEnv);
    }

    @NotNull
    public static Elements getElementUtils() {
        return getProcessingEnv().getElementUtils();
    }

    @NotNull
    public static String getDocComment(Element element) {
        return JavaDocUtils.getDocComment(element, getProcessingEnv());
    }

    @NotNull
    public static String getDocComment(TagLink link) {
        return getDocComment(getImportScanner().getElement(link));
    }

    @NotNull
    public static List<String> getDocCommentAsList(Element element) {
        return Utils.toLineList(JavaDocUtils.getDocComment(element, getProcessingEnv()));
    }

    // TODO I think that getImportedElement is enough
    public static TypeElement getElement(String className) {
        return getElementUtils().getTypeElement(className);
    }

    public static TypeElement getImportedElement(String className) {
        return getImportScanner().getElement(className);
    }

    public static Element getImportedElement(TagLink link) {
        return getImportScanner().getElement(link);
    }

    @NotNull
    public static String getClassName() {
        return THREAD_LOCAL_CLASS_NAME.get();
    }

    @NotNull
    public static void setClassName(@NotNull String className) {
        THREAD_LOCAL_CLASS_NAME.set(className);
    }

    @NotNull
    public static String getMethodName() {
        return THREAD_LOCAL_METHOD_NAME.get();
    }

    @NotNull
    public static void setMethodName(@NotNull String methodName) {
        THREAD_LOCAL_METHOD_NAME.set(methodName);
    }

    @NotNull
    public static String getParamName() {
        return THREAD_LOCAL_PARAM_NAME.get();
    }

    @NotNull
    public static void setParamName(@NotNull String paramName) {
        THREAD_LOCAL_PARAM_NAME.set(paramName);
    }

    public static void remove() {
        if (getImportScanner() != null)
            getImportScanner().clear();

        THREAD_LOCAL_PROCESSING_ENV.remove();
        THREAD_LOCAL_IMPORT_SCANNER.remove();
        THREAD_LOCAL_CONFIG.remove();
        THREAD_LOCAL_ROUND_ENVIRONMENT.remove();

        THREAD_LOCAL_CLASS_NAME.remove();
        THREAD_LOCAL_METHOD_NAME.remove();
        THREAD_LOCAL_PARAM_NAME.remove();
    }

    private ThreadLocalContext() {
    }
}
