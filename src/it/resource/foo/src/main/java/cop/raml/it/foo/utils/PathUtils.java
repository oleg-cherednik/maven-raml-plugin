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
package cop.raml.it.foo.utils;

import java.util.UUID;
import java.util.regex.Pattern;

public final class PathUtils {
//    public static final String SERVICES = "/services";
    public static final String SERVICES = "";
    public static final String MODEL_UUID = "{modelUuid}";
    public static final String MODEL_STEP_UUID = "{stepUuid}";
    public static final String CUSTOM_TABLE_ID = "{customTableId}";
    public static final String PROJECT_ID = "{projectId}";
    public static final String PLAYLIST_ID = "{playlistId}";
    public static final String MODEL_STEP_PARAMETER_ID = "{paramId}";
    public static final String TEMPLATE_ID = "{templateId}";
    public static final String UUID = "{UUID}";

    // Paths for Models CRUD
    public static final String MODEL = SERVICES + "/model";
    public static final String MODEL_LIST = MODEL;
    public static final String MODEL_ONE = MODEL + '/' + MODEL_UUID;
    public static final String MODEL_FIND = MODEL + "/find";
    public static final String MODEL_CREATE = MODEL;
    public static final String MODEL_DELETE = MODEL_ONE;
    public static final String MODEL_UPDATE = MODEL_ONE;

    // Paths for Model Metadata CRUD
    public static final String MODEL_METADATA = MODEL_ONE + "/metadata";
    public static final String MODEL_METADATA_CREATE = MODEL_METADATA;
    public static final String MODEL_METADATA_DELETE = MODEL_METADATA;
    public static final String MODEL_METADATA_UPDATE = MODEL_METADATA;

    // Paths for Model Steps CRUD
    public static final String MODEL_STEP = SERVICES + "/step";
    public static final String MODEL_STEP_LIST = MODEL_STEP;
    public static final String MODEL_STEP_ONE = MODEL_STEP + '/' + MODEL_STEP_UUID;
    public static final String MODEL_STEP_FIND = MODEL_STEP + "/find";
    public static final String MODEL_STEP_CREATE = MODEL_STEP;
    public static final String MODEL_STEP_DELETE = MODEL_STEP_ONE;
    public static final String MODEL_STEP_UPDATE = MODEL_STEP_ONE;
    public static final String MODEL_STEP_CHANGE_STATUS = MODEL_STEP_ONE + "/status/{status}";
    public static final String MODEL_STEPS = MODEL_ONE + "/step";

    // Paths for Model Step Metadata CRUD
    public static final String MODEL_STEP_METADATA = MODEL_STEP_ONE + "/metadata";
    public static final String MODEL_STEP_METADATA_CREATE = MODEL_STEP_METADATA;
    public static final String MODEL_STEP_METADATA_DELETE = MODEL_STEP_METADATA;
    public static final String MODEL_STEP_METADATA_UPDATE = MODEL_STEP_METADATA;

    // Paths for Model Steps actions
    public static final String MODEL_STEP_GET_READONLY_FILES = MODEL_STEP_ONE + "/readonly";
    public static final String MODEL_STEP_INSPECT = MODEL_STEP_ONE + "/inspect";
    public static final String MODEL_STEP_INSPECT_SHEETS = MODEL_STEP_INSPECT + "/sheets";
    public static final String MODEL_STEP_INSPECT_CELLS = MODEL_STEP_INSPECT + "/cells";
    public static final String MODEL_STEP_GET_INPUTS = MODEL_STEP_ONE + "/input";
    public static final String MODEL_STEP_GET_OUTPUTS = MODEL_STEP_ONE + "/output";
    public static final String MODEL_STEP_GET_OUTPUT = MODEL_STEP_GET_OUTPUTS + '/' + MODEL_STEP_PARAMETER_ID;
    public static final String MODEL_STEP_CONVERT = MODEL_STEP_ONE + "/convert";
    public static final String MODEL_STEP_CONVERT_JS = MODEL_STEP_CONVERT + "/js";
    public static final String MODEL_STEP_UPLOAD = MODEL_STEP_ONE + "/upload";

    // Paths for Model Step Items CRUD
    public static final String MODEL_ITEM = MODEL_STEP_ONE + "/item";
    public static final String MODEL_ITEM_GET_LIST = MODEL_ITEM + "";
    public static final String MODEL_ITEM_GET_ONE = MODEL_ITEM + "/{itemId}";

