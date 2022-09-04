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
package cop.raml.it.foo.exception;

import org.springframework.http.HttpStatus;

public class ExceptionBody {

    private static final int MAGIC_1024 = 1024;

    private HttpStatus status;
    private Boolean success;
    private Boolean warning;
    private Boolean isModal;
    private String message;
    private String fullLog;
    private ExceptionBody cause;

    public ExceptionBody() {
    }

    public ExceptionBody(final HttpStatus status, final Throwable e, final boolean warning) {
    }

    public ExceptionBody(final HttpStatus status, final Throwable e, final boolean warning, final boolean isModal) {
    }

}
