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

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * @author Oleg Cherednik
 * @since 23.12.2016
 */
public enum JavaDocTag {
    API_NOTE("@apiNote"),
    AUTHOR("@author"),
    DEPRECATED("@deprecated"),
    EXCEPTION("@exception"),
    IMPL_NOTE("@implNote"),
    IMPL_SPEC("@implSpec"),
    PARAM("@param", "^@param(\\s+(?<name>\\w+)(?:\\s+(?<text>.+))?)?"),
    RETURN("@return", "^@return(\\s+(?<text>.+))?"),
    SEE("@see"),
    SERIAL("@serial"),
    SERIAL_DATA("@serialData"),
    SINCE("@since"),
    THROWS("@throws"),
    VERSION("@version");

    private final String id;
    private final Pattern pattern;

    JavaDocTag(String id) {
        this(id, null);
    }

    JavaDocTag(String id, String regex) {
        this.id = id;
        pattern = regex != null ? Pattern.compile(regex) : null;
    }

    public Pattern getPattern() {
        return pattern;
    }

    // ========== static ==========

    public static boolean startsWith(String str) {
        if (StringUtils.isNotBlank(str))
            for (JavaDocTag tag : values())
                if (str.startsWith(tag.id))
                    return true;

        return false;
    }
}
