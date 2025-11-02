package com.SCM.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.SCM.entities.User;
import com.SCM.helper.Helper;
import com.SCM.services.UserService;

@ControllerAdvice
public class RootController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserService userService;

    // iske method hr ek request ke liye execute honge
    @ModelAttribute
    public void addLogedInUserInformation(Model model, Authentication authentication) {
        if (authentication == null) {
            return;
        }

        System.out.println("loggedInUser inuser information to the model");
        String username = Helper.getEmailOfLoggedinUser(authentication);
        logger.info("User logged in:{}", username);

        User user = userService.getUserByEmail(username);
        System.out.println(username);

        System.out.println(user.getName());
        System.out.println(user.getEmail());
        model.addAttribute("LogedInUser", user);
    }

}
