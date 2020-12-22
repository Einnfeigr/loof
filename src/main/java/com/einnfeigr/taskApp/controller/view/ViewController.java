package com.einnfeigr.taskApp.controller.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.einnfeigr.taskApp.config.WebSecurityConfig;
import com.einnfeigr.taskApp.controller.rest.CodeController;
import com.einnfeigr.taskApp.controller.rest.LinkController;
import com.einnfeigr.taskApp.controller.rest.RecoveryController;
import com.einnfeigr.taskApp.controller.rest.UserController;
import com.einnfeigr.taskApp.exception.controller.AccessException;
import com.einnfeigr.taskApp.exception.controller.AuthUserNotFoundException;
import com.einnfeigr.taskApp.exception.controller.UserNotFoundException;
import com.einnfeigr.taskApp.misc.MailUtils;
import com.einnfeigr.taskApp.misc.mav.ModelAndViewBuilder;
import com.einnfeigr.taskApp.pojo.LinkType;
import com.einnfeigr.taskApp.pojo.RecoveryCode;
import com.einnfeigr.taskApp.pojo.User;

@RestController
public class ViewController {
	
	@Autowired
	private UserController userController;

	@Autowired
	private CodeController codeController;
	
	@Autowired
	private RecoveryController recoveryController;
	
	@Autowired
	private LinkController linkController;
	
	@Autowired
	private MailUtils mailUtils;
	
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
	
	@PostMapping("/register")
	public ModelAndView register(@RequestParam String id, 
			@RequestParam String name,
			@RequestParam String login,
			@RequestParam String password,
			@RequestParam String email) throws AuthUserNotFoundException, AccessException {
		if(!codeController.isCorrect(id)) {
			throw new AccessException("Идентификатор не найден");
		}
		User user = null;
		try {
			user = userController.addUser(id, login, password, name, email);
		} finally {
			if(user != null) {
				codeController.delete(id);
			}
		}
		return new ModelAndView("redirect:/");
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
		
		User user = userController.getAuthUser();
		return new ModelAndViewBuilder(device, userController.getAuthUser()) 
				.page()
					.path("templates/main")
					.data("isMe", user != null, "user", user)
					.and()
				.data("page.main", true, "excludeBack", true, "excludeHeader", true, 
						"pageName", user != null ? "userinfo" : "main")				
				.title("")
				.build();
	}

	@GetMapping("/settings")
	public ModelAndView showSettingsPage(Device device) 
			throws AuthUserNotFoundException, IOException {
		User user = userController.getAuthUser();
		List<LinkType> linkList = linkController.getAllLinkTypes();
		return new ModelAndViewBuilder(device, userController.getAuthUser())
				.page()
					.title("")
					.path("user/settings")
					.data("user", user, "links", linkList)
					.and()
				.title("Настройки пользователя "+user.getName())
				.data("pageName", "usersettings")
				.build();
	}

	@GetMapping("/recovery")
	public ModelAndView showRecoveryGenerate(Device device) throws IOException {
		return new ModelAndViewBuilder(device, userController.getAuthUser())
				.page()
					.title("")
					.path("recovery/generate")
					.and()
				.title("Восстановить пароль")
				.data("pageName", "recoveryGenerate")
				.build();
	}
	
	@PostMapping("/recovery")
	public ModelAndView generateRecoveryCode(Device device, @RequestParam String email) 
			throws UserNotFoundException, AccessException {
		User user = userController.get(email);
		RecoveryCode recoveryCode = recoveryController.generate(user.getLogin());
		mailUtils.sendMail(user.getEmail(), "Восстановление пароля", 
				  "На ваш аккаунт поступил запрос на восстановление пароля. \n"
				+ "Ссылка для восстановления пароля: "
						  +"https://loof.com/recovery/"+recoveryCode.getCode()+"\n"
				+ "Если вы не запрашивали восстановление пароля- игнорируйте это сообщение");
		return new ModelAndView("redirect:/");
	}
	
	@GetMapping("/recovery/{code}")
	public ModelAndView showRecoverByCodePage(Device device, @PathVariable String code) 
			throws AuthUserNotFoundException, IOException {
		return new ModelAndViewBuilder(device, userController.getAuthUser())
				.page()
					.title("")
					.path("recovery/consume")
					.and()
				.data("pageName", "consume")
				.build();
	}
	
	@PostMapping("/recovery/{code}")
	public ModelAndView recoverByCode(@PathVariable String code, @RequestParam String password) 
			throws UserNotFoundException, AccessException {
		recoveryController.consume(code, password);
		return new ModelAndView("redirect:/login");
	}
	
	@GetMapping("/users/{login}")
	public ModelAndView viewUserInfo(Device device, @RequestParam String login) 
			  throws AuthUserNotFoundException, IOException {
		User user = userController.getAuthUser();
		return new ModelAndViewBuilder(device, user) 
				.page()
					.title("")
					.path("/user/info")
					.and()
				.data("isMe", user.getLogin().equals(login))
				.build();
	}
	
	@GetMapping("/id/generate")
	public ModelAndView showGenerateIdPage(Device device) 
			throws AuthUserNotFoundException, IOException {
		return new ModelAndViewBuilder(device, userController.getAuthUser())
				.page()
					.title("")
					.path("generateId")
					.and()
				.build();
	}
	
	@PostMapping("/id/generate")
	public ModelAndView generateId(@RequestParam(required=false) Integer count) 
			throws AccessException {
		codeController.generateCodes(count);
		return new ModelAndView("redirect:/id/generate");
	}
}
