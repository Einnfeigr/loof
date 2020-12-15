package com.einnfeigr.taskApp.controller.view;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.einnfeigr.taskApp.config.WebSecurityConfig;
import com.einnfeigr.taskApp.controller.rest.CodeController;
import com.einnfeigr.taskApp.controller.rest.UserController;
import com.einnfeigr.taskApp.exception.controller.AccessException;
import com.einnfeigr.taskApp.exception.controller.AuthUserNotFoundException;
import com.einnfeigr.taskApp.misc.mav.ModelAndViewBuilder;
import com.einnfeigr.taskApp.pojo.User;

@RestController
public class ViewController {
	
	@Autowired
	private UserController userController;

	@Autowired
	private CodeController codeController;
	
	@GetMapping("/register")
	public ModelAndView showRegisterPage(Device device, @RequestParam(required=false) String code)
			throws AuthUserNotFoundException, IOException {
		Boolean isCorrect = code == null || code.equals("") || codeController.isCorrect(code);
		return new ModelAndViewBuilder(device, userController.getAuthUser())
				.page()
					.path("templates/register")
					.data("id", isCorrect ? code : null,
							"error", isCorrect ? null : "Введенный код недействителен")
					.and()
				.build();
	}
	
	@GetMapping("/generate")
	public ModelAndView showGeneratePage(Device device) 
			throws AuthUserNotFoundException, IOException, AccessException {
		User user = userController.getAuthUser();
		if(user.getLogin() != WebSecurityConfig.ADMIN_LOGIN) {
			throw new AccessException("У вас нет возможности просматривать данную страницу");
		}
		return new ModelAndViewBuilder(device, userController.getAuthUser())
				.page()
					.path("templates/generate")
					.and()
				.build();
	}
	
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
					.path("templates/main")
					.and()
				.data("page.main", true, "excludeBack", true, "excludeHeader", true, 
						"pageName", "main")				
				.title("")
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