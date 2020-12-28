package com.einnfeigr.taskApp.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.einnfeigr.taskApp.pojo.RecoveryCode;

public interface RecoveryCodeRepository extends JpaRepository<RecoveryCode, String> {
	
	@Cacheable("recovery_codes")
	RecoveryCode findByCode(String code);
	
}
