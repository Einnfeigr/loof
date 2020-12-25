package com.einnfeigr.taskApp.controller.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.einnfeigr.taskApp.pojo.Code;
import com.einnfeigr.taskApp.pojo.Link;
import com.einnfeigr.taskApp.pojo.LinkType;
import com.einnfeigr.taskApp.pojo.RecoveryCode;
import com.einnfeigr.taskApp.pojo.User;

@RestController
public class ViewController {
	
	private final static Logger log = LoggerFactory.getLogger(ViewController.class);
	
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
	
	@GetMapping("/codes")
	public ModelAndView showCodesPage(Device device) throws AuthUserNotFoundException, IOException {
		return new ModelAndViewBuilder(device, userController.getAuthUser())
				.page()
					.title("Список кодов регистрации")
					.data("codes", codeController.listCodes())
					.path("/codes")
					.and()
				.data("pageName", "codes")
				.build();
	}
	
	@PostMapping("/codes")
	public ModelAndView updateCodes(@RequestParam List<String> ids, 
			@RequestParam List<String> nfcs) {
		for(int x = 0; x < ids.size(); x++) {
			codeController.update(ids.get(x), nfcs.get(x));
		}
		return new ModelAndView("redirect:/");
	}
	
	@GetMapping("/manage")
	public ModelAndView showManagePage(Device device)
			throws AuthUserNotFoundException, IOException {
		return new ModelAndViewBuilder(device, userController.getAuthUser())
				.page()
					.title("")
					.path("/manage")
					.and()
				.title("Управление")
				.build();
	}
	
	@GetMapping("/register")
	public ModelAndView showRegisterPage(Device device, @RequestParam(required=false) String code)
			throws AuthUserNotFoundException, IOException {
		Code regCode = codeController.get(code);
		Boolean isCorrect = code == null || !code.equals("") && codeController.isCorrect(code)
				&& regCode != null && regCode.getNfc() != null;
		if(regCode != null && regCode.getNfc() == null) { 
			log.warn("Code "+regCode.getId()+" don't have mapped nfc");
		}
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
		if(!codeController.isCorrect(""+id)) {
			throw new AccessException("Идентификатор не найден");
		}
		userController.addUser(id, login, password, name, email);
		return new ModelAndView("redirect:/login");
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
				.data("pageName", "users")
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
					.data("isMe", user != null, "user", user, 
						"links", user == null ? null : user.getLinks(),
						"isAdmin", user == null ? null : WebSecurityConfig.ADMIN_LOGIN
								.equals(user.getLogin()))
					.and()
				.data("excludeHeader", true, "pageName", user != null ? "userinfo" : "main")				
				.title("")
				.build();
	}

	@PostMapping("/settings")
	public ModelAndView applySettings(Device device, HttpServletRequest request) 
			throws AuthUserNotFoundException {
		Map<String, String[]> params = request.getParameterMap();
		User user = userController.getAuthUser();
		List<LinkType> linkTypes = linkController.getAllLinkTypes();
		List<String> linkNames = new ArrayList<>();
		linkTypes.forEach(t -> linkNames.add(t.getName()));
		for(Entry<String, String[]> param : params.entrySet()) {
			if(param.getValue() != null && linkNames.contains(param.getKey())) {
				Link link = new Link();
				LinkType type = linkController.getByName(param.getKey());
				type.setName(param.getKey());
				link.setLink(param.getValue()[0]);
				link.setType(type);
				link.setUser(user);
				user.addLink(link);
				log.info(link.getId()+"");
				user.getLinks().add(linkController.save(link));
			}
		}
		userController.save(user);
		return new ModelAndView("redirect:/");
	}
	
	@GetMapping("/settings")
	public ModelAndView showSettingsPage(Device device) 
			throws AuthUserNotFoundException, IOException {
		User user = userController.getAuthUser();
		return new ModelAndViewBuilder(device, userController.getAuthUser())
				.page()
					.title("")
					.path("user/settings")
					.data("user", user, "links", user.getLinks(linkController.getAllLinkTypes()))
					.and()
				.title("Настройки пользователя "+user.getName())
				.data("pageName", "usersettings")
				.build();
	}

	@GetMapping("/recovery")
	public ModelAndView showRecoveryGenerate(Device device) throws IOException {
		User user = userController.getAuthUser();
		return new ModelAndViewBuilder(device, user)
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
		if(user.getEmail() == null) {
			String error = "У пользователя не указана электронная почта";
			return new ModelAndView("redirect:/recovery?error="+error);
		}
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
	public ModelAndView viewUserInfo(Device device, @PathVariable String login) 
			  throws AuthUserNotFoundException, IOException, AccessException {
		User user = userController.get(login);
		return new ModelAndViewBuilder(device, userController.getAuthUser()) 
				.page()
					.title("")
					.path("/user/info")
					.data("links", user.getLinks())
					.and()
				.title(user.getName())
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
	
	@PostMapping("/delete")
	public ModelAndView delete() 
			throws AuthUserNotFoundException, UserNotFoundException, AccessException {
		userController.removeUser(userController.getAuthUser());
		return new ModelAndView("redirect:/logout");
	}
	
	@GetMapping("/delete")
	public ModelAndView showDelete(Device device) throws AuthUserNotFoundException, IOException {
		return new ModelAndViewBuilder(device, userController.getAuthUser())
				.page()
					.path("/user/delete")
					.data("isAdmin", UserController.isAuthAdmin())
					.and()
				.build();
	}
}
