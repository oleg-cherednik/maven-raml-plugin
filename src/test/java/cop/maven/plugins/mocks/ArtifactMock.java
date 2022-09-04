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
package cop.maven.plugins.mocks;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.OverConstrainedVersionException;
import org.apache.maven.artifact.versioning.VersionRange;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * @author Oleg Cherednik
 * @since 17.02.2017
 */
public class ArtifactMock implements Artifact {
    private String scope;
    private File destination;

    public ArtifactMock() {
    }

    public ArtifactMock(String scope, File destination) {
        setScope(scope);
        setFile(destination);
    }

    // ========== Artifact ==========

    @Override
    public String getGroupId() {
        return null;
    }

    @Override
    public String getArtifactId() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public void setVersion(String version) {
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String getClassifier() {
        return null;
    }

    @Override
    public boolean hasClassifier() {
        return false;
    }

    @Override
    public File getFile() {
        return destination;
    }

    @Override
    public void setFile(File destination) {
        this.destination = destination;
    }

    @Override
    public String getBaseVersion() {
        return null;
    }

    @Override
    public void setBaseVersion(String baseVersion) {
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getDependencyConflictId() {
        return null;
    }

    @Override
    public void addMetadata(ArtifactMetadata metadata) {
    }

    @Override
    public Collection<ArtifactMetadata> getMetadataList() {
        return null;
    }

    @Override
    public void setRepository(ArtifactRepository remoteRepository) {
    }

    @Override
    public ArtifactRepository getRepository() {
        return null;
    }

    @Override
    public void updateVersion(String version, ArtifactRepository localRepository) {

    }

    @Override
    public String getDownloadUrl() {
        return null;
    }

    @Override
    public void setDownloadUrl(String downloadUrl) {
    }

    @Override
    public ArtifactFilter getDependencyFilter() {
        return null;
    }

    @Override
    public void setDependencyFilter(ArtifactFilter artifactFilter) {
    }

    @Override
    public ArtifactHandler getArtifactHandler() {
        return null;
    }

    @Override
    public List<String> getDependencyTrail() {
        return null;
    }

    @Override
    public void setDependencyTrail(List<String> dependencyTrail) {
    }

    @Override
    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public VersionRange getVersionRange() {
        return null;
    }

    @Override
    public void setVersionRange(VersionRange newRange) {
    }

    @Override
    public void selectVersion(String version) {
    }

    @Override
    public void setGroupId(String groupId) {
    }

    @Override
    public void setArtifactId(String artifactId) {
    }

    @Override
    public boolean isSnapshot() {
        return false;
    }

    @Override
    public void setResolved(boolean resolved) {

    }

    @Override
    public boolean isResolved() {
        return false;
    }

    @Override
    public void setResolvedVersion(String version) {
    }

    @Override
    public void setArtifactHandler(ArtifactHandler handler) {

    }

    @Override
    public boolean isRelease() {
        return false;
    }

    @Override
    public void setRelease(boolean release) {
    }

    @Override
    public List<ArtifactVersion> getAvailableVersions() {
        return null;
    }

    @Override
    public void setAvailableVersions(List<ArtifactVersion> versions) {
    }

    @Override
    public boolean isOptional() {
        return false;
    }

    @Override
    public void setOptional(boolean optional) {
    }

    @Override
    public ArtifactVersion getSelectedVersion() throws OverConstrainedVersionException {
        return null;
    }

    @Override
    public boolean isSelectedVersionKnown() throws OverConstrainedVersionException {
        return false;
    }

    // ========== Comparable ==========

    @Override
    public int compareTo(Artifact o) {
        return 0;
    }
}
