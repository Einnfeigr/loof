package com.einnfeigr.taskApp.misc.mav.mustache;

import java.io.IOException;

import com.einnfeigr.taskApp.misc.mav.ModelAndViewBuilder;
import com.einnfeigr.taskApp.misc.mav.Page;

public class MustachePage extends MustacheElement implements Page {

	public MustachePage title(String title) {
		builder.data("pageTitle", title);
		builder.title(title);
		return this;
	}
	
	public MustachePage(ModelAndViewBuilder builder) {
		super(builder);
	}
	
	@Override
	public String compile() throws IOException {
		return super.compile();
	}
	
}
