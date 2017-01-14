package com.stormpath.juiser.spring.security.config;

import com.stormpath.juiser.jwt.config.JwtConfig;

/**
 * @since 0.1.0
 */
public class SpringSecurityJwtConfig extends JwtConfig {

    private String userClaimName;

    private String grantedAuthoritiesExpression;

    public SpringSecurityJwtConfig() {
        super();
    }

    public String getUserClaimName() {
        return userClaimName;
    }

    public void setUserClaimName(String userClaimName) {
        this.userClaimName = userClaimName;
    }

    public String getGrantedAuthoritiesExpression() {
        return grantedAuthoritiesExpression;
    }

    public void setGrantedAuthoritiesExpression(String grantedAuthoritiesExpression) {
        this.grantedAuthoritiesExpression = grantedAuthoritiesExpression;
    }
}
