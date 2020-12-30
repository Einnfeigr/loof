package com.einnfeigr.taskApp.pojo;

import java.time.LocalDateTime;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name="recovery_codes")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RecoveryCode {

	@Id
	@Column
	private String code;
	
	@OneToOne
	private User user;

	@Column
	private LocalDateTime expires;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public LocalDateTime getExpires() {
		return expires;
	}

	public void setExpires(LocalDateTime expires) {
		this.expires = expires;
	}
	
}
