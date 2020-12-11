package com.einnfeigr.taskApp.misc.mav;

import java.io.IOException;
import java.util.Map;

public interface Element {
	
	ModelAndViewBuilder and();
	
	String compile() throws IOException;
	
	Element path(String path);
	
	Element data(Object...data);
	
	Element data(Map<String, Object> data);
	
	Map<String, Object> getData();
	
}
