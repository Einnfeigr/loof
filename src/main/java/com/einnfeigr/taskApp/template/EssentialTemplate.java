package com.einnfeigr.taskApp.template;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

public class EssentialTemplate extends AbstractTemplate {
	
	Map<String, Object> data;
	
	EssentialTemplate() {}
	
	EssentialTemplate(Map<String, Object> data) {
		this.data = data;
	}
	
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
	public Map<String, Object> getData() {
		return data;
	}
	
	@Override
	public String compile() throws IOException {
	   	DefaultMustacheFactory factory = new DefaultMustacheFactory();
	   	for(Entry<String, Template> entry : partials.entrySet()) {
	   		data.put(entry.getKey(), entry.getValue().compile());
	   	}
	   	Mustache mustache = factory.compile(path);
	   	StringWriter writer = new StringWriter();
	   	mustache.execute(writer, data).flush();
	   	return writer.toString();
	}

}
