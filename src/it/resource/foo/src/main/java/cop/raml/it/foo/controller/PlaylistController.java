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
package cop.raml.it.foo.controller;

import cop.raml.it.foo.dto.PlaylistDTO;
import cop.raml.it.foo.exception.FooException;
import cop.raml.it.foo.utils.PathUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST end point for playlist operations
 */
@RestController
public class PlaylistController {
    /**
     * Retrieve all available playlists in project
     *
     * @param projectId project id
     * @return list of {@link PlaylistDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.PLAYLIST_LIST, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlaylistDTO> getPlaylists(@PathVariable long projectId) throws FooException {
        return null;
    }

    /**
     * Retrieve all available playlists with name that contains {@code value}.
     *
     * @param projectId project id
     * @param value     playlist name part
     * @return list of {@link PlaylistDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.PLAYLIST_SEARCH, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlaylistDTO> searchPlaylists(@PathVariable long projectId, @PathVariable String value) throws FooException {
        return null;
    }

    /**
     * Retrieve project with given {@code projectId}
     *
     * @param projectId  project id
     * @param playlistId playlist id
     * @return {@link PlaylistDTO} object
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.PLAYLIST_ONE, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PlaylistDTO getPlaylist(@PathVariable long projectId, @PathVariable long playlistId) throws FooException {
        return null;
    }

    /**
     * Create playlist using given {@code project}.
     *
     * @param projectId project id
     * @param playlist  project data
     * @return created {@link PlaylistDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.PLAYLIST_CREATE, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PlaylistDTO createPlaylist(@PathVariable long projectId, @RequestBody PlaylistDTO playlist) throws FooException {
        return null;
    }

    /**
     * Update playlist with given {@code playlistId} with new given {@code playlist}.
     *
     * @param projectId  project id
     * @param playlistId playlist id
     * @param playlist   new playlist
     * @return created {@link PlaylistDTO}
     * @throws FooException in case of any error
     */
    @RequestMapping(value = PathUtils.PLAYLIST_UPDATE, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public PlaylistDTO updatePlaylist(@PathVariable long projectId, @PathVariable long playlistId, @RequestBody PlaylistDTO playlist)
            throws FooException {
        return null;
    }

    /**
     * Delete playlist with given {@code playlistId}.
     *
     * @param projectId  project id
     * @param playlistId playlist id
     */
    @RequestMapping(value = PathUtils.PLAYLIST_DELETE, method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deletePlaylist(@PathVariable long projectId, @PathVariable long playlistId) {
    }
}
