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
import cop.raml.utils.Utils;
import cop.raml.utils.javadoc.tags.TagLink;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Oleg Cherednik
 * @since 17.12.2016
 */
public enum Macro {
    DEFAULT("@default", " *\\{ *@default +((?:\\w+[\\w ]+\\w)+) *\\} *\n?"),
    ENUM("@enum", " *\\{ *@enum +(\\[[^}]+\\]) *\\} *") {
        @Override
        public String get(String str) {
            // TODO do check enum parameter duplication
            String res = super.get(str);
            return res != null ? Utils.SEMICOLON.matcher(res).replaceAll(",") : null;
        }
    },
    EXAMPLE("@example", " *\\{ *@example +(\\S[^{}]+\\S) *\\} *\n?"),
    NAME("@name", " *\\{ *@name +(\\w+[\\w ]+\\w)+ *\\} *\n?"),
    PATTERN("@pattern", " *\\{ *@pattern +((?:[^\\s}]+ *[^\\s}]+)+) *\\} *\n?"),
    OPTIONAL("@optional", " *\\{ *@optional *\\} *\n?"),
    STATUS("@status", " *\\{ *@status +((?:\\d+)+) *\\} *\n?"),
    TYPE("@type", " *\\{ *@type +(\\w+) *\\} *\n?"),
    URL("@url", " *\\{ *@url +([\\w\\/][\\w+&@#\\/%=~_|{}]+[\\w\\/]+|" + TagLink.REGEX + ") *\\} *");
    /*
    {@min 3}
    {@max 10}
    {@range [3;10]}
     */

    private final String id;
    private final Pattern pattern;

    Macro(String id, String regex) {
        this.id = id;
        pattern = Pattern.compile(regex);
    }

    public String getId() {
        return id;
    }

    public String get(String str) {
        if (StringUtils.isBlank(str))
            return null;

        Matcher matcher = pattern.matcher(str);

        if (!matcher.find())
            return null;

        String res = matcher.group(1);

        if (matcher.find())
            ProblemResolver.macroDuplication(this);

        return res;
    }

    public String remove(String str) {
        return removeWithPattern(pattern, str);
    }

    public boolean exists(String str) {
        return StringUtils.isNotBlank(str) && pattern.matcher(str).find();
    }

    // ========== static ==========

    public static String removeAll(String str) {
        for (Macro macro : values())
            str = macro.remove(str);

        return str.trim();
    }

    public static String removeWithPattern(@NotNull Pattern pattern, String str) {
        return StringUtils.isNotBlank(str) ? pattern.matcher(str).replaceAll(StringUtils.SPACE) : str;
    }
}
