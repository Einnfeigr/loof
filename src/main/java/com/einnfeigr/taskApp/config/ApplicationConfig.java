package com.einnfeigr.taskApp.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mobile.device.DeviceHandlerMethodArgumentResolver;
import org.springframework.mobile.device.DeviceResolverHandlerInterceptor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.einnfeigr.taskApp.misc.Util;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan("com.einnfeigr.taskApp")
@EnableJpaRepositories("com.einnfeigr.taskApp.repository")
public class ApplicationConfig implements WebMvcConfigurer {
	
    @Bean
    public DeviceResolverHandlerInterceptor deviceResolverHandlerInterceptor() { 
    	return new DeviceResolverHandlerInterceptor(); 
    }
	 
    @Bean
    public DeviceHandlerMethodArgumentResolver deviceHandlerMethodArgumentResolver() { 
        return new DeviceHandlerMethodArgumentResolver(); 
    }
 
    @Override
    public void addInterceptors(InterceptorRegistry registry) { 
        registry.addInterceptor(deviceResolverHandlerInterceptor()); 
    }
 
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(deviceHandlerMethodArgumentResolver()); 
    }
    
	@Bean
	public DriverManagerDataSource getDataSource() {
		DriverManagerDataSource bds = new DriverManagerDataSource();
		bds.setDriverClassName("com.mysql.cj.jdbc.Driver");
		bds.setUrl("jdbc:mysql://"+System.getenv("db.host")+":"
				+ (System.getenv("db.port") != null ? System.getenv("db.port") : "3306")+"/"
				+ System.getenv("db.schema")
				+ "?serverTimezone=UTC"
				+ "&useUnicode=yes&characterEncoding=UTF-8");
		bds.setUsername(System.getenv("db.username"));
		bds.setPassword(System.getenv("db.password"));
		return bds;
	}
	
	@Bean
	public Util util() {
		return new Util();
	}
	
	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}
	
}
