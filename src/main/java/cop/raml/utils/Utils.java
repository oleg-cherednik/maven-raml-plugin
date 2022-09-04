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

import cop.raml.utils.javadoc.HtmlTag;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

/**
 * @author Oleg Cherednik
 * @since 05.12.2016
 */
public final class Utils {
    public static final Pattern COLON = Pattern.compile("\\s*,\\s*");
    public static final Pattern SEMICOLON = Pattern.compile("\\s*;\\s*");
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_XML = "application/xml";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String TEXT_PLAIN = "text/plain";

    public static String[] arrApplicationJson() {
        return new String[] { APPLICATION_JSON };
    }

    public static String[] arrMultipartFormData() {
        return new String[] { MULTIPART_FORM_DATA };
    }

    public static String join(String lhs, String rhs) {
        lhs = StringUtils.isBlank(lhs) ? StringUtils.EMPTY : lhs.trim();
        rhs = StringUtils.isBlank(rhs) ? StringUtils.EMPTY : rhs.trim();

        lhs = removeTrailingSlashes(lhs);
        rhs = removeLeadingSlashes(rhs);
        rhs = removeTrailingSlashes(rhs);

        if (rhs.isEmpty())
            return lhs.isEmpty() ? null : lhs;
        return lhs.isEmpty() ? rhs : String.format("%s/%s", lhs, rhs);
    }

    private static final Pattern LEADING_SLASH = Pattern.compile("^/+");

    private static String removeLeadingSlashes(String str) {
        if (str == null)
            return null;
        if (StringUtils.isBlank(str))
            return StringUtils.EMPTY;
        return LEADING_SLASH.matcher(str).replaceAll("");
    }

    private static final Pattern TRAILING_SLASH = Pattern.compile("/+$");

    private static String removeTrailingSlashes(String str) {
        if (str == null)
            return null;
        if (StringUtils.isBlank(str))
            return StringUtils.EMPTY;
        return TRAILING_SLASH.matcher(str).replaceAll("");
    }

    @NotNull
    public static StringBuilder comma(@NotNull StringBuilder buf) {
        if (buf == null)
            buf = new StringBuilder();
        else if (buf.length() > 0)
            buf.append(", ");
        return buf;
    }

    private static final Pattern URI_PARAM = Pattern.compile("\\{(\\w+)\\}");

    @NotNull
    public static Set<String> getParams(String uri) {
        if (StringUtils.isBlank(uri))
            return Collections.emptySet();

        Set<String> names = new LinkedHashSet<>();
        Matcher matcher = URI_PARAM.matcher(uri);

        while (matcher.find())
            names.add(matcher.group(1));

        return names.isEmpty() ? Collections.emptySet() : names;
    }

    private static final Pattern EMPTY_ENUM = Pattern.compile("\\s*\\[\\s*\\]\\s*");

    public static String toEnumStr(String doc, VariableElement var) {
        doc = StringUtils.isBlank(doc) || EMPTY_ENUM.matcher(doc).matches() ? null : doc;

        if (doc != null || var == null)
            return doc;

        try {
            TypeElement typeElement = ElementUtils.asElement(var.asType());

            if (typeElement.getKind() != ElementKind.ENUM)
                return null;

            return toEnumStr(typeElement.getEnclosedElements().stream()
                                        .filter(element -> element.getKind() == ElementKind.ENUM_CONSTANT)
                                        .map(element -> element.getSimpleName().toString())
                                        .toArray(String[]::new));
        } catch(Exception ignored) {
            return null;
        }
    }

    /**
     * Converts enum constants as set {@code anEnum} to enum string without any duplication.
     *
     * @param anEnum set of enum constants
     * @return {@code null} or not empty enum string (e.g. [one,two])
     */
    public static String toEnumStr(Set<String> anEnum) {
        return CollectionUtils.isNotEmpty(anEnum) ? toEnumStr(anEnum.toArray(new String[anEnum.size()])) : null;
    }

    /**
     * Converts enum constants as array {@code arr} to enum string without any duplication.
     *
     * @param arr array of enum items
     * @return {@code null} or not empty enum string (e.g. [one,two])
     */
    public static String toEnumStr(String... arr) {
        Set<String> res = asSet(arr);
        return res.isEmpty() ? null : res.stream().collect(Collectors.joining(",", "[", "]"));
    }

    /**
     * Convert enum constants in given {@code arr} to no duplication constant collection.
     *
     * @param arr array of enum items
     * @return {@code null} or not empty enum item set
     */
    @NotNull
    public static Set<String> asSet(String... arr) {
        if (ArrayUtils.isEmpty(arr))
            return Collections.emptySet();

        return Arrays.stream(arr)
                     .filter(StringUtils::isNotBlank)
                     .map(String::trim)
                     .collect(toCollection(LinkedHashSet::new));
    }

    private static final Pattern TRIM_LINE = Pattern.compile(" *\n *");

    public static String trimLine(String str) {
        return str != null ? TRIM_LINE.matcher(str).replaceAll("\n").trim() : null;
    }

    // \n - nix, \r - mac, \r\n - win
    private static final Pattern NEW_LINE = Pattern.compile("(?<!\r)\n|\r(?!\n)|\r\n");

    public static String[] splitLine(String str) {
        if (str == null)
            return null;
        if (str.isEmpty())
            return ArrayUtils.EMPTY_STRING_ARRAY;
        return NEW_LINE.split(str);
    }

    public static String[] splitPath(String str) {
        if (str == null)
            return null;
        if (StringUtils.isBlank(str))
            return ArrayUtils.EMPTY_STRING_ARRAY;

        str = removeLeadingSlashes(str);
        str = removeTrailingSlashes(str);

        return str.split("/");
    }

    public static String offs(String str, int offs, boolean strict) {
        if (StringUtils.isBlank(str) || offs < 0)
            return str;

        String tmp = StringUtils.repeat(StringUtils.SPACE, offs);
        String[] lines = Arrays.stream(splitLine(str))
                               .map(line -> tmp + (strict ? line : line.trim()))
                               .map(line -> StringUtils.isBlank(line) ? StringUtils.EMPTY : line)
                               .toArray(String[]::new);

        return String.join("\n", lines);
    }

    /**
     * Convert given {@code comment} into separate line list. Each line is trimmed.
     *
     * @param comment javadoc comment
     * @return not {@code null} list of javadoc comment's lines
     */
    @NotNull
    public static List<String> toLineList(String comment) {
        if (StringUtils.isBlank(comment))
            return Collections.emptyList();

        String[] arr = splitLine(comment);
        List<String> lines = new ArrayList<>(arr.length);
        int emptyLines = 0;

        for (String line : arr) {
            line = line.trim();

            if (StringUtils.isBlank(line) || HtmlTag.P.is(line) || HtmlTag.BR.is(line))
                emptyLines++;
            else {
                line = HtmlTag.P.replace(line);
                line = HtmlTag.BR.replace(line);

                if (lines.isEmpty())
                    emptyLines = 0;
                else if (emptyLines > 0)
                    do {
                        lines.add(StringUtils.EMPTY);
                        emptyLines--;
                    } while (emptyLines > 0);

                lines.addAll(Arrays.asList(splitLine(line)));
            }
        }

        return lines.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(lines);
    }

    public static String defOnBlank(String primary, String secondary) {
        return StringUtils.defaultIfBlank(primary, StringUtils.defaultIfBlank(secondary, null));
    }

    private Utils() {
    }
}
