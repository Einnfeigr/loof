package com.einnfeigr.taskApp.config;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.einnfeigr.taskApp.controller.rest.UserController;
import com.einnfeigr.taskApp.exception.controller.AccessException;
import com.einnfeigr.taskApp.exception.controller.UserNotFoundException;
import com.einnfeigr.taskApp.misc.Util;
import com.einnfeigr.taskApp.pojo.User;

public class UserDetailsServiceImpl implements UserDetailsService {

	private final static Logger logger = 
			LoggerFactory.getLogger(UserDetailsServiceImpl.class);
				
    @Autowired
    private UserController userController;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String name) 
    		throws UsernameNotFoundException {
        User user = null;
		try {
			user = userController.getUser(name);
		} catch (UserNotFoundException | AccessException e) {
			logger.error(Util.EXCEPTION_LOG_MESSAGE, e);
		}
        if(user == null) {
        	throw new UsernameNotFoundException(name+" cannot be found");
        }
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
        return new org.springframework.security.core.userdetails.User(
        		user.getLogin(), user.getPassword(), grantedAuthorities);
    }
}