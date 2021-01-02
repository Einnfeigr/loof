package com.einnfeigr.taskApp.repository;

import java.util.List;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.einnfeigr.taskApp.pojo.LinkType;

public interface LinkTypeRepository extends JpaRepository<LinkType, Long> {

	@Override
	@CacheEvict("linkTypesCache")
	void delete(LinkType entity);
	
	@Override
	@CacheEvict("linkTypesCache")
	void deleteAll();
	
	@Override
	@CacheEvict("linkTypesCache")
	<S extends LinkType> S save(S entity);

	@Override
	@CacheEvict("linkTypesCache")
	<S extends LinkType> List<S> saveAll(Iterable<S> entities);	
	
	@Query(value="SELECT *\r\n" + 
			"	FROM link_types\r\n" + 
			"    ORDER BY priority",
			nativeQuery=true)
	@Override
	List<LinkType> findAll();
	
	LinkType getByName(String name);
	
}
