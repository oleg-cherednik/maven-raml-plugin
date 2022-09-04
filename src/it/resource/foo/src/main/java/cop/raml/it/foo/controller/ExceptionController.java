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

import cop.raml.it.foo.exception.ExceptionBody;
import cop.raml.it.foo.exception.FooAlreadyExistException;
import cop.raml.it.foo.exception.FooException;
import cop.raml.it.foo.exception.FooSecurityException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * The type Exception controller.
 */
@ControllerAdvice
public class ExceptionController {
    /**
     * Handle json parse exception.
     *
     * @param ex      the ex
     * @param request the request
     * @return the fail response entity
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionBody> handleJsonParseException(Exception ex, HttpServletRequest request) {
        return null;
    }

    /**
     * Handle already exist exception.
     *
     * @param ex      the ex
     * @param request the request
     * @return the fail response entity
     */
    @ExceptionHandler(FooAlreadyExistException.class)
    public ResponseEntity<ExceptionBody> handleAlreadyExistException(Exception ex, HttpServletRequest request) {
        return null;
    }

    /**
     * Handle Foo exception.
     *
     * @param ex      the ex
     * @param request the request
     * @return the fail response entity
     */
    @ExceptionHandler(FooException.class)
    public ResponseEntity<ExceptionBody> handleFooException(FooException ex, HttpServletRequest request) {
        return null;
    }

    /**
     * Handle exception.
     *
     * @param ex      the ex
     * @param request the request
     * @return the fail response entity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionBody> handleException(Exception ex, HttpServletRequest request) {
        return null;
    }

    @ExceptionHandler(FooSecurityException.class)
    public ResponseEntity<ExceptionBody> handleSecurityException(FooSecurityException ex, HttpServletRequest request) {
        return null;
    }
}
