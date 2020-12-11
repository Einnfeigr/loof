package com.einnfeigr.taskApp.template;

import java.io.IOException;
import java.util.Map;

import com.einnfeigr.taskApp.misc.Util;

public class TemplateFactory {

	private TemplateFactory() {}
		
	public static String buildTemplate(String path, String...data) 
			throws IOException {
		return fillTemplate(new EssentialTemplate(Util.arrayToMap(data)), path)
				.compile();
	}
	
	public static String buildTemplate(String path, Map<String, Object> data) 
			throws IOException {
		return fillTemplate(new EssentialTemplate(data), path).compile();
	}

	public static String buildTemplate(String templatePath) throws IOException {
		return fillTemplate(new EssentialTemplate(), templatePath).compile();
	}
	
	private static Template fillTemplate(Template template, String path) {
		if(!path.contains(".mustache")) {
			path += ".mustache";
		}
		if(!path.contains("templates/")) {
			path = "templates/"+path;
		}
		template.setTemplatePath(path);
		return template;
	}
	
}
