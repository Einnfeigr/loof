package com.einnfeigr.taskApp.exception.controller;

import java.io.IOException;

@SuppressWarnings("serial")
public class ControllerException extends IOException {
	
	private String path;
	
	public ControllerException() {
		super();
	}
	
	public ControllerException(Throwable t) {
		super(t);
	}
	
	public ControllerException(String message) {
		super(message);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
