package com.stormpath.juiser.jwt.config;

/**
 * @since 0.1.0
 */
public class JwtConfig {

    public static final long DEFAULT_CLOCK_SKEW_SECONDS = 3 * 60; // 3 minutes

    private boolean enabled; //true = a jwt is expected, false = simple string or json is expected

    private Long allowedClockSkewSeconds; //seconds by which the gateway and origin server clocks are allowed to differ

    private JwkConfig key; //JWT key (JWK) config

    public JwtConfig() {
        this.enabled = true;
        this.key = new JwkConfig();
        this.allowedClockSkewSeconds = DEFAULT_CLOCK_SKEW_SECONDS;
    }

    public Long getAllowedClockSkewSeconds() {
        return allowedClockSkewSeconds;
    }

    public void setAllowedClockSkewSeconds(Long allowedClockSkewSeconds) {
        this.allowedClockSkewSeconds = allowedClockSkewSeconds;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public JwkConfig getKey() {
        return key;
    }

    public void setKey(JwkConfig key) {
        this.key = key;
    }
}
