package com.einnfeigr.taskApp.controller.view;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.einnfeigr.taskApp.controller.rest.UserController;
import com.einnfeigr.taskApp.exception.controller.ControllerException;
import com.einnfeigr.taskApp.misc.Util;
import com.einnfeigr.taskApp.misc.mav.ModelAndViewBuilder;

@Component
@RestController
public class SecurityController {
	
	private final static Logger logger = 
			LoggerFactory.getLogger(SecurityController.class);
		
	@Autowired
	private UserController userController;
	
	@GetMapping(value="/login")
	public ModelAndView login(@RequestParam(required=false) String error, 
			Device device) throws ControllerException {
		if(userController.getAuthUser() != null) {
			return new ModelAndView("redirect:/");
		}
		try {
			Map<String, Object> data = new HashMap<>();
			if(error != null) {
				data.put("isError", "true");
				data.put("error", error.equals("true") ? "Невірний логін або пароль" : error);
			}
			return new ModelAndViewBuilder(device, null)
					.title("Увійти")
					.page()
						.title("")
						.path("templates/login")
						.data(data)
						.and()
					.data("pageName", "login")
					.build();
		} catch(Exception e) {
			logger.error(Util.EXCEPTION_LOG_MESSAGE, e);
			throw new ControllerException(e);
		}
	}	
	
}

