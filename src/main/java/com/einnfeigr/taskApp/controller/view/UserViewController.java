package com.einnfeigr.taskApp.controller.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.einnfeigr.taskApp.controller.rest.UserController;
import com.einnfeigr.taskApp.exception.controller.AccessException;
import com.einnfeigr.taskApp.misc.mav.ModelAndViewBuilder;
import com.einnfeigr.taskApp.pojo.User;

@RestController
@RequestMapping("/user")
public class UserViewController {
	
	private final static Logger logger = LoggerFactory.getLogger(
			UserViewController.class);
				
	@Autowired
	private UserController userController;
	
	@GetMapping("/{login}")
	public ModelAndView showUser(@PathVariable String login, Device device) 
			throws IOException, AccessException {
		User authUser = userController.getAuthUser();
		User user = userController.getUser(login);
		ModelAndViewBuilder mavBuilder = new ModelAndViewBuilder(device,
				authUser);
		String title = "Информация о пользователе "+user.getName();
		if(user.getId() == authUser.getId()) {
			mavBuilder.data("page.profile", true, "pageTitle", title);
		} else {
			mavBuilder.data("pageTitle", title);
		}
		return mavBuilder
				.title(user.getName())
				.user(user)
				.page()
					.path("user/info")
					.and()
				.build();
	}
	
	@GetMapping("/{login}/settings")
	public ModelAndView showUserSettings(@PathVariable String login,
			Device device) throws IOException, AccessException {
		Map<String, Object> data = new HashMap<>();
		User authUser = userController.getAuthUser();
		User user = userController.getUser(login);
		if(user.getId() != authUser.getId()) {
			throw new AccessException();
		}
		data.put("user", user);
		ModelAndViewBuilder mavBuilder = new ModelAndViewBuilder(device,
				authUser);		
		if(user.getId() == authUser.getId()) {
			mavBuilder.data("page.settings", true);
		}
		return mavBuilder
				.title("Настройки пользователя")
				.page()
					.title("Изменить пользовательскую информацию")
					.data(data)
					.path("user/settings")
					.and()
				.user(user)
				.build();
	}
	
	@GetMapping("/{login}/delete")
	public ModelAndView showDeleteUserForm(@PathVariable String login, 
			Device device) throws AccessException, IOException {
		User user = userController.getUser(login);
		User authUser = userController.getAuthUser();
		if(user.getId() != authUser.getId()) {
			throw new AccessException();
		}
		return new ModelAndViewBuilder(device, authUser)
				.user(user)
				.page()
					.title("Удаление пользователя "+user.getName())
					.path("user/delete")
					.and()
				.build();
	}
	
	@GetMapping("/add")
	public ModelAndView addUser(Device device) throws IOException,
			AccessException {
		User authUser = userController.getAuthUser();
		String title = "Добавить пользователя";
		return new ModelAndViewBuilder(device, authUser)
				.data("page.user.add", true)
				.page()
					.title(title)
					.path("user/add")
					.and()
				.build();
	}
}
