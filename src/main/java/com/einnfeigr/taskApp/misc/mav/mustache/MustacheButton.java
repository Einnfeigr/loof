package com.einnfeigr.taskApp.misc.mav.mustache;

import java.io.IOException;

import com.einnfeigr.taskApp.misc.mav.Action;
import com.einnfeigr.taskApp.misc.mav.Button;
import com.einnfeigr.taskApp.misc.mav.ModelAndViewBuilder;

public class MustacheButton extends MustacheElement implements Button {

	private String url;
	private String title;
	private Action action;
	
	public MustacheButton(ModelAndViewBuilder builder) {
		super(builder);
	}
	
	public MustacheButton title(String title) {
		this.title = title;
		return this;
	}
	
	public MustacheButton action(Action action) {
		this.action = action;
		return this;
	}

	@Override
	public String compile() throws IOException {
		data.put("title", title);
		data.put("action", action.compile(url));
		return super.compile();
	}
	
}
