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
import com.einnfeigr.taskApp.pojo.Id;
import com.einnfeigr.taskApp.repository.IdRepository;

@RestController("/api/ids")
public class IdController {

	@Autowired
	private IdRepository idRepo;
	
	@PostMapping("/generate")
	public List<Id> generateIds(@RequestParam int count) throws AccessException {
		if(!UserController.getAuthLogin().equals(WebSecurityConfig.ADMIN_LOGIN)) {
			throw new AccessException();
		}
		List<Id> ids = new ArrayList<>();
		count = count <= 0 ? 1 : count;
		Random random = new Random();
		for(int x = 0; x < 1; x++) {
			Id id = new Id();
			id.setId(generateId(random));
			ids.add(id);
		}
		idRepo.saveAll(ids);
		return ids;
	}
	
	@PostMapping("/list")
	public List<Id> listIds() {
		return idRepo.findAll();
	}
	
	@PostMapping("/delete")
	public void delete(@RequestParam String id) throws AccessException {
		if(!UserController.getAuthLogin().equals(WebSecurityConfig.ADMIN_LOGIN)) {
			throw new AccessException();
		}
		deleteId(id);
	}
	
	public void deleteId(String id) {
		idRepo.delete(idRepo.findById(id));
	}
	
	public static String generateId(Random random) {
		StringBuilder sb = new StringBuilder();
		for(char r = (char)random.nextInt(); sb.length() < 8; r = (char)random.nextInt()) {
			if(r >= 'A' && r <= 'Z' || r >= '0' && r <= '9') {
				sb.append(r);
			} 
		}
		return sb.toString();
	}
	
}
