package com.einnfeigr.taskApp.template;

public class TemplateBuilder {
	
	public TemplateFiller build(String path) {
		if(!path.contains(".mustache")) {
			path += ".mustache";
		}
		return new TemplateFiller(path);
	}
	
}
