/*
 * Copyright (C) 2017 Stormpath, Inc.
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
package org.juiser.jwt.config;

/**
 * @since 0.1.0
 */
public class JwtConfig {

    public static final long DEFAULT_CLOCK_SKEW_SECONDS = 3 * 60; // 3 minutes

    private boolean enabled; //true = a jwt is expected, false = simple string or json is expected

    private Long allowedClockSkewSeconds; //seconds by which the gateway and origin server clocks are allowed to differ

    private JwkConfig key; //JWT key (JWK) config

    private String userClaimName; //claim in the JWT that represents the entire user object or null if not nested

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

    public String getUserClaimName() {
        return userClaimName;
    }

    public void setUserClaimName(String userClaimName) {
        this.userClaimName = userClaimName;
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
