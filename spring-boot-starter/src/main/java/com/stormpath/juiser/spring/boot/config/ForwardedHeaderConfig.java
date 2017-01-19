package com.stormpath.juiser.spring.boot.config;

import com.stormpath.juiser.jwt.config.JwtConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

@ConfigurationProperties("juiser.user.header")
public class ForwardedHeaderConfig<T extends JwtConfig> {

    @SuppressWarnings("WeakerAccess")
    public static final String DEFAULT_NAME = "X-Forwarded-User";

    private String name;

    private T jwt;

    @SuppressWarnings("unchecked")
    public ForwardedHeaderConfig() {
        this((T)new JwtConfig());
    }

    public ForwardedHeaderConfig(T jwt) {
        this.name = DEFAULT_NAME;
        setJwt(jwt);
    }

    public String getName() {
        return name;
    }

    public ForwardedHeaderConfig<T> setName(String name) {
        Assert.hasText(name, "jwt header name cannot be null or empty.");
        this.name = name;
        return this;
    }

    public T getJwt() {
        return jwt;
    }

    public ForwardedHeaderConfig<T> setJwt(T jwt) {
        Assert.notNull(jwt, "jwt config argument cannot be null.");
        this.jwt = jwt;
        return this;
    }
}
