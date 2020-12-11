package com.einnfeigr.taskApp.misc.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Request {
	
	private final static Logger logger = 
			LoggerFactory.getLogger(Request.class);
	
	private String method;
	private String address;
	private String content;
	private String contentType;
	private String authorization;
	private Map<String, String> headers;
	private Map<String, String> params;
	
	Request() {}

	void setMethod(String method) {
		this.method = method;
	}

	public String getMethod() {
		return method;
	}
	
	void setAddress(String address) {
		this.address = address;
	}
	
	public String getAddress() {
		return address;
	}

	void setContent(String content) {
		this.content = content;
	}
	
	public String getContent() {
		return content;
	}

	void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}
	
	void setAuthorization(String authorization) {
		this.authorization = authorization;
	}

	public String getAuthorization() {
		return authorization;
	}
	
	void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}
	
	void setParams(Map<String, String> params) {
		this.params = params;
	}
	
	public Map<String, String> getParams() {
		return params;
	}

	public Response perform() throws IOException {
		String responseContent;
		if(params != null) {
			if(method.equals("GET")) {
				address = appendParams(address, params);
			} else {
				contentType = "application/x-www-form-urlencoded";
			}
		} 
		URL url = new URL(address);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod(method);
		if(headers != null) {
			headers.forEach((k, v) -> connection.setRequestProperty(k, v));
		}
		if(authorization != null) {
			connection.setRequestProperty("Authorization", authorization);
		}
		if(content != null) {
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-length", 
					String.valueOf(content.length()));
			connection.setRequestProperty("Content-type", contentType);
			try(BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(connection.getOutputStream()))) {
				writer.write(content.toString());
			}
		}
		connection.connect();
		try {
			responseContent = readContent(connection.getInputStream());
		} catch(IOException e) {
			responseContent = readContent(connection.getErrorStream());
		}
		connection.disconnect();
		if(logger.isDebugEnabled()) {
			logger.debug("performed "+method+" request on address '"
					+address+"'"
					+" with response code "+connection.getResponseCode());
			logger.debug("headers:");
			for(Entry<String, List<String>> entry : connection.getHeaderFields()
					.entrySet()) {
				logger.debug(entry.getKey()+":");
				for(String str : entry.getValue()) {
					logger.debug("	"+str);
				}
			}
			logger.debug(responseContent.toString());
		}
		return new Response(connection.getResponseMessage(),
				responseContent, connection.getResponseCode());
	}
	
	private String readContent(InputStream inputStream) throws IOException {
		StringBuilder content = new StringBuilder();
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream))) {
			reader.lines().forEach(l -> content.append(l+"\n"));
		}
		return content.toString();
	}
	
	private String appendParams(String address, Map<String, String> params) {
		StringBuilder output = new StringBuilder(address);
		if(!address.contains("?")) {
			if(!address.endsWith("/")) {
				output.append("/");
			}
			output.append("?");
		} else {
			if(!address.endsWith("&")) {
				output.append("&");
			}
		}
		boolean first = true;
		for(Entry<String, String> entry : params.entrySet()) {	
			String key = entry.getKey();
			String value = entry.getValue();
			if(first) {
				first = false;
			} else {
				output.append("&");
			}
			output.append(key+"="+value);
		}
		return output.toString();
	}
	
}
