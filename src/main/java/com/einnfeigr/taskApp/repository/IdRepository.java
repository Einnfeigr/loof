package com.einnfeigr.taskApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.einnfeigr.taskApp.pojo.Code;

public interface IdRepository extends JpaRepository<Code, Long> {

	Code findById(String id);
	
}
