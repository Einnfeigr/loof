package com.einnfeigr.taskApp.misc.mav;

import java.io.IOException;

public abstract class AbstractElement implements Element {

	protected ModelAndViewBuilder builder;
	protected String templatePath;
	
	public AbstractElement(ModelAndViewBuilder builder) {
		this.builder = builder;
	}
	
	public ModelAndViewBuilder and() {
		return builder;
	}
	
	public AbstractElement templatePath(String templatePath) {
		this.templatePath = templatePath;
		return this;
	}
	
	public abstract String compile() throws IOException;
	
}
