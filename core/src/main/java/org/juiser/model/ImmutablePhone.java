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
package org.juiser.model;

import io.jsonwebtoken.lang.Assert;

/**
 * @since 0.1.0
 */
public class ImmutablePhone implements Phone {

    private final String number;
    private final String digitString;
    private final String name;
    private final String description;
    private final boolean verified;

    public ImmutablePhone(String number, String name, String description, boolean verified) {
        Assert.hasText(number, "number argument cannot be null or empty.");
        this.number = number;
        this.digitString = digitsOnly(number);
        this.name = name;
        this.description = description;
        this.verified = verified;
    }

    private static String digitsOnly(String number) {
        StringBuilder sb = new StringBuilder(number.length());
        for (char c : number.toCharArray()) {
            if (c >= '0' && c <= '9') {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getNumber() {
        return this.number;
    }

    @Override
    public boolean isVerified() {
        return this.verified;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ImmutablePhone) {
            ImmutablePhone phone = (ImmutablePhone) obj;
            return digitString.equals(phone.digitString);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return digitString.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (name != null) {
            sb.append(name).append(": ");
        }
        sb.append(number).append(" (").append(verified ? "verified" : "unverified");
        if (description != null) {
            sb.append(", ").append(description);
        }
        sb.append(")");

        return sb.toString();
    }
}
