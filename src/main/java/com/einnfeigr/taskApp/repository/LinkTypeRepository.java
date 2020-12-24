package com.einnfeigr.taskApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.einnfeigr.taskApp.pojo.LinkType;

public interface LinkTypeRepository extends JpaRepository<LinkType, Long> {

	LinkType getByName(String name);
	
}
