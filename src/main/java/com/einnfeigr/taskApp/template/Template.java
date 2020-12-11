package com.einnfeigr.taskApp.template;

import java.io.IOException;

public interface Template {
	
	public String getTemplatePath();
	public void setTemplatePath(String templatePath);
	public void addPartial(String name, Template template);
	
	public String compile() throws IOException;
}
