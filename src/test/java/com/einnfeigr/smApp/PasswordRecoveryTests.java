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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.einnfeigr.taskApp.SocialMediaApplication;
import com.einnfeigr.taskApp.config.WebSecurityConfig;
import com.einnfeigr.taskApp.misc.MailUtils;
import com.einnfeigr.taskApp.pojo.RecoveryCode;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@Import(WebSecurityConfig.class)
@DisplayName("Password recovery tests")
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(classes= {SocialMediaApplication.class})
public class PasswordRecoveryTests {

	private final static Logger log = LoggerFactory.getLogger(PasswordRecoveryTests.class);
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Autowired
	private MailUtils mailUtils;
	
	@Test
	@DisplayName("I. Generate code and change password")
	@WithMockUser(WebSecurityConfig.ADMIN_LOGIN)
	void generateCodeAndRecoveryPassword() throws Exception {
		MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/api/recovery/generate")
				.param("login", WebSecurityConfig.ADMIN_LOGIN))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		String content = result.getResponse().getContentAsString();		
		RecoveryCode code = mapper.readValue(content, RecoveryCode.class);
		String password = "12345";
		mvc.perform(MockMvcRequestBuilders.post("/api/recovery/consume")
				.param("code", code.getCode())
				.param("login", WebSecurityConfig.ADMIN_LOGIN)
				.param("password", password))
		.andExpect(MockMvcResultMatchers.status().isOk());
		mvc.perform(MockMvcRequestBuilders.post("/login")
				.param("login", WebSecurityConfig.ADMIN_LOGIN)
				.param("password", password))
		.andExpect(MockMvcResultMatchers.status().is3xxRedirection());
	}
	
	@Test
	@DisplayName("Send email")
	void sendMailTest() {
		mailUtils.sendMail("studiedlist@gmail.com", "test subject", "test text");
	}
	
	@Test
	@DisplayName("IIa. Try to generate code without logging in")
	void tryGenerateWithoutLogin() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/api/recovery/generate")
				.param("login", WebSecurityConfig.ADMIN_LOGIN))
		.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}
	
	@Test
	@DisplayName("IIb. Try to generate code with incorrect login")
	@WithMockUser(WebSecurityConfig.ADMIN_LOGIN)
	void tryGenerateWithIncorrect() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/api/recovery/generate")
				.param("login", "incorrectLogin"))
		.andExpect(MockMvcResultMatchers.status().is5xxServerError());
	}
	
	@Test
	@DisplayName("IIIa. Try to consume unexisting code")
	void tryConsumeUnexisting() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/api/recovery/consume")
				.param("code", "unexisting code")
				.param("login", WebSecurityConfig.ADMIN_LOGIN)
				.param("password", "123"));
	}
	
	@Test
	@DisplayName("IIIb. Try to consume code without logging in")
	@WithMockUser(WebSecurityConfig.ADMIN_LOGIN)
	void tryConsumeWithoutLoggingIn() throws Exception {
		MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/api/recovery/generate")
				.param("login", WebSecurityConfig.ADMIN_LOGIN))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		String content = result.getResponse().getContentAsString();		
		RecoveryCode code = mapper.readValue(content, RecoveryCode.class);
		mvc.perform(MockMvcRequestBuilders.get("/logout"))
			.andExpect(MockMvcResultMatchers.status().is3xxRedirection());
		mvc.perform(MockMvcRequestBuilders.get("/api/recovery/consume")
				.param("login", WebSecurityConfig.ADMIN_LOGIN)
				.param("code", code.getCode())
				.param("password", "newPassword"));
	}
	
}
