package com.einnfeigr.taskApp.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.einnfeigr.taskApp.pojo.LinkType;

public interface LinkTypeRepository extends JpaRepository<LinkType, Long> {

	@Cacheable("link_types")
	LinkType getByName(String name);
	
}