    // Paths for Model Step Parameters CRUD
    public static final String MODEL_STEP_PARAMETER = MODEL_STEP_ONE + "/parameter";
    public static final String MODEL_STEP_PARAMETER_LIST = MODEL_STEP_PARAMETER;
    public static final String MODEL_STEP_PARAMETER_ONE = MODEL_STEP_PARAMETER + '/' + MODEL_STEP_PARAMETER_ID;
    public static final String MODEL_STEP_PARAMETER_CREATE = MODEL_STEP_PARAMETER;
    public static final String MODEL_STEP_PARAMETER_UPDATE = MODEL_STEP_PARAMETER;
    public static final String MODEL_STEP_PARAMETER_DELETE = MODEL_STEP_PARAMETER;

    // Paths for Version
    public static final String VERSION = SERVICES + "/version";

    // Paths for User Groups CRUD
    public static final String USER_GROUP = SERVICES + "/usergroup";
    public static final String USER_GROUP_LIST = USER_GROUP;
    public static final String USER_GROUP_ONE = USER_GROUP + "/{userGroupId}";
    public static final String USER_GROUP_FOR_ASSIGNMENT = USER_GROUP + "/assignment";
    public static final String USER_GROUP_CREATE = USER_GROUP;
    public static final String USER_GROUP_DELETE = USER_GROUP_ONE;
    public static final String USER_GROUP_UPDATE = USER_GROUP_ONE;

    // Paths for Users CRUD
    public static final String USER = SERVICES + "/user";
    public static final String USER_LIST = USER;
    public static final String USER_ACTIVE_LIST = USER + "/active";
    public static final String USER_INACTIVE_LIST = USER + "/inactive";
    public static final String USER_ONE = USER + "/{userId}";
    public static final String USER_CREATE = USER;
    public static final String USER_ACTIVATE = USER_ONE + "/activate";
    public static final String USER_DEACTIVATE = USER_ONE + "/deactivate";
    public static final String USER_UPDATE = USER_ONE;

    // Paths for Users actions
    public static final String USER_CURRENT = USER + "/current";

    // Paths for work with files
    public static final String FILES = SERVICES + "/files";
    public static final String FILES_DOWNLOAD = FILES;

    // Paths for Projects CRUD
    public static final String PROJECT = SERVICES + "/project";
    public static final String PROJECT_LIST = PROJECT;
    public static final String PROJECT_ONE = PROJECT + '/' + PROJECT_ID;
    public static final String PROJECT_CREATE = PROJECT;
    public static final String PROJECT_DELETE = PROJECT_ONE;
    public static final String PROJECT_UPDATE = PROJECT_ONE;

    // Paths for Projects actions
    public static final String PROJECT_GET_DETAILS = PROJECT_ONE + "/details";
    public static final String PROJECT_COPY = PROJECT + "/copy";
    public static final String PROJECT_GET_DELETED = PROJECT + "/deleted";
    public static final String PROJECT_CHANGE_STATUS = PROJECT_ONE + "/status/{status}";

    // Paths for Project's files CRUD
    public static final String PROJECT_FILE = PROJECT_ONE + "/file";
    public static final String PROJECT_FILE_SAVE = PROJECT_FILE;
    public static final String PROJECT_FILE_LIST = PROJECT_FILE;
    public static final String PROJECT_FILE_ONE = PROJECT_FILE + "/" + UUID;
    public static final String PROJECT_FILE_DELETE = PROJECT_FILE_ONE;
    public static final String PROJECT_FILE_UPDATE = PROJECT_FILE_ONE;


    // Paths for Scenarios CRUD
    public static final String SCENARIO = SERVICES + "/project/{projectId}/scenario";
    public static final String SCENARIO_LIST = SCENARIO;
    public static final String SCENARIO_ONE = SCENARIO + "/{scenarioId}";
    public static final String SCENARIO_CREATE = SCENARIO;
    public static final String SCENARIO_DELETE = SCENARIO_ONE;
    public static final String SCENARIO_UPDATE = SCENARIO_ONE;

    // Paths for Scenarios actions
    public static final String SCENARIO_GET_BY_TAG = SCENARIO + "/tag";
    public static final String SCENARIO_GET_BY_STATUS = SCENARIO + "/status/{status}";
    public static final String SCENARIO_GET_DELETED = SCENARIO + "/deleted";
    public static final String SCENARIO_GET_BY_PLAYLIST = PROJECT_ONE + "/playlist/" + PLAYLIST_ID + "/scenario";
    public static final String SCENARIO_COPY = SCENARIO + "/copy";
    public static final String SCENARIO_RENAME = SCENARIO_ONE + "/rename";

