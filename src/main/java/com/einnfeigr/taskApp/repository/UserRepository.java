package com.einnfeigr.taskApp.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.einnfeigr.taskApp.pojo.Code;
import com.einnfeigr.taskApp.pojo.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	@Cacheable("users")
	User findByLogin(String login);

	@Cacheable("users")
	User findByEmail(String email);

	@Cacheable("users")
	User findByCode(Code code);
	
}
