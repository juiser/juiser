package com.stormpath.juiser.spring.security.core;

import com.stormpath.juiser.model.User;
import org.springframework.security.core.Authentication;

/**
 * @since 0.1.0
 */
public interface UserAuthentication extends Authentication {

    User getUser();
}
