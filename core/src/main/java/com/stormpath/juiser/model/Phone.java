package com.stormpath.juiser.model;

/**
 * @since 0.1.0
 */
public interface Phone {

    String getName(); //friendly name, like 'Home' or 'Work'

    String getDescription();

    String getNumber();

    boolean isVerified();
}
