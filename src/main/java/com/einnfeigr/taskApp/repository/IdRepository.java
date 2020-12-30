package com.einnfeigr.taskApp.repository;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.einnfeigr.taskApp.pojo.Code;
import com.einnfeigr.taskApp.pojo.User;

@Cacheable
public interface IdRepository extends JpaRepository<Code, Long> {

	@Query(value="SELECT ids.id, ids.user_id, ids.nfc, "
			+ " users.id, users.name, users.login, users.email"
			+ " FROM ids"
			+ " LEFT JOIN users"
			+ " ON ids.user_id = users.id",
			nativeQuery=true)
	@Override
	@Cacheable("ids")
	List<Code> findAll();
	
	@Query(value="SELECT ids.id, ids.user_id, ids.nfc, "
			+ " users.id, users.name, users.login, users.email"
			+ " FROM ids"
			+ " LEFT JOIN users"
			+ " ON ids.user_id = users.id"
			+ " WHERE ids.id = ?1",
			nativeQuery=true)
	@Cacheable("ids")
	Code findById(String id);

	@Query(value="SELECT ids.id, ids.user_id, ids.nfc, "
			+ " users.id, users.name, users.login, users.email"
			+ " FROM ids"
			+ " LEFT JOIN users"
			+ " ON ids.user_id = users.id"
			+ " WHERE ids.user_id = ?1.id",
			nativeQuery=true)
	@Cacheable("ids")
	Code getByUser(User user);
	
}
