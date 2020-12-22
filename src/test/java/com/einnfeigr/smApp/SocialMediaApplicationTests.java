package com.einnfeigr.smApp;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.einnfeigr.taskApp.SocialMediaApplication;
import com.einnfeigr.taskApp.config.WebSecurityConfig;
import com.einnfeigr.taskApp.exception.controller.AccessException;
import com.einnfeigr.taskApp.pojo.Code;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@Import(WebSecurityConfig.class)
@DisplayName("Social media application tests")
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(classes= {SocialMediaApplication.class})
public class SocialMediaApplicationTests {

	private final static Logger log = LoggerFactory.getLogger(SocialMediaApplicationTests.class);
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private Util util;
	private List<Code> codes;
	
	@BeforeEach
	void init() {
		util = new Util(mvc, log, objectMapper);
	}
	
	@DisplayName("Test I | check main page avialability")
	@Test
	void test0mainAvialability() throws Exception {
		mvc.perform(MockMvcRequestBuilders
				.get("/")
				.accept(MediaType.ALL))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@DisplayName("Test II | create user")
	@WithMockUser(WebSecurityConfig.ADMIN_LOGIN)
	@Test
	void test1createUser() throws Exception {
		codes = util.generateCodes(1);
		util.logout();
		String login = "vasya123";
		String name = "Вася";
		String password = "123";
		try {
			util.createUser(login, name, password, codes.get(0).getId());
		} finally {
			util.deleteUser(login);
		}
	}
	
	@DisplayName("Test III | cause 'code not found' error")
	@WithMockUser(WebSecurityConfig.ADMIN_LOGIN)
	@Test
	void test2createUserError() throws Exception {
		String login = "vasya123";
		String name = "Вася";
		String password = "123";
		util.createUser(login, name, password, "123");
	}
	
	@DisplayName("Test IV | create and edit user")
	@WithMockUser(WebSecurityConfig.ADMIN_LOGIN)
	@Test
	void test3addUserAndLogin() throws Exception {
		try {
			List<Code> codes = util.generateCodes(1);
			for(Code code : codes) {
				log.info(code+"");
			}
			util.createUser("vasya123", "Вася", "123", codes.get(0).getId());
			log.info("Created user succesfully");
			mvc.perform(MockMvcRequestBuilders
					.post("/users/vasya123"))
			.andDo(MockMvcResultHandlers.log())
			.andExpect(MockMvcResultMatchers.status().isOk());
			util.logout();
			log.info("Logged out succesfully");
			util.loginWith("vasya123", "123");
			log.info("Logged in with new account succesfully");		
		} finally {
			util.deleteUser("vasya123");
		}
	}
		
}