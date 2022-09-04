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

import cop.raml.utils.Utils;
import cop.raml.utils.javadoc.tags.TagLink;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Oleg Cherednik
 * @since 15.03.2016
 */
public final class JavaDocUtils {
    public static String getDocComment(Element element, ProcessingEnvironment processingEnv) {
        String str = element != null ? processingEnv.getElementUtils().getDocComment(element) : null;
        return StringUtils.isNotBlank(str) ? str.trim() : null;
    }

    public static String getText(List<String> lines) {
        if (CollectionUtils.isEmpty(lines))
            return null;

        StringBuilder buf = new StringBuilder();
        int emptyLines = 0;

        for (String line : lines) {
            if (StringUtils.isBlank(line)) {
                if (buf.length() > 0)
                    emptyLines++;
            } else if (JavaDocTag.startsWith(line))
                break;
            else
                buf.append(StringUtils.repeat("\n", emptyLines + (buf.length() > 0 ? 1 : 0))).append(line);
        }

        return clearMacros(buf.toString());
    }

    private static final Pattern MULTIPLE_WHITESPACES = Pattern.compile(" {2,}");

    public static String clearMacros(String str) {
        if (StringUtils.isBlank(str))
            return null;

        str = Macro.removeAll(str);
        str = TagLink.remove(str);
        // TODO link should not be deleted; should be modified
        str = HtmlTag.replaceAll(str);
        str = MULTIPLE_WHITESPACES.matcher(str).replaceAll(StringUtils.SPACE);
        str = Utils.trimLine(str);
        str = str.trim();

        return str.isEmpty() ? null : str;
    }

    public static boolean isArray(String doc) {
        return "arr".equals(Macro.TYPE.get(doc));
    }

    private JavaDocUtils() {
    }
}
