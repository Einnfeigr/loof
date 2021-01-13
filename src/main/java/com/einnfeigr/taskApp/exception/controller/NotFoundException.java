package com.einnfeigr.taskApp.exception.controller;

@SuppressWarnings("serial")
public class NotFoundException extends ControllerException {

	public NotFoundException() {
		super("Сторінка не знайдена");
	}
	
	public NotFoundException(String message) {
		super(message);
	}

}
