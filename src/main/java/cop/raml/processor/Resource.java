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

import cop.raml.utils.AutoCreateMap;
import cop.raml.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Describes rest resource, e.g @RequestMapping(value = "/services/user"), then key for each resource is "/services/user"
 *
 * @author Oleg Cherednik
 * @since 03.04.2016
 */
public class Resource implements DescriptionPart, DisplayNamePart {
    private String path;
    private String displayName;
    private String description;
    private boolean done;
    private final AutoCreateMap<String, Parameter> uriParameters = new AutoCreateMap<>();
    private final AutoCreateMap<String, RestMethod> methods = new AutoCreateMap<>(new TreeMap<>(RestMethod.SORT_BY_TYPE));

    final Map<String, Resource> children = new TreeMap<>(SORT_BY_PATH);

    public Resource(String path) {
        this.path = path;
    }

    public Resource(String path, Resource resource) {
        this.path = path;

        // TODO move this out of constructor
        resource.uriParameters.keySet().stream()
                              .filter(name -> path.contains(String.format("{%s}", name)))
                              .forEach(name -> uriParameters.put(name, resource.uriParameters.get(name)));
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isDone() {
        return done;
    }

    public String getPath() {
        return path;
    }

    public void removePathPrefix(String prefix) {
        if (StringUtils.isNotBlank(prefix)) {
            path = StringUtils.removeStart(path, prefix);

//            for (String key : children.keySet()) {
//                Resource resource = children.replace(key);
//                resource.removePathPrefix(prefix);
//                children.put(resource.path, resource);
//            }
        }
    }

    public Collection<RestMethod> getMethods() {
        return methods.values();
    }

    public Collection<Parameter> getUriParameters() {
        return uriParameters.values();
    }

    public Parameter addUriParameter(String name) {
        return uriParameters.put(name, Parameter::new);
    }

    public void removeUriParameters(List<String> names) {
        names.forEach(uriParameters::remove);
    }

    public Parameter getUriParameter(String name) {
        return uriParameters.get(name);
    }

    public Collection<Resource> getChildren() {
        return children.values();
    }

    public RestMethod createMethod(String type) {
        return methods.put(type, RestMethod::new);
    }

    // ========== DisplayNamePart =========

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
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getDescription(int offs) {
        return Utils.offs(description, offs, false);
    }

    // ========== Object ==========

    @Override
    public String toString() {
        return children.isEmpty() ? path : String.format("%s -> [%d]", path, children.size());
    }

    // ========== static ==========

    public static final Comparator<String> SORT_BY_PATH = (path1, path2) -> Comparator.comparing(String::toString).compare(path1, path2);
}
