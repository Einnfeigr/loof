package com.einnfeigr.smApp;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.einnfeigr.taskApp.exception.controller.UserNotFoundException;
import com.einnfeigr.taskApp.pojo.Code;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Util {

	private static final String MOBILE_USER_AGENT = 
			"Mozilla/5.0 (Linux; Android 7.1; Mi A1 Build/N2G47H)";
	
	public MockMvc mvc;
	public final Logger log;
	public ObjectMapper mapper;
	
	public Util(MockMvc mvc, Logger log, ObjectMapper mapper) {
		this.mvc = mvc;
		this.log = log;
		this.mapper = mapper;
	}
	
	public void deleteUser(String login) throws Exception {
		try {
			mvc.perform(MockMvcRequestBuilders
					.post("/users/delete")
					.param("login", login))
			.andDo(MockMvcResultHandlers.log());
		} catch(UserNotFoundException e) {
			log.error("User cannot be found", e);
		}
	}
	
	public List<Code> generateCodes(int count) throws Exception {
		MvcResult result = mvc.perform(MockMvcRequestBuilders
				.post("/api/codes/generate/")
				.param("count", count+"")
				.accept("application/json")
				.accept(MediaType.ALL))
		.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();		
		String content = result.getResponse().getContentAsString();		
		TypeReference<List<Code>> mapType = new TypeReference<List<Code>>() {};
		List<Code> codes = mapper.readValue(content, mapType);
		return codes;
	}
	
	public void logout() throws Exception {
		mvc.perform(MockMvcRequestBuilders
				.get("/logout"))
		.andExpect(MockMvcResultMatchers.status().is3xxRedirection());
		
	}
	
	public void createUser(String login, String name, String password, String code) 
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
	
	public void loginWith(String username, String password) throws Exception {
		mvc.perform(MockMvcRequestBuilders
				.post("/login")
				.param("username", username)
				.param("password", password)
				.accept(MediaType.ALL))
		.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
		.andDo(MockMvcResultHandlers.log());
	}
	
}