    // Paths for Analyses CRUD
    public static final String ANALYSIS = SERVICES + "/project/{projectId}/analysis";
    public static final String ANALYSIS_LIST = ANALYSIS;
    public static final String ANALYSIS_ONE = ANALYSIS + "/{analysisId}";
    public static final String ANALYSIS_CREATE = ANALYSIS;
    public static final String ANALYSIS_DELETE = ANALYSIS_ONE;
    public static final String ANALYSIS_UPDATE = ANALYSIS_ONE;

    // Paths for Analyses actions
    public static final String ANALYSIS_GET_BY_TAG = ANALYSIS + "/tag";
    public static final String ANALYSIS_GET_BY_STATUS = ANALYSIS + "/status/{status}";
    public static final String ANALYSIS_GET_DELETED = ANALYSIS + "/deleted";
    public static final String ANALYSIS_GET_BY_PLAYLIST = PROJECT_ONE + "/playlist/" + PLAYLIST_ID + "/analysis";
    public static final String ANALYSIS_COPY = ANALYSIS + "/copy";
    public static final String ANALYSIS_RENAME = ANALYSIS_ONE + "/rename";
    public static final String ANALYSIS_GET_TORNADO = ANALYSIS_ONE + "/tornado";

    // Paths for Tags actions
    public static final String TAG = SERVICES + "/tag";
    public static final String TAG_LIST = TAG;
    public static final String TAG_SEARCH = TAG + "/search/{value}";
    public static final String TAG_GET_BY_SCENARIO = TAG + "/scenario/{scenarioId}";
    public static final String TAG_GET_BY_ANALYSIS = TAG + "/analysis/{analysisId}";

    // Paths for Collaboration Workspace actions
    public static final String WORKSPACE = SERVICES + "/workspace";
    public static final String WORKSPACE_LIST = WORKSPACE;
    public static final String WORKSPACE_BY_USER = WORKSPACE + "/user";
    public static final String WORKSPACE_ONE = WORKSPACE + "/{workspaceId}";
    public static final String WORKSPACE_DELETE = WORKSPACE_ONE;
    public static final String WORKSPACE_CREATE = WORKSPACE;
    public static final String WORKSPACE_UPDATE = WORKSPACE_ONE;

    // Paths for Roles actions
    public static final String ROLE = SERVICES + "/role";
    public static final String ROLE_GET_LIST = ROLE;
    public static final String AVAILABLE_USER_TYPES = ROLE + "/usertypes";

    // Paths for Evaluations actions
    public static final String EVALUATION = SERVICES + "/evaluation";
    public static final String EVALUATION_ONE = EVALUATION + "/{evaluationId}";
    public static final String EVALUATION_RUN_SCENARIO = SCENARIO_ONE + "/run";
    public static final String EVALUATION_RUN_ANALYSIS = ANALYSIS_ONE + "/run";
    public static final String EVALUATION_GET_OUTPUT = EVALUATION + "/output";
    public static final String EVALUATION_GET_OUTPUTS = EVALUATION + "/outputs";
    public static final String EVALUATION_GET_ERROR = EVALUATION + "/job/error/{uuid}";
    public static final String EVALUATION_RERUN = EVALUATION + "/rerun";
    public static final String EVALUATION_SUSPEND = EVALUATION + "/suspend";
    public static final String EVALUATION_RERUN_SCENARIO = SCENARIO_ONE + "/rerun";
    public static final String EVALUATION_RERUN_ANALYSIS = ANALYSIS_ONE + "/rerun";
    public static final String EVALUATION_SUSPEND_SCENARIO = SCENARIO_ONE + "/suspend";
    public static final String EVALUATION_SUSPEND_ANALYSIS = ANALYSIS_ONE + "/suspend";

    // Paths for Auth actions
    public static final String AUTH = SERVICES + "/auth";
    public static final String AUTH_LOGIN = AUTH + "/login";
    public static final String AUTH_LOGOUT = AUTH + "/logout";

