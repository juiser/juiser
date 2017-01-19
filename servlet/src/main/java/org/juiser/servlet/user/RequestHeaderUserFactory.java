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
package org.juiser.servlet.user;

import org.juiser.model.User;
import org.juiser.model.UserBuilder;
import io.jsonwebtoken.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

/**
 * Inspects a Request header and creates a {@link User} instance based on the header value.  Binding the resulting
 * User to the request (or anywhere else) is the responsibility of the caller.
 */
public class RequestHeaderUserFactory implements Function<HttpServletRequest, User> {

    private static final Logger log = LoggerFactory.getLogger(RequestHeaderUserFactory.class);

    private final String headerName;

    private final Function<String, User> stringToUserConverter;

    public RequestHeaderUserFactory(String headerName, Function<String, User> stringToUserConverter) {
        Assert.hasText(headerName, "headerName argument cannot be null or empty.");
        Assert.notNull(stringToUserConverter, "stringToUserConverter cannot be null.");
        this.headerName = headerName;
        this.stringToUserConverter = stringToUserConverter;
    }

    @Override
    public User apply(HttpServletRequest request) {
        String value = request.getHeader(headerName);
        Assert.hasText(value, "header value cannot be null or empty.");

        User user;

        try {
            user = stringToUserConverter.apply(value);
        } catch (Exception e) {
            if (log.isInfoEnabled()) {
                String msg = "Unable to determine request User based on " + headerName + " header value [" + value +
                    "].  Returning an anonymous user.  Message: " + e.getMessage();
                log.info(msg);
            }
            user = new UserBuilder().setGivenName("Guest").setAuthenticated(false).build();
        }

        return user;
    }
}
