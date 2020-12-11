package com.einnfeigr.taskApp.exception.controller;

@SuppressWarnings("serial")
public class NotFoundException extends ControllerException {

	public NotFoundException() {
		super("Страница не найдена");
	}
	
	public NotFoundException(String message) {
		super(message);
	}

}