    // CustomTable
    public static final String CUSTOM_TABLE = SERVICES + "/customtable";
    public static final String CUSTOM_TABLE_ONE = CUSTOM_TABLE + '/' + CUSTOM_TABLE_ID;
    public static final String CUSTOM_TABLE_LIST = CUSTOM_TABLE;
    public static final String CUSTOM_TABLE_MODEL_STEP_PARAM_LIST =
            CUSTOM_TABLE + "/step/" + MODEL_STEP_UUID + "/parameter/{stepParam}";
    public static final String CUSTOM_TABLE_LIST_BY_USER = CUSTOM_TABLE + "/user/{userId}";
    public static final String CUSTOM_TABLE_LIST_GLOBAL = CUSTOM_TABLE + "/scope/global";
    public static final String CUSTOM_TABLE_CREATE = CUSTOM_TABLE;
    public static final String CUSTOM_TABLE_UPDATE = CUSTOM_TABLE_ONE;
    public static final String CUSTOM_TABLE_DELETE = CUSTOM_TABLE_ONE;

    // Audit
    public static final String AUDIT = SERVICES + "/audit";
    public static final String AUDIT_MODEL = AUDIT + "/model/" + MODEL_UUID;
    public static final String AUDIT_MODEL_STEP = AUDIT + "/step/" + MODEL_STEP_UUID;
    public static final String AUDIT_PROJECT = AUDIT + "/project/" + PROJECT_ID;

    // Paths for Projects CRUD Playlist
    public static final String PLAYLIST = PROJECT_ONE + "/playlist";
    public static final String PLAYLIST_LIST = PLAYLIST;
    public static final String PLAYLIST_SEARCH = PLAYLIST + "/search/{value}";
    public static final String PLAYLIST_ONE = PLAYLIST + '/' + PLAYLIST_ID;
    public static final String PLAYLIST_CREATE = PLAYLIST;
    public static final String PLAYLIST_DELETE = PLAYLIST_ONE;
    public static final String PLAYLIST_UPDATE = PLAYLIST_ONE;

    // Paths for Templates CRUD
    public static final String TEMPLATE = SERVICES + "/template";
    public static final String TEMPLATE_LIST = TEMPLATE;
    public static final String TEMPLATE_LIST_BY_MODEL_STEP = MODEL_STEP_ONE + "/template";
    public static final String TEMPLATE_LIST_BY_MODEL_STEP_PUBLISHED = TEMPLATE_LIST_BY_MODEL_STEP + "/published";

    public static final String TEMPLATE_LIST_BY_PROJECT = PROJECT_ONE + "/template";
    public static final String TEMPLATE_DEFAULT_BY_PROJECT = TEMPLATE_LIST_BY_PROJECT + "/default";
    public static final String TEMPLATE_ONE = TEMPLATE + '/' + TEMPLATE_ID;
    public static final String TEMPLATE_CREATE = TEMPLATE;
    public static final String TEMPLATE_DELETE = TEMPLATE_ONE;
    public static final String TEMPLATE_UPDATE = TEMPLATE_ONE;
    public static final String TEMPLATE_CHANGE_STATUS = TEMPLATE_ONE + "/status/{status}";
    // ========== methods ==========

    private static final Pattern TAG_OPEN = Pattern.compile("\\}");
    private static final Pattern TAG_CLOSE = Pattern.compile("\\{");

    private PathUtils() {
    }

    public static String urlCustomTableOne(long customTableId) {
        String url = CUSTOM_TABLE_ONE;
        url = replace(url, CUSTOM_TABLE_ID, Long.toString(customTableId));
        return url;
    }

    public static String urlModelStepParameterOne(UUID stepUuid, long paramId) {
        String url = MODEL_STEP_PARAMETER_ONE;
        url = replace(url, MODEL_STEP_UUID, stepUuid.toString());
        url = replace(url, MODEL_STEP_PARAMETER_ID, Long.toString(paramId));
        return url;
    }

    public static String urlModelStepOne(UUID stepUuid) {
        String url = MODEL_STEP_ONE;
        url = replace(url, MODEL_STEP_UUID, stepUuid.toString());
        return url;
    }

    /**
     * Replace all {@code {key}} in the given string with {@code value}.
     * E.g. <i>str=/step/{stepId}/param, key=stepId, value=123</i> then return <i>str=/step/123/param</i>
     *
     * @param str   string
     * @param key   key string
     * @param value value to replace key
     * @return incoming {@code str} with replaced {@code key} with given {@code value}
     */
    public static String replace(String str, String key, String value) {
        return str.replaceAll(TAG_OPEN.matcher(TAG_CLOSE.matcher(key).replaceAll("\\\\{")).replaceAll("\\\\}"), value);
    }
}
