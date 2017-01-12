package com.stormpath.juiser.examples;

import com.stormpath.juiser.spring.security.core.userdetails.ForwardedUserDetails;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WelcomeController {

    @RequestMapping("/")
    public String welcome(Model model) {

        String name = "World"; //for guests

        ForwardedUserDetails user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            user = (ForwardedUserDetails) authentication.getPrincipal();
        }

        if (user != null) {
            name = user.getUsername();
            model.addAttribute("user", user);
        }

        model.addAttribute("name", name);

        return "welcome";
    }
}
