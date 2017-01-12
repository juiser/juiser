package com.stormpath.juiser.jwt;

/**
 * @since 0.1.0
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
