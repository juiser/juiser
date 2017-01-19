package com.stormpath.juiser.spring.web.user;

import com.stormpath.juiser.model.ResolvingUser;
import com.stormpath.juiser.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class RequestContextUser extends ResolvingUser {

    private static final Logger log = LoggerFactory.getLogger(RequestContextUser.class);

    protected User findUser() {
        try {
            RequestAttributes reqAttr = RequestContextHolder.currentRequestAttributes();

            if (reqAttr instanceof ServletRequestAttributes) {

                ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) reqAttr;

                HttpServletRequest request = servletRequestAttributes.getRequest();

                if (request != null) {
                    Object obj = request.getAttribute(User.class.getName());
                    if (obj instanceof User) {
                        return (User) obj;
                    }
                }
            }
        } catch (IllegalStateException e) {
            log.debug("Unable to obtain request context user via RequestContextHolder.", e);
        }

        return null;
    }
}
