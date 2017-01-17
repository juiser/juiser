package com.stormpath.juiser.spring.boot.config;

import com.stormpath.juiser.jwt.config.JwtConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("juiser.user.header")
public class ForwardedHeaderConfig<T extends JwtConfig> {

    @SuppressWarnings("WeakerAccess")
    public static final String DEFAULT_NAME = "X-Forwarded-User";

    private String name;

    private T jwt;

    @SuppressWarnings("unchecked")
    public ForwardedHeaderConfig() {
        this.name = DEFAULT_NAME;
        T jwt = (T) new JwtConfig();
        setJwt(jwt);
    }

    public String getName() {
        return name;
    }

    public ForwardedHeaderConfig<T> setName(String name) {
        this.name = name;
        return this;
    }

    public T getJwt() {
        return jwt;
    }

    public ForwardedHeaderConfig<T> setJwt(T jwt) {
        this.jwt = jwt;
        return this;
    }
}
