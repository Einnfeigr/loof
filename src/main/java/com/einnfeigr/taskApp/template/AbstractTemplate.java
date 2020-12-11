package com.einnfeigr.taskApp.template;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public abstract class AbstractTemplate implements Template {

	String path;
	Map<String, Template> partials = new HashMap<>();
	
	@Override
	public void setTemplatePath(String templatePath) {	
		this.path = templatePath;
	}
	
	@Override
	public String getTemplatePath() {
		return path;
	}
	
	@Override
	public void addPartial(String name, Template template) {
		partials.put(name, template);
	}
	
	@Override
	public String compile() throws IOException {
	   	MustacheFactory factory = new DefaultMustacheFactory();
	   	Mustache mustache = factory.compile(path);
	   	StringWriter writer = new StringWriter();
	   	mustache.execute(writer, "").flush();
	   	return writer.toString();
	}
}
