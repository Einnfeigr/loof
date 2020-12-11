package com.einnfeigr.taskApp.misc.http;

import java.io.IOException;

import com.einnfeigr.taskApp.exception.RequestException;
import com.einnfeigr.taskApp.misc.Util;

public abstract class RequestBuilder implements Builder {
	
	private static final RequestInitializer initializer =
			new RequestInitializer();
	
	protected RequestBuilder() {}
	
	public static RequestBuilder getInstance() {
		return initializer;
	}
	
	public static String performGet(String address) 
			throws IOException, RequestException {
		Response response = initializer.get(address).build().perform();
		if(response.getCode() == 200) {
			return response.getContent();
		} else {
			throw new RequestException(response);
		}
	}
	
	public static String performGet(String address, String... params) 
			throws IOException {
		Response response = initializer.get(address)
				.params(Util.arrayToMap(params))
				.build()
				.perform();
		if(response.getCode() == 200) {
			return response.getContent();
		} else {
			throw new RequestException(response);
		}
	}
	
	public static String performPost(String address) 
			throws IOException, RequestException {
		Response response = initializer.post(address).build().perform();
		if(response.getCode() == 200) {
			return response.getContent();
		} else {
			throw new RequestException(response);
		}
	}
	
	public static String performPost(String address, String... params) 
			throws IOException, RequestException {
		Response response = initializer.post(address)
				.params(Util.arrayToMap(params))
				.build()
				.perform();
		if(response.getCode() == 200) {
			return response.getContent();
		} else {
			throw new RequestException(response);
		}
	}
}

interface Builder {
	RequestFiller blank();
	RequestFiller get();
	RequestFiller get(String address);
	RequestFiller get(String address, String... params);
	RequestFiller post();
	RequestFiller post(String address);
	RequestFiller post(String address, String... params);
}
