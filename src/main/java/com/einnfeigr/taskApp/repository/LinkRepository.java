package com.einnfeigr.taskApp.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.einnfeigr.taskApp.pojo.Link;
import com.einnfeigr.taskApp.pojo.LinkType;
import com.einnfeigr.taskApp.pojo.User;

public interface LinkRepository extends JpaRepository<Link, Long>{
	
	@Cacheable("links")
	Link findByUser(User user);
	
	@Cacheable("links")
	void deleteByType(LinkType type);
}
