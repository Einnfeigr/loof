package com.einnfeigr.taskApp.controller.view;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
	
	public final static String RECOVERY_NO_MAIL_ERROR = "У пользователя не указан почтовый ящик";
	public final static String RECOVERY_NO_USER_ERROR = "Пользователь \'%s\' не найден";
	
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
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@GetMapping("/codes")
	public ModelAndView showCodesPage(Device device) 
			throws AuthUserNotFoundException, IOException, AccessException {
		User auth = userController.getAuthUser();
		log.info(auth.getLogin()+" requested codes list");
		if(!auth.getLogin().equals(WebSecurityConfig.ADMIN_LOGIN)) {
			throw new AccessException();
		}
		return new ModelAndViewBuilder(device, auth)
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
			@RequestParam List<String> nfcs) throws AuthUserNotFoundException, AccessException {
		User auth = userController.getAuthUser();
		if(!auth.getLogin().equals(WebSecurityConfig.ADMIN_LOGIN)) {
			log.info(auth.getLogin()+" tried to update codes");
			throw new AccessException();
		}
		log.info(auth.getLogin()+" updated codes");
		for(int x = 0; x < ids.size(); x++) {
			codeController.update(ids.get(x), nfcs.get(x));
		}
		return new ModelAndView("redirect:/codes");
	}
	
	@GetMapping("/manage")
	public ModelAndView showManagePage(Device device)
			throws AuthUserNotFoundException, IOException, AccessException {
		User auth = userController.getAuthUser();
		if(!auth.getLogin().equals(WebSecurityConfig.ADMIN_LOGIN)) {
			log.info(auth.getLogin()+" tried to get manage page");
			throw new AccessException();
		}
		return new ModelAndViewBuilder(device, auth)
				.page()
					.title("")
					.path("/manage")
					.and()
				.title("Управление")
				.data("pageName", "manage")
				.build();
	}
	
	@GetMapping("/register")
	public ModelAndView showRegisterPage(Device device, @RequestParam(required=false) String code,
			@RequestParam(required=false) String error)
			throws AuthUserNotFoundException, IOException {
		Code regCode = codeController.get(code);
		Boolean isCorrect = code != null && !code.equals("") && codeController.isCorrect(code);
		if(regCode != null) {
			if(regCode.getNfc() == null) { 
				log.warn("Code \'"+regCode.getId()+"\' don't have mapped nfc");
			}
			if(regCode.getUser() != null) {
				log.warn("Someone requested registration on \'"+regCode.getId()
					+"\' which is already mapped on user with login\'"
						+regCode.getUser().getLogin()+"\'");
			}
		}
		String invalidMessage = "Введенный код недействителен";
		String claimedMessage = "На этот %s уже зарегистрирован аккаунт";
		String message = !isCorrect ? invalidMessage : null;
		error = error == null ? "" : error;
		switch(error) {
			case("empty"):
				message = "Вам нужно заполнить все поля формы";
				break;
			case("invalid"):
				message = invalidMessage;
				break;
			case("login"):
				message = String.format(claimedMessage, "логин");
				break;
			case("email"):
				message = String.format(claimedMessage, "почтовый ящик");
				break;
		}
		return new ModelAndViewBuilder(device, userController.getAuthUser())
				.page()
					.path("templates/register")
					.data("id", isCorrect ? code : null,
							"error", code == null ? null : message
								)
					.and()
				.build();
	}
	
	@PostMapping("/register")
	public ModelAndView register(@RequestParam(required=false) String id, 
			@RequestParam(required=false) String name,
			@RequestParam(required=false) String login,
			@RequestParam(required=false) String password,
			@RequestParam(required=false) String email,
			HttpServletRequest request)
					throws AccessException, UserNotFoundException, ServletException {
		name = name.trim();
		login = login.trim().toLowerCase();
		password = password.trim();
		email = email.trim().toLowerCase();
		if(id == null || "".equals(id) || name == null || "".equals(name) 
				|| login == null || "".equals(login) || password == null || "".equals(password) 
				|| email == null || "".equals(email)) {
			return new ModelAndView("redirect:/register?code="+id+"&error=empty");
		}
		if(!codeController.isCorrect(""+id)) {
			return new ModelAndView("redirect:/register?code="+id+"&error=invalid");
		}
		try {
			if(userController.get(login) != null) {
				return new ModelAndView("redirect:/register?code="+id+"&error=login");
			}
		} catch(UserNotFoundException e) {
			
		}
		try {
			if(userController.get(email) != null) {
				return new ModelAndView("redirect:/register?code="+id+"&error=email");				
			}
		} catch(UserNotFoundException e) {
			
		}
		userController.addUser(id, login, password, name, email, request);
		log.info("User has registered with login \'"+login+"\' and code \'"+id+"\'");
		return new ModelAndView("redirect:/login");
	}	
	
	@GetMapping("/users")
	public ModelAndView showUsers(Device device) throws IOException, 
			AccessException {
		User auth = userController.getAuthUser();
		log.info(auth.getLogin()+" requested users list");
		if(!auth.getLogin().equals(WebSecurityConfig.ADMIN_LOGIN)) {
			throw new AccessException();
		}
		List<User> users = userController.getAll();
		return new ModelAndViewBuilder(device, auth)
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
		if(user != null) {
			return new ModelAndView("redirect:/u/"+user.getLogin());
		}
		return new ModelAndViewBuilder(device, user) 
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

	@PostMapping("/settings/details")
	public ModelAndView applyDetailsSettings(@RequestParam String name, 
			@RequestParam String login,
			@RequestParam(required=false) String email,
			@RequestParam(required=false) String password) 
					throws UserNotFoundException, AccessException {
		User user = userController.getAuthUser();
		if(login != null) {
			try {
				if(!user.getLogin().equals(login) && userController.get(login) != null) {
					return new ModelAndView("redirect:/settings?error=login");
				}
			} catch(UserNotFoundException e) {
				
			}
			user.setLogin(login);
		}
		if(name != null) {
			user.setName(name);
		}
		if(email != null) {
			try {
				if(!email.equals(user.getEmail()) && userController.get(email) != null) {
					return new ModelAndView("redirect:/settings?error=mail");
				}
			} catch(UserNotFoundException e) {
				
			}
		}
		user.setEmail(email);
		if(password != null && !"".equals(password)) {
			user.setPassword(encoder.encode(password));
		}
		userController.save(user);
		return new ModelAndView("redirect:/logout");
	}
	
	@PostMapping("/settings")
	public ModelAndView applySettings(HttpServletRequest request) 
			throws AuthUserNotFoundException {
		Map<String, String[]> params = request.getParameterMap();
		User user = userController.getAuthUser();
		List<String> linkNames = linkController.getNames();
		try {
			for(Entry<String, String[]> param : params.entrySet()) {
				if(param.getValue() != null && linkNames.contains(param.getKey())) {
					LinkType type = linkController.getTypeByName(param.getKey());
					for(String url : param.getValue()) {
						generateLink(user, type, url);
					}
				}
			}
		} catch(IllegalArgumentException e) {
			return new ModelAndView("settings:/settings?error=true&type="+e.getMessage());
		}
		return new ModelAndView("redirect:/");
	}
	
	private void generateLink(User user, LinkType type, String url) {
		if(url == null || url.equals("")) {
			return;
		}
		String typeName = type.getName().toLowerCase();
		url = url.replace("http://", "").replace("https://", "");
		String domain = type.getDomain();
		if(domain != null) {
			domain = domain.contains(".") || domain.startsWith("/") ? domain += "/" : domain;
			url = url.contains(type.getDomain()) ? url : domain + url;
		}
		if(!typeName.equals("email") && !typeName.equals("mobile") && !typeName.equals("location")
				&& !typeName.equals("card")
				&& !url.startsWith("@") && url.contains(".") && url.contains("/") 
				&& !domain.startsWith("/")) {
			url ="https://"+url;
		}
		if(typeName.equals("location")) {
			url = String.format("http://maps.google.com/?ie=UTF8&hq=&ll=%s&z=13",
					url);
		}
		if(user.hasLinkUrl(url)) {
			return;
		}
		if(!typeName.equals("custom") && !typeName.equals("mobile") && !typeName.equals("card")
				&& user.hasLink(type)) {
			for(Link uLink : user.getLinks(type)) {
				linkController.delete(uLink);
			}
		}
		if(linkController.get(url) != null) {
			throw new IllegalArgumentException(typeName);
		}
		Link link = new Link();
		link.setLink(url);
		link.setType(type);
		link.setUser(user);
		link.setTitle(type.getName());
		user.addLink(link);
		user.getLinks().add(linkController.save(link));
	}
	
	@PostMapping("/settings/delete")
	public ModelAndView deleteLinks(HttpServletRequest request) throws AuthUserNotFoundException {
		Map<String, String[]> params = request.getParameterMap();
		User user = userController.getAuthUser();
		for(Entry<String, String[]> param : params.entrySet()) {
			if(param.getValue() != null && user.hasLinkType(param.getKey())) {
				for(Link link : user.getLinks(linkController.getTypeByName(param.getKey()))) {
					for(String linkName : param.getValue()) {
						if(link.getLink().equals(linkName)) {
							linkController.delete(link);							
						}
					}
				}
			}
		}
		return new ModelAndView("redirect:../");
	}
	
	@GetMapping("/settings")
	public ModelAndView showSettingsPage(Device device, @RequestParam(required=false) String error,
			@RequestParam(required=false) String type) 
			throws AuthUserNotFoundException, IOException {
		User user = userController.getAuthUser();
		String errorMessage = null;
		error = error == null ? "" : error;
		switch(error) {
			case("login"):
				errorMessage = "Этот логин занят";
				break;
			case("email"):
				errorMessage = "Эта электронная почта уже привязана к другому аккаунту";
				break;
			case("true"):
				errorMessage = type == null ? "" : "Значение поля \'"+type+"\' уже привязано к "
						+ "одному из аккаунтов";
		}
		return new ModelAndViewBuilder(device, user)
				.page()
					.title("")
					.path("user/settings")
					.data("user", user, "links", user.getLinks(linkController.getAllLinkTypes()),
							"error", errorMessage)
					.and()
				.title("Настройки пользователя "+user.getName())
				.data("pageName", "usersettings")
				.build();
	}

	@GetMapping("/recovery")
	public ModelAndView showRecoveryGenerate(Device device, 
			@RequestParam(required=false) Boolean error,
			@RequestParam(required=false) String login) throws IOException {
		User user = userController.getAuthUser();
		return new ModelAndViewBuilder(device, user)
				.page()
					.title("")
					.path("recovery/generate")
					.data("error", error != null ? login == null ? 
							RECOVERY_NO_MAIL_ERROR : String.format(RECOVERY_NO_USER_ERROR, login)
							: null)
					.and()
				.title("Восстановить пароль")
				.data("pageName", "recoveryGenerate")
				.build();
	}
	
	@PostMapping("/recovery")
	public ModelAndView generateRecoveryCode(Device device, @RequestParam String email) 
			throws UserNotFoundException, AccessException {
		User user;
		try {
			user = userController.get(email);			
		} catch(UserNotFoundException e) {
			log.warn("Recovery code has been requested for invalid user \'"+email+"\'");
			return new ModelAndView("redirect:/recovery?error=true&login="+email);			
		}
		if(user.getEmail() == null) {
			log.warn("Recovery code has been requested for user \'"
					+user.getLogin()+"\' with no mail");
			return new ModelAndView("redirect:/recovery?error=true");
		}
		log.info("Recovery code has been requested for user \'"+user.getLogin()+"\'");
		RecoveryCode recoveryCode = recoveryController.generate(user.getLogin());
		mailUtils.sendMail(user.getEmail(), "Восстановление пароля", 
				  "На ваш аккаунт поступил запрос на восстановление пароля. \n"
				+ "Ссылка для восстановления пароля: "
						  +"https://loofme.site/recovery/"+recoveryCode.getCode()+"\n"
				+ "Ни в коем случае не передавайте эту ссылку третьим лицам,"
				+ " это грозит утратой аккаунта \n"
				+ "Если вы не запрашивали восстановление пароля- игнорируйте это сообщение");
		return new ModelAndView("redirect:/recovery/message");
	}
	
	@GetMapping("/recovery/message")
	public ModelAndView showRecoveryMessage(Device device) 
			throws AuthUserNotFoundException, IOException {
		return new ModelAndViewBuilder(device, userController.getAuthUser())
				.page()
					.title("")
					.path("/recovery/message")
					.and()
				.build();
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
		log.info("Recovery code \'"+code+"\' has been consumed");
		return new ModelAndView("redirect:/login");
	}
	
	@GetMapping("/u/{login}")
	public ModelAndView viewUserInfoByLogin(Device device, @PathVariable String login) 
			  throws AuthUserNotFoundException, IOException, AccessException {
		return showUserInfo(device, userController.get(login));
	}
	
	@GetMapping("/user/{code}")
	public ModelAndView viewUserInfoById(Device device, @PathVariable String code) 
			throws AuthUserNotFoundException, IOException, AccessException {
		return showUserInfo(device, userController.getByCode(code));
	}
	
	private ModelAndView showUserInfo(Device device, User user) 
			throws AuthUserNotFoundException, IOException {
		User auth = userController.getAuthUser();
		if(user == null) {
			return new ModelAndViewBuilder(device, auth)
					.page()
						.title("")
						.path("/user/notfound")
						.and()
					.title("Пользователь не найден")
					.build();
		}
		return new ModelAndViewBuilder(device, auth) 
				.page()
					.title("")
					.path("/user/info")
					.data("links", user.getLinks(), "user", user, 
							"isAdmin", UserController.isAuthAdmin(),
							"isMe", auth == null ? false : user.getLogin().equals(auth.getLogin()))
					.and()
				.title(user.getName())
				.data("pageName", "userinfo")
				.build();
	}
	
	@PostMapping("/codes/delete")
	public ModelAndView deleteCodes(@RequestParam List<String> codes) {
		for(String id : codes) {
			Code code = codeController.get(id);
			if(code.getUser() != null) {
				continue;
			}
			codeController.delete(code);
		}
		return new ModelAndView("redirect:/codes/");
	}
	
	@GetMapping("/codes/generate")
	public ModelAndView showGenerateIdPage(Device device) 
			throws AuthUserNotFoundException, IOException, AccessException {
		User auth = userController.getAuthUser();
		if(!auth.getLogin().equals(WebSecurityConfig.ADMIN_LOGIN)) {
			log.info(auth.getLogin()+" tried to get manage page");
			throw new AccessException();
		}
		return new ModelAndViewBuilder(device, userController.getAuthUser())
				.page()
					.title("")
					.path("generateId")
					.and()
				.build();
	}
	
	@PostMapping("/codes/generate")
	public ModelAndView generateId(Device device, @RequestParam(required=false) Integer count) 
			throws AccessException, IOException {
		User auth = userController.getAuthUser();
		if(!auth.getLogin().equals(WebSecurityConfig.ADMIN_LOGIN)) {
			log.info(auth.getLogin()+" tried to get manage page");
			throw new AccessException();
		}
		codeController.generateCodes(count);
		log.info(count+" ids has been generated by user \'"+UserController.getAuthLogin()+"\'");
		return showCodesPage(device);
	}
	
	@PostMapping("/delete")
	public ModelAndView delete() 
			throws AuthUserNotFoundException, UserNotFoundException, AccessException {
		User auth = userController.getAuthUser();
		if(auth.getLogin().equals(WebSecurityConfig.ADMIN_LOGIN)) {
			throw new AccessException("Аккаунт админа не может быть удален");
		}
		log.info("User \'"+auth.getLogin()+"\' is deleting account");
		userController.removeUser(auth);
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
