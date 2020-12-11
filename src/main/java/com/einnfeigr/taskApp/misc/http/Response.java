package com.einnfeigr.taskApp.misc.http;

public class Response {

	private String responseMessage;
	private String content;
	private int code;
	
	Response(String responseMessage, String content, int code) {
		this.responseMessage = responseMessage;
		this.content = content;
		this.code = code;
	}
	
	public String getResponseMessage() {
		return responseMessage;
	}
	public String getContent() {
		return content;
	}
	public int getCode() {
		return code;
	}
	
}
