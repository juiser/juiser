/*
 * Copyright (C) 2017 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.juiser.spring.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.util.Assert;

/**
 * @since 0.1.0
 */
public class HeaderAuthenticationToken extends AbstractAuthenticationToken {

    private final String headerName;

    private final String headerValue;

    public HeaderAuthenticationToken(String headerName, String headerValue) {
        super(null);
        Assert.hasText(headerName, "headerName cannot be null or empty.");
        this.headerName = headerName;
        Assert.hasText(headerValue, "headerValue cannot be null or empty.");
        this.headerValue = headerValue;
    }

    public String getHeaderName() {
        return headerName;
    }

    @Override
    public Object getCredentials() {
        return this.headerValue;
    }

    @Override
    public Object getPrincipal() {
        return this.headerValue;
    }
}
