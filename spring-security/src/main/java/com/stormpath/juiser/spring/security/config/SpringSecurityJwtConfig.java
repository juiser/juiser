package com.stormpath.juiser.spring.security.config;

import com.stormpath.juiser.jwt.config.JwtConfig;

/**
 * @since 0.1.0
 */
public class SpringSecurityJwtConfig extends JwtConfig {

    private String usernameExpression;

    private String dataExpression;

    private String grantedAuthoritiesExpression;

    public SpringSecurityJwtConfig() {
        super();
    }

    public String getUsernameExpression() {
        return usernameExpression;
    }

    public void setUsernameExpression(String usernameExpression) {
        this.usernameExpression = usernameExpression;
    }

    public String getDataExpression() {
        return dataExpression;
    }

    public void setDataExpression(String dataExpression) {
        this.dataExpression = dataExpression;
    }

    public String getGrantedAuthoritiesExpression() {
        return grantedAuthoritiesExpression;
    }

    public void setGrantedAuthoritiesExpression(String grantedAuthoritiesExpression) {
        this.grantedAuthoritiesExpression = grantedAuthoritiesExpression;
    }
}
