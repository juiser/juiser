package com.stormpath.juiser.spring.boot.config;

import com.stormpath.juiser.spring.security.config.SpringSecurityJwtConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @since 1.3.0
 */
@ConfigurationProperties("juiser.user.header")
public class ForwardedHeaderConfig {

    @SuppressWarnings("WeakerAccess")
    public static final String DEFAULT_NAME = "X-Forwarded-User";

    private String name;

    private SpringSecurityJwtConfig jwt;

    public ForwardedHeaderConfig() {
        this.name = DEFAULT_NAME;
        this.jwt = new SpringSecurityJwtConfig();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SpringSecurityJwtConfig getJwt() {
        return jwt;
    }

    public void setJwt(SpringSecurityJwtConfig jwt) {
        this.jwt = jwt;
    }
}
