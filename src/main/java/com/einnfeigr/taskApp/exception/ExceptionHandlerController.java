package com.einnfeigr.taskApp.exception;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.einnfeigr.taskApp.controller.rest.UserController;
import com.einnfeigr.taskApp.controller.view.ViewController;
import com.einnfeigr.taskApp.exception.controller.AccessException;
import com.einnfeigr.taskApp.exception.controller.AuthUserNotFoundException;
import com.einnfeigr.taskApp.exception.controller.NotFoundException;
import com.einnfeigr.taskApp.exception.controller.UserNotFoundException;
import com.einnfeigr.taskApp.misc.Util;
import com.einnfeigr.taskApp.misc.mav.ModelAndViewBuilder;
import com.einnfeigr.taskApp.pojo.User;
import com.einnfeigr.taskApp.template.TemplateFactory;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {
	
	private final static Logger logger = 
			LoggerFactory.getLogger(ViewController.class);
	
	@Autowired
	UserController userController;
	
	@ExceptionHandler(value= {Exception.class})
	public ModelAndView handleConflict(HttpServletRequest request, 
			HttpServletResponse response, Exception e, Device device)
			throws IOException, ServletException {
		logger.info("Handler resolved exception", e);
		String textPath;
		String title = "Ошибка";
		if(e instanceof NotFoundException && !(e instanceof UserNotFoundException)) {
			textPath = "templates/text/ru/notFound";
			response.setStatus(404);
		} else if(e instanceof AccessException) {
			textPath = "templates/text/ru/error";
			response.setStatus(400);
		} else {
			textPath = "templates/text/ru/error";
			response.setStatus(503);
		}
		if(e instanceof AuthUserNotFoundException) {
			request.logout();
		}
		try {
			response.setHeader("error_message", URLEncoder.encode(
					e.getLocalizedMessage(), "UTF-8"));
		} catch(Exception ex) {
			logger.error(Util.EXCEPTION_LOG_MESSAGE, ex);
		}
		User authUser = userController.getAuthUser();
		try {
			return new ModelAndViewBuilder(device, authUser)
					.title(title)
					.page()
						.path("templates/error")
						.data("text", TemplateFactory.buildTemplate(textPath,
							"message", e.getMessage()))
						.and()
					.build();
		} catch(IOException | NullPointerException ex) {
			logger.info("Handler caught an exception while running: ", ex);
			return new ModelAndViewBuilder(device, authUser)
					.page()
						.path("templates/error")
						.and()
					.build();
		}
	}

}