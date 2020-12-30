package com.einnfeigr.taskApp.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.einnfeigr.taskApp.pojo.Code;
import com.einnfeigr.taskApp.pojo.User;

@Cacheable
public interface UserRepository extends JpaRepository<User, Long> {
	
	@Query(value="SELECT * FROM users LEFT JOIN links ON links.user_id = users.id"
			+ " LEFT JOIN recovery_codes ON recovery_codes.user_id = users.id"
			+ " LEFT JOIN ids ON ids.user_id = users.id WHERE users.login = ?1",
			nativeQuery = true)
	@Cacheable(value="userscache", key="#login")  
	User findByLogin(String login);

	@Query(value="SELECT * FROM users LEFT JOIN links ON links.user_id = users.id"
			+ " LEFT JOIN recovery_codes ON recovery_codes.user_id = users.id"
			+ " LEFT JOIN ids ON ids.user_id = users.id WHERE users.email = ?1",
			nativeQuery = true)
	@Cacheable(value="userscache", key="#email")
	User findByEmail(String email);

	@Query(value="SELECT * FROM users LEFT JOIN links ON links.user_id = users.id"
			+ " LEFT JOIN recovery_codes ON recovery_codes.user_id = users.id"
			+ " LEFT JOIN ids ON ids.user_id = users.id WHERE codes.id = ?1.id",
			nativeQuery = true)
	@Cacheable(value="userscache", key="#code")
	User findByCode(Code code);
	
}
