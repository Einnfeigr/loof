package com.einnfeigr.taskApp.exception;

import java.io.IOException;

import com.einnfeigr.taskApp.misc.http.Response;

@Deprecated
public class RequestException extends IOException {

	private static final long serialVersionUID = -4925779678631468220L;
	
	private Response response;
	
	public RequestException(Response response) {
		super("caused by "+response.getCode()+" "+response.getResponseMessage()+
				" response status \n"+response.getContent());
		this.response = response;
	}
	
	public Response getResponse() {
		return response;
	}
	
	public String getContent() {
		return response.getContent();
	}
}
