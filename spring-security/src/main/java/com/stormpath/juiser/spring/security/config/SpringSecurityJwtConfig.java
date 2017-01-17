package com.stormpath.juiser.spring.security.config;

import com.stormpath.juiser.jwt.config.JwtConfig;

/**
 * @since 0.1.0
 */
public class SpringSecurityJwtConfig extends JwtConfig {

    private String grantedAuthoritiesExpression;

    public SpringSecurityJwtConfig() {
        super();
    }

    public String getGrantedAuthoritiesExpression() {
        return grantedAuthoritiesExpression;
    }

    public void setGrantedAuthoritiesExpression(String grantedAuthoritiesExpression) {
        this.grantedAuthoritiesExpression = grantedAuthoritiesExpression;
    }
}
