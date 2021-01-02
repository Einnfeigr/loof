package com.einnfeigr.taskApp.repository;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.einnfeigr.taskApp.pojo.Code;
import com.einnfeigr.taskApp.pojo.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	@Override
	@CacheEvict("userscache")
	<S extends User> S save(S entity);
	
	@Override
	@CacheEvict("userscache")
	<S extends User> List<S> saveAll(Iterable<S> entities);
	
	@Override
	@CacheEvict("userscache")
	void delete(User entity);
	
	@Override
	@CacheEvict("userscache")
	void deleteAll();
	
	@Query(value="SELECT * FROM users LEFT JOIN links ON links.user_id = users.id"
			+ " LEFT JOIN recovery_codes ON recovery_codes.user_id = users.id"
			+ " LEFT JOIN ids ON ids.user_id = users.id WHERE users.login = ?1",
			nativeQuery = true)
	User findByLogin(String login);

	@Query(value="SELECT * FROM users LEFT JOIN links ON links.user_id = users.id"
			+ " LEFT JOIN recovery_codes ON recovery_codes.user_id = users.id"
			+ " LEFT JOIN ids ON ids.user_id = users.id WHERE users.email = ?1",
			nativeQuery = true)
	User findByEmail(String email);

	@Query(value="SELECT * FROM users LEFT JOIN links ON links.user_id = users.id"
			+ " LEFT JOIN recovery_codes ON recovery_codes.user_id = users.id"
			+ " LEFT JOIN ids ON ids.user_id = users.id WHERE codes.id = ?1.id",
			nativeQuery = true)
	User findByCode(Code code);
	
}
