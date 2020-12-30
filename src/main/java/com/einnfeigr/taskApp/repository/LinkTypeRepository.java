package com.einnfeigr.taskApp.repository;

import java.util.List;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.einnfeigr.taskApp.pojo.LinkType;

@CacheConfig(cacheNames={"linkTypes"})   
@Cacheable
public interface LinkTypeRepository extends JpaRepository<LinkType, Long> {

	@Query(value="SELECT *\r\n" + 
			"	FROM link_types\r\n" + 
			"    ORDER BY priority",
			nativeQuery=true)
	@Cacheable(value="linkTypesCache")
	@Override
	List<LinkType> findAll();
	
	@Cacheable("linkTypesCache")
	LinkType getByName(String name);
	
}
