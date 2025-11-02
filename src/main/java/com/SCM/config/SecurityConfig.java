package com.SCM.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.SCM.services.impl.SecurityCustomUserDetailService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {
    // user create and login using java code with in memory service
    // spring security userdetailservice ko use krta hai with the help of
    // loadUserByUserName
    // @Bean
    // public UserDetailsService userDetailsService() {

    // UserDetails user1 = User
    // .withDefaultPasswordEncoder() //ye production ke liye nhi hai ye only tsting
    // ke liye hai
    // .username("user123")
    // .password("password")
    // // .roles("ADMIN","USER")
    // .build();

    // UserDetails user2 = User
    // .withUsername("admin123")
    // .password("admin123")
    // .roles("ADMIN", "USER")
    // .build();
    // // hume inmemoryUserDetailsService bhi use nhi krni hai because hume usename
    // and password database se lena hai
    // var inMemoryUserDetailManager = new InMemoryUserDetailsManager(user1, user2);
    // return inMemoryUserDetailManager;
    // }

    // we use database username and password
    @Autowired
    private OAuthenticationSuccessHandler handler;

    @Autowired
    private SecurityCustomUserDetailService userDetailService;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        // DaoAuthenticationProvider ke pas vo sare method jiski help se hum register kr
        // skte hai service

        // we have to bring the object of userDetailsService
        daoAuthenticationProvider.setUserDetailsService(userDetailService);

        // we have to bring the object of password encoder
        // we use the below bean
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    // we manage which page is protected
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        // configurataion
        // urls configure kiye hai ki kon se public rahege aur kon se private rahenge
        httpSecurity.authorizeHttpRequests(autherize -> {
            // autherize.requestMatchers("/home", "/register", "/service").permitAll();

            autherize.requestMatchers("/user/**").authenticated();
            autherize.anyRequest().permitAll();
        });
        // form defaul login

        // agr hame kuchh bhi change krna hua to hum yha aayege form login se related
        httpSecurity.formLogin(formLogin -> {
            // spna custom login page lgayege
            formLogin.loginPage("/login");
            formLogin.loginProcessingUrl("/authenticate");
            formLogin.successForwardUrl("/user/profile");
            // formLogin.failureForwardUrl("/login?eror=true");
            formLogin.usernameParameter("email");
            formLogin.passwordParameter("password");

            // formLogin.failureHandler(new AuthenticationFailureHandler() {

            // @Override
            // public void onAuthenticationFailure(HttpServletRequest request,
            // HttpServletResponse response,
            // AuthenticationException exception) throws IOException, ServletException {
            // // TODO Auto-generated method stub
            // throw new UnsupportedOperationException("Unimplemented method
            // 'onAuthenticationFailure'");
            // }

            // });
            // formLogin.successHandler(new AuthenticationSuccessHandler(){

            // @Override
            // public void onAuthenticationSuccess(HttpServletRequest request,
            // HttpServletResponse response,
            // Authentication authentication) throws IOException, ServletException {
            // // TODO Auto-generated method stub
            // throw new UnsupportedOperationException("Unimplemented method
            // 'onAuthenticationSuccess'");
            // }

            // });

        });
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.logout(logoutForm -> {
            logoutForm.logoutUrl("/logout");
            logoutForm.logoutSuccessUrl("/login?logout=true");
        });
        // oauth configuration
        httpSecurity.oauth2Login(oauth -> {
            oauth.loginPage("/login");
            oauth.successHandler(handler); // we create the new class for succeshandler
        });
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
