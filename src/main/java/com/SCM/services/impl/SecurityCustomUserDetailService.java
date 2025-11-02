package com.SCM.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.SCM.repositories.UserRepo;

@Service
public class SecurityCustomUserDetailService implements UserDetailsService{
    @Autowired
     private UserRepo userRepo;
     
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       //apne user ko load krana hai

      return userRepo.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("user not found with this email :"));
    }

}
