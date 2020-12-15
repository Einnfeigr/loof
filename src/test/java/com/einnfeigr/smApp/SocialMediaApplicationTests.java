package com.einnfeigr.smApp;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.einnfeigr.taskApp.SocialMediaApplication;
import com.einnfeigr.taskApp.config.WebSecurityConfig;
import com.einnfeigr.taskApp.exception.controller.AccessException;
import com.einnfeigr.taskApp.exception.controller.UserNotFoundException;
import com.einnfeigr.taskApp.misc.Util;
import com.einnfeigr.taskApp.pojo.Code;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@Import(WebSecurityConfig.class)
@DisplayName("Social media application tests")
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(classes= {SocialMediaApplication.class})
public class SocialMediaApplicationTests {

	private static final String MOBILE_USER_AGENT = 
			"Mozilla/5.0 (Linux; Android 7.1; Mi A1 Build/N2G47H)";
	
	private final static Logger logger = 
			LoggerFactory.getLogger(SocialMediaApplicationTests.class);
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private List<Code> codes;
	
	@DisplayName("Test I | check /login page redirection")
	@Test
	public void test0unauthorizedCheck() throws Exception {
		mvc.perform(MockMvcRequestBuilders
				.get("/")
				.accept(MediaType.ALL))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection());
	}
	
	@DisplayName("Test II | create user")
	@Test
	public void test1createUser() throws Exception {
		loginWith(WebSecurityConfig.ADMIN_LOGIN, System.getenv("adminPassword"));
		codes = generateCodes(1);
		logout();
		String login = "vasya123";
		String name = "Вася";
		String password = "123";
		try {
			createUser(login, name, password, codes.get(0).getId());
		} finally {
			deleteUser(login);
		}
	}
	
	@DisplayName("Test III | cause 'code not found' error")
	@Test
	public void test2createUserError() throws Exception {
		String login = "vasya123";
		String name = "Вася";
		String password = "123";
		assertThrows(AccessException.class, () -> createUser(login, name, password, "123"));
	}
	
	@DisplayName("Test IV | create and edit user")
	@WithMockUser(value="admin")
	@Test
	public void test3addUserAndLogin() throws Exception {
		try {
			loginWith(WebSecurityConfig.ADMIN_LOGIN, System.getenv("adminPassword"));
			logger.info("Logged in succesfully");
			List<Code> codes = generateCodes(1);
			for(Code code : codes) {
				logger.info(code+"");
			}
			createUser("vasya123", "Вася", "123", codes.get(0).getId());
			logger.info("Created user succesfully");
			mvc.perform(MockMvcRequestBuilders
					.post("/users/vasya123")
					.param("fbLink", "facebook.com")
					.param("igLink", "instagram.com")
					.param("vkLink", "vk.com"))
			.andDo(MockMvcResultHandlers.log())
			.andExpect(MockMvcResultMatchers.status().isOk());
			logout();
			logger.info("Logged out succesfully");
			loginWith("vasya123", "123");
			logger.info("Logged in with new account succesfully");		
		} finally {
			deleteUser("vasya123");
		}
	}
	
	private void deleteUser(String login) throws Exception {
		try {
			mvc.perform(MockMvcRequestBuilders
					.post("/users/delete")
					.param("login", login))
			.andDo(MockMvcResultHandlers.log());
		} catch(UserNotFoundException e) {
			logger.error(Util.EXCEPTION_LOG_MESSAGE, e);
		}
	}
	
	private List<Code> generateCodes(int count) throws Exception {
		MvcResult result = mvc.perform(MockMvcRequestBuilders
				.post("/api/codes/generate/")
				.param("count", count+"")
				.accept("application/json")
				.accept(MediaType.ALL))
		.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();		
		String content = result.getResponse().getContentAsString();		
		TypeReference<List<Code>> mapType = new TypeReference<List<Code>>() {};
		List<Code> codes = objectMapper.readValue(content, mapType);
		return codes;
	}
	
	private void logout() throws Exception {
		mvc.perform(MockMvcRequestBuilders
				.get("/logout"))
		.andExpect(MockMvcResultMatchers.status().is3xxRedirection());
		
	}
	
	private void createUser(String login, String name, String password, String code) 
			throws Exception {
		mvc.perform(MockMvcRequestBuilders
				.post("/users/add/")
				.param("id", code)
				.param("name", name)
				.param("login", login)
				.param("password", password)
				.accept(MediaType.ALL))
		.andDo(MockMvcResultHandlers.log());
	}
	
	private void loginWith(String username, String password) throws Exception {
		mvc.perform(MockMvcRequestBuilders
				.post("/login")
				.param("username", username)
				.param("password", password)
				.accept(MediaType.ALL))
		.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
		.andDo(MockMvcResultHandlers.log());
	}
	
}