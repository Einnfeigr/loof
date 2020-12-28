package com.einnfeigr.taskApp.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.einnfeigr.taskApp.pojo.Code;
import com.einnfeigr.taskApp.pojo.User;

public interface IdRepository extends JpaRepository<Code, Long> {

	@Cacheable("ids")
	Code findById(String id);

	@Cacheable("ids")
	Code getByUser(User user);
	
}
