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

/**
 * @author Oleg Cherednik
 * @since 18.06.2016
 */
final class DevUtils {
    private static final ThreadLocal<RestApi> THREAD_LOCAL_REST_API = new ThreadLocal<>();
    private static final ThreadLocal<String> THREAD_LOCAL_RAML = new ThreadLocal<>();

    public static void setRestApi(RestApi api) {
        THREAD_LOCAL_REST_API.set(api);
    }

    public static RestApi getRestApi() {
        return THREAD_LOCAL_REST_API.get();
    }

    public static void setRaml(String raml) {
        THREAD_LOCAL_RAML.set(raml);
    }

    public static String getRaml() {
        return THREAD_LOCAL_RAML.get();
    }

    public static void remove() {
        THREAD_LOCAL_REST_API.remove();
        THREAD_LOCAL_RAML.remove();
    }

    private DevUtils() {
    }
}
