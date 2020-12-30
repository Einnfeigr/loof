package com.einnfeigr.taskApp.config;

import java.util.List;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mobile.device.DeviceHandlerMethodArgumentResolver;
import org.springframework.mobile.device.DeviceResolverHandlerInterceptor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.session.hazelcast.HazelcastIndexedSessionRepository;
import org.springframework.session.hazelcast.PrincipalNameExtractor;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.einnfeigr.taskApp.misc.Util;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapAttributeConfig;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan("com.einnfeigr.taskApp")
@EnableJpaRepositories("com.einnfeigr.taskApp.repository")
@EnableCaching
@EnableHazelcastHttpSession 
public class ApplicationConfig implements WebMvcConfigurer {
	
	@Bean
	public HazelcastInstance hazelcastInstance() {
		Config config = new Config();
		MapAttributeConfig attributeConfig = new MapAttributeConfig()
				.setName(HazelcastIndexedSessionRepository.PRINCIPAL_NAME_ATTRIBUTE)
				.setExtractor(PrincipalNameExtractor.class.getName());
		config.getMapConfig(HazelcastIndexedSessionRepository.DEFAULT_SESSION_MAP_NAME) 
				.addMapAttributeConfig(attributeConfig).addMapIndexConfig(
						new MapIndexConfig(
								HazelcastIndexedSessionRepository.PRINCIPAL_NAME_ATTRIBUTE, false));
		return Hazelcast.newHazelcastInstance(config); 
	}
	
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
