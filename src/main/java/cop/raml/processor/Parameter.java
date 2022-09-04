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
package cop.raml.processor;

import cop.raml.processor.exceptions.RamlProcessingException;
import cop.raml.utils.ProblemResolver;
import cop.raml.utils.Utils;
import edu.emory.mathcs.backport.java.util.Collections;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.type.TypeMirror;
import java.io.File;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Oleg Cherednik
 * @since 06.04.2016
 */
public class Parameter implements ExamplePart, DescriptionPart, DisplayNamePart, RequiredPart {
    private final String name;
    // docs
    private String displayName;
    private String example;
    private String description;
    // parameters
    /**
     * all types: [required,default]
     * string: +[enum,min,max]
     * number/integer: +[min,max]
     */
    private Type type = Type.STRING;
    private boolean required = true;
    private String def;
    private final Set<String> anEnum = new LinkedHashSet<>();
    private String min;
    private String max;
    // others
    private String pattern;

    public Parameter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type.id;
    }

    public void setType(TypeMirror typeMirror) {
        Type type = Type.parseTypeMirror(typeMirror);

        if (type != Type.STRING && !anEnum.isEmpty())
            ProblemResolver.paramEnumForNotStringType(type.id);

        this.type = type;
    }

    public String getDef() {
        return def;
    }

    public void setDefault(String def) {
        this.def = def;
    }

    public String getEnum() {
        return Utils.toEnumStr(anEnum);
    }

    /**
     * Set enum field for current parameter. Other fields will be automatically validated. In case of any error, {@link RamlProcessingException} will
     * be thrown
     *
     * @param anEnum {@link Utils#SEMICOLON} separated enum constants string
     * @throws RamlProcessingException in case of any error (and {@link Config#ramlShowExample} is set to {@code true})
     */
    public void setEnum(String anEnum) {
        String[] arr = StringUtils.isNotBlank(anEnum) ? Utils.COLON.split(anEnum) : ArrayUtils.EMPTY_STRING_ARRAY;
        Set<String> items = StringUtils.isNotBlank(anEnum) ? Utils.asSet(arr) : Collections.emptySet();
        String validEnum = Utils.toEnumStr(items);

        if (items.isEmpty())
            this.anEnum.clear();
        else {
            if (def != null && !items.contains(def))
                ProblemResolver.paramDefaultIsNotInEnum(def, validEnum);
            if (pattern != null && items.stream().filter(item -> !item.matches(pattern)).count() > 0)
                ProblemResolver.paramEnumConstantIsNotMatchPattern(validEnum, pattern);
            if (arr.length > items.size())
                ProblemResolver.paramEnumConstantDuplication(String.format("[%s]", anEnum));
            if (type != Type.STRING)
                ProblemResolver.paramEnumForNotStringType(type.id);
        }

        this.anEnum.clear();

        if (validEnum != null)
            this.anEnum.addAll(items);
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
//        if (StringUtils.isNoneBlank(pattern)) {
//            if (pattern != null && type != Type.STRING)
//                type = null;
        // TODO throw warning if type != string && pattern != null
//            this.pattern = pattern;
//        }
    }

    // ========== RequiredPart ==========

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public void setRequired(boolean required) {
        this.required = required;
    }

    // ========== DisplayNamePart ==========

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getDisplayName(int offs) {
        return Utils.offs(displayName, offs, false);
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    // ========== DescriptionPart ==========

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getDescription(int offs) {
        return Utils.offs(description, offs, false);
    }

    @Override
    public void setDescription(String description) {
        this.description = StringUtils.isNotBlank(description) ? description : null;
    }

    // ========== ExamplePart ==========

    @Override
    public boolean isSingleLine() {
        return example == null || !example.contains("\n");
    }

    @Override
    public String getExample() {
        return example;
    }

    @Override
    public String getExample(int offs) {
        return Utils.offs(example, offs, false);
    }

    @Override
    public void setExample(String example) {
        this.example = Config.get().ramlShowExample() ? example : null;
    }

    // ========== Object ==========

    @Override
    public String toString() {
        return StringUtils.isBlank(description) ? name : String.format("%s [%s]", name, description);
    }

    // ========== enum ==========

    enum Type {
        STRING("string") {
            @Override
            protected boolean check(String str) {
                return String.class.getName().equals(str);
            }
        },
        INTEGER("integer") {
            @Override
            protected boolean check(String str) {
                return Integer.class.getName().equals(str) || int.class.getName().equals(str);
            }
        },
        NUMBER("number") {
            @Override
            protected boolean check(String str) {
                if (Long.class.getName().equals(str) || long.class.getName().equals(str))
                    return true;
                if (Double.class.getName().equals(str) || double.class.getName().equals(str))
                    return true;
                if (Float.class.getName().equals(str) || float.class.getName().equals(str))
                    return true;
                if (Short.class.getName().equals(str) || short.class.getName().equals(str))
                    return true;
                return Byte.class.getName().equals(str) || byte.class.getName().equals(str);
            }
        },
        FILE("file") {
            @Override
            protected boolean check(String str) {
                return File.class.getName().equals(str);
            }
        },
        DATE("date") {
            @Override
            protected boolean check(String str) {
                return Date.class.getName().equals(str);
            }
        },
        BOOLEAN("boolean") {
            @Override
            protected boolean check(String str) {
                return Boolean.class.getName().equals(str) || boolean.class.getName().equals(str);
            }
        };

        private final String id;

        Type(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        protected abstract boolean check(String str);

        // ========== static ==========

        public static Type parseTypeMirror(TypeMirror typeMirror) {
            if (typeMirror != null) {
                String str = typeMirror.toString();

                for (Type type : values())
                    if (type.check(str))
                        return type;
            }

            return STRING;
        }
    }
}
