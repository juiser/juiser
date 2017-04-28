/*
 * Copyright 2017 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.juiser.io;

import io.jsonwebtoken.lang.Assert;

import java.io.InputStream;

/**
 * @since 1.0.0
 */
public class DefaultResource implements Resource {

    private final InputStream is;
    private final String name;

    public DefaultResource(InputStream is, String name) {
        Assert.notNull(is, "InputStream cannot be null.");
        Assert.hasText(name, "String name argument cannot be null or empty.");
        this.is = is;
        this.name = name;
    }

    @Override
    public InputStream getInputStream() {
        return this.is;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "DefaultResource name='" + getName() + "'";
    }
}
