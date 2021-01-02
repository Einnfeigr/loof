package com.einnfeigr.taskApp.repository;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.einnfeigr.taskApp.pojo.Link;
import com.einnfeigr.taskApp.pojo.LinkType;
import com.einnfeigr.taskApp.pojo.User;

public interface LinkRepository extends JpaRepository<Link, Long>{
	
	@Override
	@CacheEvict("links")
	<S extends Link> List<S> saveAll(Iterable<S> entities);
	
	@Override
	@CacheEvict("links")
	<S extends Link> S save(S entity);
	
	@Override
	@CacheEvict("links")
	void delete(Link link);
	
	@Override
	@CacheEvict("links")
	void deleteAll();
	
	@Query(value="SELECT links.id, links.title, links.link, links.is_custom,\r\n" + 
			" users.name, users.login, users.email,\r\n" + 
			" link_types.name, link_types.priority, link_types.domain\r\n" + 
			"	FROM links \r\n" + 
			"    LEFT JOIN users \r\n" + 
			"		ON links.user_id = users.id\r\n" + 
			"	LEFT JOIN link_types\r\n" + 
			"		ON links.type_id = link_types.id",
			nativeQuery=true)
	@Override
	@Cacheable("links")
	List<Link> findAll(); 
	
	@Query(value="SELECT links.id, links.title, links.link, links.is_custom,\r\n" + 
			" users.name, users.login, users.email,\r\n" + 
			" link_types.name, link_types.priority, link_types.domain\r\n" + 
			"	FROM links \r\n" + 
			"    LEFT JOIN users \r\n" + 
			"		ON links.user_id = users.id\r\n" + 
			"	LEFT JOIN link_types\r\n" + 
			"		ON links.type_id = link_types.id"
			+ "	WHERE user_id = ?1.id",
			nativeQuery=true)
	@Cacheable("links")
	Link findByUser(User user);
	
	@Cacheable("links")
	void deleteByType(LinkType type);
}
