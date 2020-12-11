package com.einnfeigr.taskApp;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.einnfeigr.taskApp.misc.Util;
import com.einnfeigr.taskApp.misc.http.RequestBuilder;

@EnableScheduling
@Component
public class Notifier {

	private final static Logger logger = 
			LoggerFactory.getLogger(Notifier.class);
	
	@Scheduled(fixedDelay = 1200000)
	public void notifyServer() { 
		try {
			String url = System.getenv("currentUrl");
			if(url != null) {
				RequestBuilder.performGet(url);
				logger.info("server has been notified");
			} else {
				logger.warn("server cannot be notified because url is null");
			}
		} catch(Exception e) {
			logger.error(Util.EXCEPTION_LOG_MESSAGE, e);	
		}
	}
	
}