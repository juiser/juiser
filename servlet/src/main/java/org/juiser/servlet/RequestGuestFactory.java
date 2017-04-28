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

import org.juiser.model.User;
import org.juiser.model.UserBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

/**
 * A simple function that ignores the request and always returns a non-null 'guest' (unauthenticated) user.
 *
 * @since 1.0.0
 */
public class RequestGuestFactory implements Function<HttpServletRequest, User> {

    @Override
    public User apply(HttpServletRequest httpServletRequest) {
        return new UserBuilder().setGivenName("Guest").setFamilyName("Guest").setAuthenticated(false).build();
    }
}
