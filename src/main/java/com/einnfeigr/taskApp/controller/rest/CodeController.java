package com.einnfeigr.taskApp.controller.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.einnfeigr.taskApp.config.WebSecurityConfig;
import com.einnfeigr.taskApp.exception.controller.AccessException;
import com.einnfeigr.taskApp.pojo.Code;
import com.einnfeigr.taskApp.repository.IdRepository;

@RestController()
public class CodeController {

	@Autowired
	private IdRepository idRepo;
	
	@PostMapping("/api/codes/generate")
	public List<Code> generateCodes(@RequestParam int count) throws AccessException {
		if(!UserController.getAuthLogin().equals(WebSecurityConfig.ADMIN_LOGIN)) {
			throw new AccessException();
		}
		List<Code> ids = new ArrayList<>();
		count = count <= 0 ? 1 : count;
		Random random = new Random();
		for(int x = 0; x < count; x++) {
			Code id = new Code();
			id.setId(generateCode(random));
			ids.add(id);
		}
		idRepo.saveAll(ids);
		return ids;
	}
	
	@PostMapping("/api/codes//list")
	public List<Code> listCodes() {
		return idRepo.findAll();
	}
	
	@PostMapping("/api/codes//delete")
	public void delete(@RequestParam String id) throws AccessException {
		if(!UserController.getAuthLogin().equals(WebSecurityConfig.ADMIN_LOGIN)) {
			throw new AccessException();
		}
		deleteCode(id);
	}
	
	public void deleteCode(String id) {
		idRepo.delete(idRepo.findById(id));
	}
	
	public static String generateCode(Random random) {
		StringBuilder sb = new StringBuilder();
		for(char r = (char)random.nextInt(); sb.length() < 8; r = (char)random.nextInt()) {
			if(r >= 'A' && r <= 'Z' || r >= '0' && r <= '9') {
				sb.append(r);
			} 
		}
		return sb.toString();
	}

	public boolean isCorrect(String id) {
		for(Code code : listCodes()) {
			if(code.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}
	
}
