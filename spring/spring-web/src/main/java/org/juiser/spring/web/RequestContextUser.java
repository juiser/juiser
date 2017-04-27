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
package org.juiser.spring.web;

import org.juiser.model.ResolvingUser;
import org.juiser.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class RequestContextUser extends ResolvingUser {

    private static final Logger log = LoggerFactory.getLogger(RequestContextUser.class);

    protected User findUser() {
        try {
            RequestAttributes reqAttr = RequestContextHolder.currentRequestAttributes();

            if (reqAttr instanceof ServletRequestAttributes) {

                ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) reqAttr;

                HttpServletRequest request = servletRequestAttributes.getRequest();

                if (request != null) {
                    Object obj = request.getAttribute(User.class.getName());
                    if (obj instanceof User) {
                        return (User) obj;
                    }
                }
            }
        } catch (IllegalStateException e) {
            log.debug("Unable to obtain request context user via RequestContextHolder.", e);
        }

        return null;
    }
}
