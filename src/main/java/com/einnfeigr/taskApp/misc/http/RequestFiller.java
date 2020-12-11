package com.einnfeigr.taskApp.misc.http;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMethod;

public class RequestFiller {
	
	private Request request;
	
	RequestFiller(Request request) {
		this.request = request;
	}
	
	public RequestFiller method(String method) {
		if(!isMethod(method)) {
			throw new IllegalArgumentException("Given value is not a method");
		}
		request.setMethod(method);
		return this;
	}
	
	private boolean isMethod(String text) {
		boolean matches = false;
		for(RequestMethod requestMethod : RequestMethod.values()) {
			if(requestMethod.name().equals(text)) {
				matches = true;
			}
		}
		return matches;
	}
	
	public RequestFiller address(String address) {
		if(!isValidAddress(address)) {
			throw new IllegalArgumentException("Given address is not valid");
		}
		request.setAddress(address);
		return this;
	}

	public boolean isValidAddress(String address) {
		return address.contains("http://") || address.contains("https://") 
				&& address.contains("/");
	}
	
	public RequestFiller content(String content) {
		if(request.getParams() != null && !request.getMethod().equals("GET")) {
			throw new IllegalStateException(
					"Cannot set both content and params");
		}
		request.setContent(content);
		return this;
	}

	public RequestFiller contentType(String contentType) {
		if(request.getParams() != null && !request.getMethod().equals("GET")) {
			throw new IllegalStateException(
					"Cannot set both params and content type");
		}
		request.setContentType(contentType);
		return this;
	}

	public RequestFiller authorization(String authorization) {
		request.setAuthorization(authorization);
		return this;
	}

	public RequestFiller headers(Map<String, String> headers) {
		request.setHeaders(headers);
		return this;
	}

	public RequestFiller params(Map<String, String> parameters) { 
		if(request.getContent() != null && !request.getMethod().equals("GET")) {
			throw new IllegalStateException(
					"Cannot set both params and content");
		}
		request.setParams(parameters);
		return this;
	}
	
	public Request build() {
		return request;
	}	

}