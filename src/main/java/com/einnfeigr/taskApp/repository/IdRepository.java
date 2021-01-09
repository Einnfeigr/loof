package com.einnfeigr.taskApp.repository;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.einnfeigr.taskApp.pojo.Code;
import com.einnfeigr.taskApp.pojo.User;

public interface IdRepository extends JpaRepository<Code, Long> {

	@Override
	@CacheEvict("ids")
	void delete(Code entity);
	
	@Override
	@CacheEvict("ids")
	void deleteAll();
	
	@Override
	@CacheEvict("ids")
	<S extends Code> List<S> saveAll(Iterable<S> entities);
	
	@Override
	@CacheEvict("ids")
	<S extends Code> S save(S entity);
	
	@Query(value="SELECT ids.id, ids.user_id, ids.nfc, "
			+ " users.id, users.name, users.login, users.email"
			+ " FROM ids"
			+ " LEFT JOIN users"
			+ " ON ids.user_id = users.id",
			nativeQuery=true)
	@Override
	List<Code> findAll();
	
	@Query(value="SELECT ids.id, ids.user_id, ids.nfc, "
			+ " users.id, users.name, users.login, users.email"
			+ " FROM ids"
			+ " LEFT JOIN users"
			+ " ON ids.user_id = users.id"
			+ " WHERE ids.id = ?1",
			nativeQuery=true)
	Code findById(String id);

	@Query(value="SELECT ids.id, ids.user_id, ids.nfc, "
			+ " users.id, users.name, users.login, users.email"
			+ " FROM ids"
			+ " LEFT JOIN users"
			+ " ON ids.user_id = users.id"
			+ " WHERE ids.user_id = ?1",
			nativeQuery=true)
	Code getByUser(Long id);
	
}
