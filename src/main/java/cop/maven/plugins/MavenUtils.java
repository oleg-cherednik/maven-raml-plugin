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
package cop.maven.plugins;

import org.codehaus.plexus.util.xml.Xpp3Dom;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Oleg Cherednik
 * @since 31.12.16
 */
final class MavenUtils {
    @NotNull
    public static Map<String, Object> readMap(Xpp3Dom node) {
        if (!isMap(node))
            return Collections.emptyMap();

        Object obj;
        Map<String, Object> map = new LinkedHashMap<>();

        for (int i = 0; i < node.getChildCount(); i++) {
            Xpp3Dom child = node.getChild(i);

            if (isPrimitive(child))
                map.put(child.getName(), readPrimitive(child.getValue()));
            else if ((obj = readArray(child)) != null || !((Map<String, Object>)(obj = readMap(child))).isEmpty())
                map.put(child.getName(), obj);
            else
                throw new RuntimeException("Illegal maven configuration in: " + child);
        }

        return map;
    }

    private static boolean isArray(Xpp3Dom node) {
        if (node == null || node.getValue() != null || node.getChildCount() == 0)
            return false;

        int total = getDistinctChildrenAmount(node);

        if (total == 1) {
            String name = node.getChild(0).getName();
            return "param".equals(name) || node.getName().equals(name + 's');
        }

        if (total == node.getChildCount())
            return false;

        throw new RuntimeException("Illegal maven configuration in: " + node);
    }

    private static boolean isMap(Xpp3Dom node) {
        if (node == null || node.getValue() != null || node.getChildCount() == 0)
            return false;

        int total = getDistinctChildrenAmount(node);

        if (total == 1) {
            String name = node.getChild(0).getName();
            return !"param".equals(name) && !node.getName().equals(name + 's');
        }

        if (total == node.getChildCount())
            return true;

        throw new RuntimeException("Illegal maven configuration in: " + node);
    }

    private static int getDistinctChildrenAmount(@NotNull Xpp3Dom node) {
        return (int)Arrays.stream(node.getChildren()).map(Xpp3Dom::getName).distinct().count();
    }

    private static List<String> readArray(Xpp3Dom node) {
        if (!isArray(node))
            return null;

        List<String> res = new ArrayList<>();

        for (Xpp3Dom child : node.getChildren()) {
            String value = child.getValue();

            if (child.getChildCount() == 0 && value != null)
                res.add(child.getValue());
            else
                throw new RuntimeException("Illegal maven configuration in: " + child);
        }

        return res;
    }

    private static boolean isPrimitive(Xpp3Dom node) {
        return node != null && node.getChildCount() == 0 && node.getValue() != null;
    }

    private static Object readPrimitive(@NotNull String str) {
        return "true".equals(str) || "false".equals(str) ? Boolean.parseBoolean(str) : str;
    }

    private MavenUtils() {
    }
}
