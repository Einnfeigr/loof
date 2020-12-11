package com.einnfeigr.taskApp.controller.view;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.einnfeigr.taskApp.controller.rest.UserController;
import com.einnfeigr.taskApp.exception.controller.AccessException;
import com.einnfeigr.taskApp.misc.mav.ModelAndViewBuilder;
import com.einnfeigr.taskApp.pojo.User;

@RestController
public class ViewController {
	
	@Autowired
	private UserController userController;

	@GetMapping("/users")
	public ModelAndView showUsers(Device device) throws IOException, 
			AccessException {
		List<User> users = userController.getAll();
		return new ModelAndViewBuilder(device, userController.getAuthUser())
				.title("Список пользователей")
				.page()
					.path("templates/users")
					.data("users", users)
				.and()
				.data("page.users", true)
				.build();
	}

	@GetMapping("/")
	public ModelAndView showMain(Device device,
			@RequestParam(value="date", required=false) String dateText) 
					throws IOException {
		return new ModelAndViewBuilder(device, userController.getAuthUser()) 
				.page()
					.title("Главная")
					.path("templates/main")
					.and()
				.data("page.main", true, "excludeBack", true)
				.build();
	}

	@GetMapping("/about")
	public ModelAndView showAbout(Device device) throws IOException {
		return new ModelAndViewBuilder(device, userController.getAuthUser())
				.page()
					.title("Справка")
					.path("about")
					.and()
				.data("page.about", true, "excludeBack", true)
				.build();
	}

}