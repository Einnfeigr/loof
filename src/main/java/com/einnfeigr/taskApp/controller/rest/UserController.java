package com.einnfeigr.taskApp.controller.rest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.einnfeigr.taskApp.config.WebSecurityConfig;
import com.einnfeigr.taskApp.exception.controller.AccessException;
import com.einnfeigr.taskApp.exception.controller.AuthUserNotFoundException;
import com.einnfeigr.taskApp.exception.controller.ControllerException;
import com.einnfeigr.taskApp.exception.controller.UserNotFoundException;
import com.einnfeigr.taskApp.misc.Util;
import com.einnfeigr.taskApp.pojo.Code;
import com.einnfeigr.taskApp.pojo.User;
import com.einnfeigr.taskApp.repository.UserRepository;

@RestController
public class UserController {

	private final static Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CodeController codeController;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@GetMapping("api/users/{login}/info")
	public User getUserInfo(@PathVariable("login") Optional<String> login)
			throws CloneNotSupportedException, ControllerException,
			AccessException {
		if(!login.isPresent()) {
			throw new UserNotFoundException();
		}
		return get(login.get());
	}

	@PostMapping(value="api/users/{login}")
	public void updateUser(
			@PathVariable("login") Optional<String> optionalLogin,
			@RequestParam(required=false) String name,
			@RequestParam(required=false) String login,
			@RequestParam(required=false) String password,
			@RequestParam(required=false) String oldPassword,
			@RequestParam(required=false) String fbLink,
			@RequestParam(required=false) String igLink,
			@RequestParam(required=false) String vkLink
			) throws IOException, AccessException {
		String currentLogin;
		if(optionalLogin.isPresent()) {
			currentLogin = optionalLogin.get();
		} else {
			throw new UserNotFoundException();
		}
		User user = get(currentLogin);
		if(name != null && name.length() > 0) {
			user.setName(name);
		}
		if(login != null && login.length() > 0) {
			user.setLogin(login);
		}
		if(password != null && password.length() > 0) {
			if(!passwordEncoder.matches(oldPassword, user.getPassword())) {
				throw new IllegalArgumentException("Неверный старый пароль");
			}
			user.setPassword(passwordEncoder.encode(password));
		}
		userRepository.save(user);
	}
	
	@PostMapping("api/users/add")
	public User addUser(@RequestParam String id, 
			@RequestParam String login,
			@RequestParam String password,
			@RequestParam String name,
			@RequestParam String email) 
					throws AuthUserNotFoundException, AccessException {
		User user = new User();
		Code code = codeController.get(id);
		user.setCode(code);
		user.setName(name);
		user.setLogin(login);
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));
		user = userRepository.save(user);
		codeController.save(code);
		return user;
	}
		
	public void removeUser(User user) throws UserNotFoundException, AccessException {
		removeUser(Optional.of(user.getLogin()));
	}
	
	@PostMapping("api/users/delete")		
	public void removeUser(@RequestParam("login") Optional<String> optionalLogin) 
					throws UserNotFoundException, AccessException {
		if(!optionalLogin.isPresent()) {
			throw new UserNotFoundException();
		}
		String login = optionalLogin.get();
		User user = get(login);
		codeController.delete(user.getCode());
		userRepository.delete(user);
	}
	
	public void save(User user) {
		userRepository.save(user);
	}
	
	public List<User> getAll() {
		return userRepository.findAll();
	}
	
	public static boolean isAuthAdmin() {
		return getAuthLogin().equals(WebSecurityConfig.ADMIN_LOGIN);
	}
	
	public User getAuthUser() throws AuthUserNotFoundException {
		try {
			return userRepository.findByLogin(UserController.getAuthLogin());
		} catch(NullPointerException e) {
			log.error(Util.EXCEPTION_LOG_MESSAGE, e);
			return null;
		}
	}
	
	public User get(String login) throws UserNotFoundException, AccessException {
		User user = userRepository.findByLogin(login);
		if(user == null && (user = userRepository.findByEmail(login)) == null) {
			throw new UserNotFoundException();
		}
		return user;
	}
	
	public static String getAuthLogin() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

}