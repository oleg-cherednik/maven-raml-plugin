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
package cop.raml.utils.javadoc.tags;

import cop.raml.utils.javadoc.Macro;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Oleg Cherednik
 * @since 14.05.2016
 */
public final class TagLink {
    public static final TagLink NULL = new TagLink(null, null);
    public static final String REGEX = "\\{ *@link +(?<cls>[^#\\.][\\w\\.]*[^\\.])?(?:#(?<par>\\w+))? *\\}";

    private final String className;
    private final String param;

    public static TagLink create(String doc) {
        return create(getLinkClassName(doc), getLinkParamName(doc));
    }

    public static TagLink create(String className, String param) {
        className = StringUtils.isNotBlank(className) ? className : null;
        param = StringUtils.isNotBlank(param) ? param : null;
        return className != null || param != null ? new TagLink(className, param) : NULL;
    }

    private TagLink(String className, String param) {
        this.className = className;
        this.param = param;
    }

    public String getClassName() {
        return className;
    }

    public String getParam() {
        return param;
    }

    public boolean isClass() {
        return className != null && param == null;
    }

    // ========== Object ==========

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof TagLink)
            return Objects.equals(className, ((TagLink)obj).className) && Objects.equals(param, ((TagLink)obj).param);
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, param);
    }

    @Override
    public String toString() {
        if (this == NULL)
            return "<null>";
        if (className != null)
            return param != null ? String.format("{@link %s#%s}", className, param) : String.format("{@link %s}", className);
        return String.format("{@link #%s}", param);
    }

    // ========== static ==========

    private static final Pattern PATTERN = Pattern.compile(REGEX + " *\n?");

    private static String getLinkClassName(String doc) {
        if (StringUtils.isBlank(doc))
            return null;

        Matcher matcher = PATTERN.matcher(doc);
        return matcher.find() ? matcher.group("cls") : null;
    }

    private static String getLinkParamName(String doc) {
        if (StringUtils.isBlank(doc))
            return null;

        Matcher matcher = PATTERN.matcher(doc);
        return matcher.find() ? matcher.group("par") : null;
    }

    public static String remove(String str) {
        return Macro.removeWithPattern(PATTERN, str);
    }
}
