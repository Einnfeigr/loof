package com.einnfeigr.smApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.einnfeigr.taskApp.controller.rest.CodeController;

public class IdTests {

	private final static Logger log = LoggerFactory.getLogger(IdTests.class);
	
	@Test
	void generateIds() {
		Random random = new Random();
		List<String> ids = new ArrayList<>();
		for(int x = 0; x < 150; x++) {
			ids.add(CodeController.generateCode(random));
		}
		for(String id : ids) {
			log.info(id);
		}
	}
	
}
