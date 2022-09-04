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
package cop.raml.it.foo.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum SecurityActionType {
    PROJECT_READ(powerOfTwo(0)),
    PROJECT_WRITE(powerOfTwo(1)),
    PROJECT_CLONE(powerOfTwo(2)),
    PROJECT_DELETE(powerOfTwo(3)),
    PROJECT_READ_ALL(powerOfTwo(4)),
    PROJECT_MODIFY_OWNER(powerOfTwo(5)),

    SA_READ(powerOfTwo(6)),
    SA_CREATE(powerOfTwo(7)),
    SA_EDIT(powerOfTwo(8)),
    SA_CLONE(powerOfTwo(9)),
    SA_RUN(powerOfTwo(10)),
    SA_DELETE(powerOfTwo(11)),
    SA_READ_ALL(powerOfTwo(12)),

    RESULT_READ(powerOfTwo(13)),
    RESULT_PUBLISH(powerOfTwo(14)),

    PLAYLIST_READ(powerOfTwo(15)),
    PLAYLIST_CREATE(powerOfTwo(16)),
    PLAYLIST_EDIT(powerOfTwo(17)),
    PLAYLIST_DELETE(powerOfTwo(18)),

    USER_ADD(powerOfTwo(19)),
    USER_DEACTIVATE(powerOfTwo(20)),

    USER_GROUP_EDIT(powerOfTwo(21)),
    USER_GROUP_MANAGE_USERS(powerOfTwo(22)),
    USER_GROUP_REMOVE(powerOfTwo(23)),

    MODEL_READ(powerOfTwo(24)),
    MODEL_WRITE(powerOfTwo(25)),
    MODEL_PUBLISH(powerOfTwo(26)),

    TEMPLATE_READ(powerOfTwo(27)),
    //TODO: replace in TemplateService and delete from here when UI for TEMPLATE_CREATE_EDIT and TEMPLATE_DELETE will be ready
    TEMPLATE_WRITE(powerOfTwo(28)),
    TEMPLATE_PUBLISH(powerOfTwo(29)),

    MODEL_INSPECT(powerOfTwo(30)),
    MODEL_DOWNLOAD(powerOfTwo(31)),

    PROJECT_CREATE(powerOfTwo(32)),
    PROJECT_FILE(powerOfTwo(33)),

    TEMPLATE_CREATE_EDIT(powerOfTwo(34)),
    TEMPLATE_DELETE(powerOfTwo(35)),
    OWNER(powerOfTwo(36));

    public static final long OWNER_PERMISSIONS = union(SecurityActionType.values());
    public static final long GENERAL_PERMISSIONS =
            SA_CREATE.bitMask
            | USER_ADD.bitMask
            | USER_DEACTIVATE.bitMask
            | USER_GROUP_EDIT.bitMask
            | USER_GROUP_MANAGE_USERS.bitMask
            | USER_GROUP_REMOVE.bitMask
            | MODEL_PUBLISH.bitMask
            | MODEL_READ.bitMask
            | MODEL_WRITE.bitMask
            | PROJECT_READ.bitMask
            | PROJECT_CREATE.bitMask
            | PROJECT_FILE.bitMask
            | SA_READ.bitMask
            | RESULT_READ.bitMask
            | TEMPLATE_READ.bitMask
            | PLAYLIST_READ.bitMask
            | MODEL_INSPECT.bitMask
            | MODEL_DOWNLOAD.bitMask
            | PLAYLIST_CREATE.bitMask;

    public static final long INTERNAL_USER_MAX = SecurityActionType.toBitMask(SecurityActionType.values());
    public static final long INTERNAL_USER_DEFAULT = PROJECT_READ.bitMask
            | PROJECT_WRITE.bitMask
            | PROJECT_CREATE.bitMask
            | PROJECT_CLONE.bitMask
            | PROJECT_FILE.bitMask
            | SA_READ.bitMask
            | SA_READ_ALL.bitMask
            | SA_RUN.bitMask
            | SA_DELETE.bitMask
            | SA_CREATE.bitMask
            | SA_EDIT.bitMask
            | SA_CLONE.bitMask
            | RESULT_READ.bitMask
            | RESULT_PUBLISH.bitMask
            | MODEL_READ.bitMask
            | PLAYLIST_DELETE.bitMask
            | PLAYLIST_READ.bitMask
            | PLAYLIST_CREATE.bitMask
            | PLAYLIST_EDIT.bitMask
            | MODEL_INSPECT.bitMask
            | MODEL_DOWNLOAD.bitMask;

    public static final long EXTERNAL_USER_DEFAULT = PROJECT_READ.bitMask
            | SA_READ.bitMask
            | RESULT_READ.bitMask
            | PLAYLIST_READ.bitMask
            | MODEL_READ.bitMask;

    public static final long EXTERNAL_USER_MAX = EXTERNAL_USER_DEFAULT
            | PROJECT_READ.bitMask
            | PROJECT_WRITE.bitMask
            | PROJECT_CLONE.bitMask
            | PROJECT_DELETE.bitMask
            | PROJECT_FILE.bitMask
            | SA_READ.bitMask
            | SA_CREATE.bitMask
            | SA_EDIT.bitMask
            | SA_CLONE.bitMask
            | SA_RUN.bitMask
            | SA_DELETE.bitMask
            | SA_READ_ALL.bitMask
            | RESULT_READ.bitMask
            | RESULT_PUBLISH.bitMask
            | PLAYLIST_READ.bitMask
            | PLAYLIST_CREATE.bitMask
            | PLAYLIST_EDIT.bitMask
            | PLAYLIST_DELETE.bitMask
            | MODEL_INSPECT.bitMask
            | MODEL_DOWNLOAD.bitMask
            | TEMPLATE_CREATE_EDIT.bitMask
            | TEMPLATE_DELETE.bitMask
            | USER_DEACTIVATE.bitMask
            | USER_ADD.bitMask;

    private long bitMask;

    private static long powerOfTwo(long pow) {
        return (long) Math.pow(2, pow);
    }

    private static long union(SecurityActionType[] items) {
        long ret = 0;
        for (SecurityActionType item : items) {
            ret |= item.bitMask;
        }
        return ret;
    }

    SecurityActionType(final long bitMask) {
        this.bitMask = bitMask;
    }

    public long bitMask() {
        return bitMask;
    }

    public static SecurityActionType[] fromBitMask(long inputBitMask) {
        List<SecurityActionType> retList = new ArrayList<>();
        for (SecurityActionType action : SecurityActionType.values()) {
            if ((action.bitMask & inputBitMask) == action.bitMask) {
                retList.add(action);
            }
        }
        return retList.toArray(new SecurityActionType[retList.size()]);
    }

    public static long toBitMask(SecurityActionType[] securityActionTypes) {
        return Arrays.asList(securityActionTypes).stream()
                .mapToLong(SecurityActionType::bitMask)
                .reduce(0, (ret, permission) -> ret | permission);
    }

    public static String toString(long permissions) {
        return Arrays.toString(fromBitMask(permissions));
    }
}
