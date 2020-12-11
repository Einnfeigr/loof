package com.einnfeigr.taskApp.misc.mav;

public enum Action {

	HREF("href"), ONCLICK("onclick"), REDIRECT("onclick"), RELOAD("onclick");
	
	Action(String attribute) {
		this.attribute = attribute;
	}
	
	public String compile(String content) {
		return attribute+"=\""+content+"\"";
	}
	
	String attribute;
	
}
