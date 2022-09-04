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

import org.apache.maven.plugin.logging.Log;

/**
 * @author Oleg Cherednik
 * @since 13.02.2017
 */
public class LogMock implements Log {
    private String infoContent;
    private String debugContent;
    private String warnContent;
    private String errorContent;

    public String getInfoContent() {
        return infoContent;
    }

    public String getDebugContent() {
        return debugContent;
    }

    public String getWarnContent() {
        return warnContent;
    }

    public String getErrorContent() {
        return errorContent;
    }

    // ========== Log ==========

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(CharSequence content) {
        debugContent = content != null ? content.toString() : null;
    }

    @Override
    public void debug(CharSequence content, Throwable error) {
    }

    @Override
    public void debug(Throwable error) {
    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public void info(CharSequence content) {
        infoContent = content != null ? content.toString() : null;
    }

    @Override
    public void info(CharSequence content, Throwable error) {
    }

    @Override
    public void info(Throwable error) {
    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public void warn(CharSequence content) {
        warnContent = content != null ? content.toString() : null;
    }

    @Override
    public void warn(CharSequence content, Throwable error) {
    }

    @Override
    public void warn(Throwable error) {
    }

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public void error(CharSequence content) {
        errorContent = content != null ? content.toString() : null;
    }

    @Override
    public void error(CharSequence content, Throwable error) {
        errorContent = content != null ? content.toString() : null;
    }

    @Override
    public void error(Throwable error) {
    }
}
