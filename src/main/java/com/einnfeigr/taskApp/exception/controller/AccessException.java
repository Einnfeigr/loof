package com.einnfeigr.taskApp.exception.controller;

public class AccessException extends Exception {

	private static final long serialVersionUID = 7631653611526781129L;
	private static final String DEFAULT_MESSAGE="Доступ закрыт";
	
	public AccessException() {
		super(DEFAULT_MESSAGE);
	}
	
	public AccessException(String message) {
		super(message);
	}
	
}
