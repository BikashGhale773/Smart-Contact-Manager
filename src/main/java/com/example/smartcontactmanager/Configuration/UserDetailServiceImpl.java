package com.example.smartcontactmanager.Configuration;

import com.example.smartcontactmanager.entities.User;
import com.example.smartcontactmanager.repository.UserRepositroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepositroy userRepositroy;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //fetching user from database

        //getting username from UserRepository ko getUserNameByEmail
        User user = userRepositroy.getUserByEmail(username);

        if(user==null){
            throw new UsernameNotFoundException("Could not found User !!!");
        }

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        return customUserDetails;
    }
}
