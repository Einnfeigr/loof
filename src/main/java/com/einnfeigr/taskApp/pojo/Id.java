package com.einnfeigr.taskApp.pojo;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="ids")
public class Id {

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
