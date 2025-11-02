package com.SCM.config;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.SCM.entities.Providers;
import com.SCM.entities.User;
import com.SCM.helper.AppConstants;
import com.SCM.repositories.UserRepo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    // logger
    Logger logger = LoggerFactory.getLogger(OAuthenticationSuccessHandler.class);

    @Autowired
    private UserRepo userRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        logger.info("OAuthenticationSuccessHandler");

        // identify the provider
        var oauth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String authorizedClientRegistrationId = oauth2AuthenticationToken.getAuthorizedClientRegistrationId();

        logger.info("Provider: {}", authorizedClientRegistrationId);

        var oauthUser = (DefaultOAuth2User) authentication.getPrincipal();

        oauthUser.getAttributes().forEach((key, value) -> {
            logger.info("{} : {}", key, value);
        });

        // Create user object
        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setRoleList(List.of(AppConstants.ROLE_USER));
        user.setEmailVerified(true);
        user.setEnabled(true);

        //dummy password
        user.setPassword("dummy");

        // Google login
        if (authorizedClientRegistrationId.equalsIgnoreCase("google")) {
            user.setEmail(oauthUser.getAttribute("email").toString());
            user.setProfilePic(oauthUser.getAttribute("picture").toString());
            user.setName(oauthUser.getAttribute("name").toString());
            user.setProviderUserId(oauthUser.getName());
            user.setProvider(Providers.GOOGLE);
            user.setAbout("this is account is created by Google:");
        }
        // GitHub login
        else if (authorizedClientRegistrationId.equalsIgnoreCase("github")) {
            String email = oauthUser.getAttribute("email") != null
                    ? oauthUser.getAttribute("email").toString()
                    : oauthUser.getAttribute("login").toString() + "@gmail.com";

            String picture = oauthUser.getAttribute("avatar_url").toString();
            String name = oauthUser.getAttribute("login").toString();
            String providerUserId = oauthUser.getName();

            user.setEmail(email);
            user.setProfilePic(picture);
            user.setName(name);
            user.setProviderUserId(providerUserId);
            user.setProvider(Providers.GITHUB);
              user.setAbout("this is account is created by Github:");
        }
        // LinkedIn login
        // else if (authorizedClientRegistrationId.equalsIgnoreCase("linkedin")) {
        //     // LinkedIn attributes (to be added if required)
        // }
        else {
            logger.info("OAuthenticationSuccessHandler: Unknown provider");
        }

        // Check if user already exists, otherwise save
        User existingUser = userRepo.findByEmail(user.getEmail()).orElse(null);
        if (existingUser == null) {
            userRepo.save(user);
            logger.info("New user saved: {}", user.getEmail());
        } else {
            logger.info("User already exists: {}", existingUser.getEmail());
        }

        // Redirect to dashboard
        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/dashboard");
    }
}
