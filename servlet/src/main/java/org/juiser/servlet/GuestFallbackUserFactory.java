/*
 * Copyright 2017 Les Hazlewood and the respective Juiser contributors
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

package org.juiser.servlet;

import io.jsonwebtoken.lang.Assert;
import org.juiser.model.User;
import org.juiser.model.UserBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

/**
 * A function that delegates user creation to a <code>Function&lt;HttpServletRequest,User&gt;</code> function. If
 * that function returns null or throws an exception, a fallback guest factory function will be invoked to create
 * a 'guest' User instance.  Finally, if the fallback guest factory function itself fails or returns null, a suitable
 * simple guest {@code User} instance will be returned.
 * <p>This implementation never returns null or propagates exceptions to ensure that a User account instance is
 * always accessible to the caller to help eliminate null-pointer exceptions in user/security-related code.</p>
 * <p>Any exceptions thrown when invoking the delegate/fallback functions will be logged as {@code warn} statements
 * to help with troubleshooting.</p>
 *
 * @since 1.0.0
 */
public class GuestFallbackUserFactory implements Function<HttpServletRequest, User> {

    private static final Logger log = LoggerFactory.getLogger(GuestFallbackUserFactory.class);

    private final Function<HttpServletRequest, User> delegate;
    private final Function<HttpServletRequest, User> guestFallback;

    public GuestFallbackUserFactory(Function<HttpServletRequest, User> delegate,
                                    Function<HttpServletRequest, User> guestFallback) {
        Assert.notNull(delegate, "delegate function cannot be null");
        Assert.notNull(guestFallback, "guestFallback function cannot be null");
        this.delegate = delegate;
        this.guestFallback = guestFallback;
    }

    @Override
    public User apply(HttpServletRequest request) {

        User user = null;

        try {
            user = delegate.apply(request);
        } catch (Exception e) {
            log.warn("Could not create a User instance based on the inbound HttpServletRequest via delegate " +
                "function.  Attempting to acquire a guest user instance instead...", e);
        }

        if (user == null) {
            try {
                user = guestFallback.apply(request);
            } catch (Exception e) {
                log.warn("Could not create a guest User instance via fallback guest creation function. " +
                    "Defaulting to a simple guest User instance...", e);
            }
        }

        if (user == null) {
            user = new UserBuilder().setGivenName("Guest").setAuthenticated(false).build();
        }

        assert user != null;

        return user;
    }
}
