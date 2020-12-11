package com.einnfeigr.taskApp.misc.http;

import com.einnfeigr.taskApp.misc.Util;

public class RequestInitializer extends RequestBuilder {
	
	@Override
	public RequestFiller blank() {
		return new RequestFiller(new Request());
	}
	
	@Override
	public RequestFiller get() {
		return blank().method("GET");
	}
	
	@Override
	public RequestFiller get(String address) {
		return get().address(address);
	}
	
	@Override
	public RequestFiller get(String address, String... params) {
		return get(address).params(Util.arrayToMap(params));
	}

	@Override
	public RequestFiller post() {
		return blank().method("POST");
	}

	@Override
	public RequestFiller post(String address) {
		return post().address(address);
	}
	
	@Override
	public RequestFiller post(String address, String... params) {
		return post(address).params(Util.arrayToMap(params));
	}
}
