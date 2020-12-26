package com.einnfeigr.taskApp.config;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.einnfeigr.taskApp.controller.rest.UserController;
import com.einnfeigr.taskApp.exception.controller.AccessException;
import com.einnfeigr.taskApp.exception.controller.UserNotFoundException;
import com.einnfeigr.taskApp.misc.Util;
import com.einnfeigr.taskApp.pojo.User;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	private final static Logger logger = 
			LoggerFactory.getLogger(WebSecurityConfig.class);
	
	public final static String ADMIN_LOGIN = "admin";
	
	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private UserController userController;
	
	private static String adminPassword;

	public WebSecurityConfig() {
		super();
		String password = System.getenv("adminPassword");
		if(password == null) {
			password = String.valueOf(new Random().nextLong());
			logger.info("Current password: "+password);
		}
		adminPassword = password;
	}
	
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new LoginFailureHandler();
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http
			.csrf().disable()
            .authorizeRequests()
            .antMatchers("/", "/css/*", "/js/*", "/img/*", "/img/icon/*", "/fonts/*", "/register",
            		"/login", "/recovery", "/recovery/*")
            	.permitAll()
            .antMatchers("/users", "/codes", "/id/generate/", "id/generate", "/api/*")
            	.hasRole("ADMIN")
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .loginPage("/login")
			.loginProcessingUrl("/login")
            .defaultSuccessUrl("/", true)
            .failureHandler(authenticationFailureHandler())
            .permitAll()
            .and()
            .logout()
            .logoutUrl("/logout")
            .permitAll();
    }
    
	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		String password; 
		User admin = null;
		try {
			admin = userController.get(ADMIN_LOGIN);
		} catch (UserNotFoundException | AccessException e) {
			logger.error(Util.EXCEPTION_LOG_MESSAGE, e);
		}
		BCryptPasswordEncoder encoder = bCryptPasswordEncoder();
		if(admin == null) {
			logger.info("No admin user found");
			password = bCryptPasswordEncoder().encode(adminPassword);
        	saveAdminUser(password);
        } else {
        	logger.info("Admin password has been set from env properties");
        	password = admin.getPassword();
        	if(!encoder.matches(adminPassword, password)) {
        		admin.setPassword(encoder.encode(adminPassword));
        		userController.save(admin);
        	}
        }
        auth
        	.userDetailsService(userDetailsService)
        	.passwordEncoder(bCryptPasswordEncoder())
        	.and()
        	.inMemoryAuthentication()
        	.withUser(ADMIN_LOGIN)
        	.password(password)
        	.roles("USER", "ADMIN");
	}
	
	private void saveAdminUser(String password) {
		logger.info("Saving new admin user with pass: "+password);
        User admin = new User();
        admin.setName("admin");
        admin.setPassword(password);
        admin.setLogin(ADMIN_LOGIN);
        userController.save(admin);
	}

}