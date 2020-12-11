package com.einnfeigr.smApp;

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
	
	@Test
	public void unauthorizedCheck() throws Exception {
		mvc.perform(MockMvcRequestBuilders
				.get("/")
				.accept(MediaType.ALL))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection());
	}
	
	@WithMockUser(value="admin")
	@Test
	public void addUserAndLogin() throws Exception {
		try {
			mvc.perform(MockMvcRequestBuilders
					.post("/login")
					.param("username", "admin")
					.param("password", System.getenv("adminPassword"))
					.accept(MediaType.ALL))
			.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
			.andDo(MockMvcResultHandlers.log());
			logger.info("Logged in succesfully");
			mvc.perform(MockMvcRequestBuilders
					.post("/users/add/")
					.param("id", "123")
					.param("name", "Вася")
					.param("password", "123")
					.param("login", "vasya123")
					.accept(MediaType.ALL))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andDo(MockMvcResultHandlers.log());
			logger.info("Created user succesfully");
			mvc.perform(MockMvcRequestBuilders
					.get("/logout"))
			.andExpect(MockMvcResultMatchers.status().is3xxRedirection());
			logger.info("Logged out succesfully");
			mvc.perform(MockMvcRequestBuilders
					.post("/login")
					.param("username", "vasya123")
					.param("password", "123")
					.accept(MediaType.ALL))
			.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
			.andDo(MockMvcResultHandlers.log());
			logger.info("Logged in with new account succesfully");		
		} finally {
			mvc.perform(MockMvcRequestBuilders
					.post("/users/delete")
					.param("login", "vasya123"))
			.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
			.andDo(MockMvcResultHandlers.log());
		}
	}
	
}