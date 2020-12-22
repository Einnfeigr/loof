package com.einnfeigr.taskApp.controller.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.einnfeigr.taskApp.config.WebSecurityConfig;
import com.einnfeigr.taskApp.exception.controller.AccessException;
import com.einnfeigr.taskApp.misc.Util;
import com.einnfeigr.taskApp.pojo.Code;
import com.einnfeigr.taskApp.repository.IdRepository;

@RestController()
public class CodeController {

	@Autowired
	private IdRepository idRepo;
	
	@PostMapping("/api/codes/generate")
	public List<Code> generateCodes(@RequestParam int count) throws AccessException {
		if(!UserController.isAuthAdmin()) {
			throw new AccessException();
		}
		List<Code> ids = new ArrayList<>();
		count = count <= 0 ? 1 : count;
		for(int x = 0; x < count; x++) {
			Code id = new Code();
			id.setId(Util.generateCode(8));
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
		deleteCode(id);
	}
	
	public void deleteCode(String id) {
		idRepo.delete(idRepo.findById(id));
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
