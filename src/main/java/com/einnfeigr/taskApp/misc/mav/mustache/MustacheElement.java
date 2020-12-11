package com.einnfeigr.taskApp.misc.mav.mustache;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.einnfeigr.taskApp.misc.Util;
import com.einnfeigr.taskApp.misc.mav.AbstractElement;
import com.einnfeigr.taskApp.misc.mav.ModelAndViewBuilder;
import com.einnfeigr.taskApp.template.TemplateFactory;

public abstract class MustacheElement extends AbstractElement {

	public MustacheElement(ModelAndViewBuilder builder) {
		super(builder);
	}

	protected String templatePath;
	protected Map<String, Object> data = new HashMap<>();
	
	public MustacheElement path(String templatePath) {
		this.templatePath = templatePath;
		return this;
	}
	
	public MustacheElement data(Object...data) {
		this.data.putAll(Util.arrayToMap(data));
		return this;
	}
	
	public MustacheElement data(Map<String, Object> data) {
		this.data.putAll(data);
		return this;
	}
	
	public String compile() throws IOException {
		return TemplateFactory.buildTemplate(templatePath, data);
	}
	
	public Map<String, Object> getData() {
		return data;
	}
	
}
