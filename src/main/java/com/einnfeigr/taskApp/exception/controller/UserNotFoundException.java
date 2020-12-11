package com.einnfeigr.taskApp.exception.controller;

public class UserNotFoundException extends NotFoundException {

	private static final long serialVersionUID = -5186253054207415306L;
	private static final String DEFAULT_MESSAGE = "Пользователь не найден";
	
	public UserNotFoundException() {
		super(DEFAULT_MESSAGE);
	}
	
	public UserNotFoundException(String message) {
		super(message);
	}
	
}
