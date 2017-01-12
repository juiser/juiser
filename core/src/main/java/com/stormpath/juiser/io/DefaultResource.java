package com.stormpath.juiser.io;

import io.jsonwebtoken.lang.Assert;

import java.io.InputStream;

/**
 * @since 0.1.0
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
