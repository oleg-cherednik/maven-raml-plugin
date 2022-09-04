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

import cop.raml.utils.Utils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author Oleg Cherednik
 * @since 03.04.2016
 */
public class RestApi {
    private final Map<String, Resource> resources = new TreeMap<>(Resource.SORT_BY_PATH);
    private String title;
    private String baseUri;
    private String version;
    private String mediaType;
    private String docTitle;
    private String docContent;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDocTitle() {
        return docTitle;
    }

    public void setDocumentation(String title, String content) {
        if (StringUtils.isNotBlank(title) && StringUtils.isNotBlank(content)) {
            docTitle = title;
            docContent = content;
        }
    }

    public String getDocContent() {
        return docContent;
    }

    public String getDocContent(int offs) {
        return Utils.offs(docContent, offs, false);
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Collection<Resource> getResources() {
        return resources.values();
    }

    public Resource createResource(String path) {
        if (StringUtils.isBlank(path))
            return null;
        if (StringUtils.isNotBlank(baseUri) && path.startsWith(baseUri))
            path = path.substring(baseUri.length());

        return add(resources, path);
    }

    //    @SuppressWarnings("TailRecursion")
    @NotNull
    private static Resource add(@NotNull Map<String, Resource> resources, @NotNull String path) {
        path = path.startsWith("/") ? path : '/' + path;

        for (Resource resource : resources.values()) {
            String basePath = resource.getPath();

            if (basePath.equals(path))
                return resource;
            if (path.startsWith(basePath + '/'))
                return add(resource.children, StringUtils.removeStart(path, basePath));

            String commonPath = findLongestPrefix(basePath, path);

            if (commonPath.isEmpty())
                continue;

            Resource parent = new Resource(commonPath, resource);
            resources.remove(basePath);
            resources.put(parent.getPath(), parent);
            resource.removePathPrefix(commonPath);
            resource.removeUriParameters(parent.getUriParameters().stream().map(Parameter::getName).collect(Collectors.toList()));
            parent.children.put(resource.getPath(), resource);

            String suffix = StringUtils.removeStart(path, commonPath);

            if (StringUtils.isBlank(suffix))
                return parent;
            return add(parent.children, StringUtils.removeStart(path, commonPath));
        }

        Resource resource = new Resource(path);
        resources.put(resource.getPath(), resource);

        return resource;
    }

    private static String findLongestPrefix(String basePath, String path) {
        basePath = StringUtils.trimToEmpty(basePath);
        path = StringUtils.trimToEmpty(path);

        if (basePath.isEmpty() || path.isEmpty())
            return StringUtils.EMPTY;

        String[] baseParts = Utils.splitPath(basePath);
        String[] pathParts = Utils.splitPath(path);
        int total = getEqualPartsAmount(baseParts, pathParts);

        if (total > 0) {
            String str = StringUtils.join(ArrayUtils.subarray(baseParts, 0, total), "/");
            return path.startsWith("/") ? '/' + str : str;
        } else
            return StringUtils.EMPTY;
    }

    @SuppressWarnings("MethodCanBeVariableArityMethod")
    private static int getEqualPartsAmount(@NotNull String[] baseParts, @NotNull String[] pathParts) {
        int total = 0;

        for (int min = Math.min(baseParts.length, pathParts.length); total < min; total++)
            if (!baseParts[total].equals(pathParts[total]))
                break;

        return total;
    }
}
