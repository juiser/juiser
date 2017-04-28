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
package org.juiser.jwt;

/**
 * @since 1.0.0
 */
public enum AlgorithmFamily {

    HMAC,
    RSA,
    EC; //Elliptic Curve

    public static AlgorithmFamily forName(String name) {

        if (HMAC.name().equalsIgnoreCase(name)) {
            return HMAC;
        } else if (RSA.name().equalsIgnoreCase(name)) {
            return RSA;
        } else if (EC.name().equalsIgnoreCase(name) ||
            "ECDSA".equalsIgnoreCase(name) ||
            "Elliptic Curve".equalsIgnoreCase(name)) {
            return EC;
        }

        //couldn't associate, invalid arg:
        String msg = "Unrecognized algorithm family name value: " + name;
        throw new IllegalArgumentException(msg);
    }
}
