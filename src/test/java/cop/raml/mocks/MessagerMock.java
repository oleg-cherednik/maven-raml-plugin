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
package cop.raml.mocks;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * @author Oleg Cherednik
 * @since 18.12.2016
 */
public final class MessagerMock implements Messager {
    public static final Messager NULL = new MessagerMock();

    private String msg;

    public String getMessage() {
        return msg;
    }

    public void clearMessage() {
        msg = null;
    }

    // ========== Messager ==========

    @Override
    public void printMessage(Diagnostic.Kind kind, CharSequence msg) {
        this.msg = toString(msg);
    }

    @Override
    public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e) {
        this.msg = toString(msg);
    }

    @Override
    public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e, AnnotationMirror a) {
        this.msg = toString(msg);
    }

    @Override
    public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e, AnnotationMirror a, AnnotationValue v) {
        this.msg = toString(msg);
    }

    // ========== static ==========

    private static String toString(CharSequence msg) {
        if (msg == null)
            return null;
        if (msg.length() == 0)
            return "";
        return String.valueOf(msg);
    }
}
