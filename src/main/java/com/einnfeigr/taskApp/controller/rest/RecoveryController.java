package com.einnfeigr.taskApp.controller.rest;

import java.io.IOException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.einnfeigr.taskApp.exception.controller.AccessException;
import com.einnfeigr.taskApp.exception.controller.UserNotFoundException;
import com.einnfeigr.taskApp.misc.Util;
import com.einnfeigr.taskApp.pojo.RecoveryCode;
import com.einnfeigr.taskApp.pojo.User;
import com.einnfeigr.taskApp.repository.RecoveryCodeRepository;

@RestController
public class RecoveryController {
	
	private static final Logger log = LoggerFactory.getLogger(RecoveryController.class);
	
	@Autowired
	private RecoveryCodeRepository repository;
	
	@Autowired
	private UserController userController;
	
	private final static int CODE_LENGTH = 15;
	
	@PostMapping("/api/recovery/generate")
	public RecoveryCode generate(@RequestParam String login) 
			throws UserNotFoundException, AccessException {
		User user = userController.get(login);
		if(user.getRecoveryCode() != null) {
			remove(user.getRecoveryCode().getCode());
			log.info("Recovery code for user \'"+login
					+"\' will be deleted because new one is being generated");
			user.setRecoveryCode(null);
			userController.save(user);
		}
		return createCode(Util.generateCode(CODE_LENGTH), userController.get(login), 
				LocalDateTime.now().plusDays(1));
	}
	
	private RecoveryCode createCode(String code, User user, LocalDateTime expires) {
		RecoveryCode recoveryCode = new RecoveryCode();
		recoveryCode.setCode(code);
		recoveryCode.setUser(user);
		recoveryCode.setExpires(expires);
		repository.save(recoveryCode);
		log.info("Recovery code \'"+code+"\' for user \'"+user.getLogin()+"\' has been created");
		return recoveryCode;
	}
	
	@PostMapping("/api/recovery/consume")
	public void consume(@RequestParam String code, 
			@RequestParam String password) throws AccessException, IOException {
		RecoveryCode recoveryCode = get(code);
		if(recoveryCode == null) {
			throw new AccessException("Code cannot be found");
		}
		User user = recoveryCode.getUser();
		userController.updateUser(null, null, null, password, null, null, null);
		user.setRecoveryCode(null);
		userController.save(user);
		log.info("User password has been changed");
		remove(code);
		log.info("Code \'"+code+"\' has been consumed by user \'"
				+user.getLogin()+"\' to change password");
	}
	
	@GetMapping("/api/recovery/get")
	public RecoveryCode get(@RequestParam String code) {
		RecoveryCode recoveryCode = repository.findByCode(code);
		if(recoveryCode == null) {
			throw new IllegalArgumentException("Code cannot be found");
		}
		if(recoveryCode.getExpires().isBefore(LocalDateTime.now())) {
			remove(code);
			log.info("Recovery code \'"+code+"\' has been deleted because it expired");
			return null;
		}
		return recoveryCode;
	}
	
	@PostMapping("api/recovery/remove")
	public void remove(String code) {
		repository.deleteById(code);
		log.info("Code \'"+code+"\' has been deleted");
	}
	
}