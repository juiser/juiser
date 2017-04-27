/*
 * Copyright 2017 Les Hazlewood and the respective Juiser contributors
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
public class JwkConfig {

    private String algFamily; //which algorithm family is associated with the specified key bytes?  HMAC = shared secret, RSA = RSA PUBLIC key, EC/ECDSA/Elliptic Curve = EC PUBLIC key

    private boolean enabled; //true = jwt should be signed, false = unsigned is ok

    private String encoding; //what encoding was used when specifying the key string? values: base64url, base64, utf8

    private String value; //raw key bytes, encoded using base64url, base64 or utf8 (see 'encoding' above)

    private String resource; //location of the key file

    public JwkConfig() {
        this.enabled = true;
    }

    public String getAlgFamily() {
        return algFamily;
    }

    public void setAlgFamily(String algFamily) {
        this.algFamily = algFamily;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
