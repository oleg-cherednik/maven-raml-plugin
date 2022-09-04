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

public class FooException extends Exception {

    private boolean warning = false;
    private boolean isModal = false;

    /**
     * Default constructor
     */
    public FooException() {
    }

    /**
     * Constructor
     *
     * @param message the detail message
     */
    public FooException(final String message) {
        super(message);
    }

    /**
     * Constructor
     *
     * @param message the detail message
     * @param warning warning flag
     */
    public FooException(final String message, final boolean warning) {
        super(message);
        this.warning = warning;
    }

    /**
     * Constructor
     *
     * @param message the detail message
     * @param warning warning flag
     * @param isModal isModal flag
     */
    public FooException(final String message, final boolean warning, final boolean isModal) {
        super(message);
        this.warning = warning;
        this.isModal = isModal;
    }

    /**
     * Constructs a new exception with the specified detail message and cause
     *
     * @param message the detail message
     * @param cause   the cause which is saved for later retrieval
     */
    public FooException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specific cause
     *
     * @param cause the cause which is saved for later retrieval
     */
    public FooException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the specified detail message, cause,
     * suppression enabled or disabled, and writable stack trace enabled or
     * disabled.
     *
     * @param message            the detail message
     * @param cause              the cause which is saved for later retrieval
     * @param enableSuppression  whether or not suppression is enabled or
     *                           disabled
     * @param writableStackTrace whether or not the stack trace should be
     *                           writable
     */
    public FooException(final String message, final Throwable cause, final boolean enableSuppression,
                        final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public boolean isWarning() {
        return warning;
    }

    public void setWarning(boolean warning) {
        this.warning = warning;
    }

    public boolean isModal() {
        return isModal;
    }

    public void setModal(boolean isModal) {
        this.isModal = isModal;
    }
}
