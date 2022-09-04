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
package cop.raml.utils.javadoc;

import cop.raml.utils.ProblemResolver;
import cop.raml.utils.ThreadLocalContext;
import cop.raml.utils.javadoc.tags.TagLink;
import cop.raml.utils.javadoc.tags.TagParam;
import cop.raml.utils.javadoc.tags.TagReturn;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Oleg Cherednik
 * @since 18.06.2016
 */
public final class MethodJavaDoc {
    public static final MethodJavaDoc NULL = new MethodJavaDoc(null);

    private final String text;
    private final Map<String, TagParam> params;
    private final TagReturn ret;

    @NotNull
    public static MethodJavaDoc create(List<String> doc) {
        return CollectionUtils.isNotEmpty(doc) ? new MethodJavaDoc(doc) : NULL;
    }

    private MethodJavaDoc(List<String> doc) {
        text = JavaDocUtils.getText(doc);
        params = getParams(doc);
        ret = getReturn(doc);
    }

    public String getText() {
        return text;
    }

    public static final Pattern TAG = Pattern.compile("^@\\w+(\\s+.+)?$");

    public TagParam getParam(String name) {
        return params.containsKey(name) ? params.get(name) : TagParam.NULL;
    }

    public Map<String, TagParam> getParams() {
        return params;
    }

    @NotNull
    private static Map<String, TagParam> getParams(List<String> doc) {
        if (CollectionUtils.isEmpty(doc))
            return Collections.emptyMap();

        Map<String, TagParam> params = new LinkedMap<>();
        String paramName = null;
        StringBuilder buf = null;
        Matcher matcher;

        for (String line : doc) {
            line = line.trim();

            if ((matcher = JavaDocTag.PARAM.getPattern().matcher(line)).find()) {
                if (paramName != null)
                    addParameter(paramName, buf, params);

                paramName = matcher.group("name");
                buf = new StringBuilder(StringUtils.defaultString(matcher.group("text"), ""));
            } else if (paramName != null) {
                if (TAG.matcher(line).find()) {
                    addParameter(paramName, buf, params);
                    paramName = null;
                } else if (buf.length() > 0)
                    buf.append('\n').append(line);
                else
                    buf.append(line);
            }
        }

        if (paramName != null)
            addParameter(paramName, buf, params);

        return params.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(params);
    }

    public TagReturn getReturn() {
        return ret;
    }

    @NotNull
    private static TagReturn getReturn(List<String> doc) {
        if (CollectionUtils.isEmpty(doc))
            return TagReturn.NULL;

        Matcher matcher;
        StringBuilder buf = null;

        for (String line : doc) {
            line = line.trim();

            if ((matcher = JavaDocTag.RETURN.getPattern().matcher(line)).find())
                buf = new StringBuilder(StringUtils.defaultString(matcher.group("text"), ""));
            else if (buf != null) {
                if (TAG.matcher(line).find())
                    return createTagReturn(buf.toString());

                buf.append(buf.length() > 0 ? '\n' : "").append(line);
            }
        }

        return createTagReturn(buf != null ? buf.toString() : null);
    }

    private static void addParameter(@NotNull String paramName, StringBuilder buf, Map<String, TagParam> params) {
        ThreadLocalContext.setParamName(paramName);

        if (params.containsKey(paramName))
            ProblemResolver.methodParamDuplication();
        else
            params.put(paramName, createTagParam(buf.toString(), TagLink.NULL));
    }

    @NotNull
    public static TagParam createTagParam(String str, @NotNull TagLink link) {
        if (StringUtils.isBlank(str))
            return TagParam.NULL;

        TagParam.Builder builder = TagParam.builder();

        builder.text(JavaDocUtils.clearMacros(str));
        builder.name(Macro.NAME.get(str));
        builder.pattern(Macro.PATTERN.get(str));
        builder.anEnum(Macro.ENUM.get(str));
        builder.def(Macro.DEFAULT.get(str));
        builder.example(Macro.EXAMPLE.get(str));
        builder.required(!Macro.OPTIONAL.exists(str));

        TagLink localLink = TagLink.create(str);

        if (localLink != TagLink.NULL && localLink.equals(link))
            ProblemResolver.paramLinksToItself(link);

        builder.link(localLink);

        return builder.build();
    }

    @NotNull
    private static TagReturn createTagReturn(String str) {
        if (StringUtils.isBlank(str))
            return TagReturn.NULL;

        TagReturn.Builder builder = TagReturn.createBuilder();

        builder.status(Macro.STATUS.get(str));
        builder.text(JavaDocUtils.clearMacros(str));
        builder.link(TagLink.create(str));
        builder.array(JavaDocUtils.isArray(str));

        return builder.build();
    }
}
