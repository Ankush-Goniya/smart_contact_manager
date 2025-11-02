package com.SCM.Controller;

import java.security.Principal;

import com.SCM.entities.User;
import com.SCM.helper.Helper;
import com.SCM.services.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/user")
public class UserController {
    // to show the value at the teminal
    @Autowired
    private UserService userService;
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    // @ModelAttribute
    // public void addLogedInUserInformation(Model model, Authentication authentication) {
    //     System.out.println("loggedInUser in the model");
    //     String username = Helper.getEmailOfLoggedinUser(authentication);
    //     logger.info("User logged in:{}", username);

    //     User user = userService.getUserByEmail(username);
    //     System.out.println(user.getName());
    //     System.out.println(user.getEmail());
    //     model.addAttribute("LogedInUser", user);
    // }

    // user dashboard page
    @RequestMapping(value = "/dashboard")
    public String userDashboard() {
        return "user/dashboard";
    }

    // user add profile page
    @RequestMapping(value = "/profile")
    public String userProfile(Model model, Authentication authentication) {
        // String username = Helper.getEmailOfLoggedinUser(authentication);
        // logger.info("User logged in:{}", username);

        // User user = userService.getUserByEmail(username);
        // System.out.println(user.getName());
        // System.out.println(user.getEmail());
        // model.addAttribute("LogedInUser", user);
        // data base se data ko fetch:get user from db:email,name,address
        return "user/profile";
    }

    // user edit contact page

    // user delete contact page

    // user search contact page
}
