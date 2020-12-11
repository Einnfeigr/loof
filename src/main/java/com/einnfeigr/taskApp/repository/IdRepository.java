package com.einnfeigr.taskApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.einnfeigr.taskApp.pojo.Id;

public interface IdRepository extends JpaRepository<Id, Long> {

	Id findById(String id);
	
}
