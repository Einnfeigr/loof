package com.einnfeigr.taskApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.einnfeigr.taskApp.pojo.Code;
import com.einnfeigr.taskApp.pojo.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	User findByLogin(String login);

	User findByEmail(String email);
	
	User findByCode(Code code);
	
}
