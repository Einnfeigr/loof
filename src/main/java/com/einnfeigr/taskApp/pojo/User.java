package com.einnfeigr.taskApp.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name="users")
public class User implements Cloneable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
	private Long id;
	
    @Column(name="name")
    private String name;
    
    @Column(name="login")
    private String login;
  
    @JsonIgnore
    @Column(name="password")
    private String password;
    
    @Column(name="fb_link")
    private String fbLink;

	@Column(name="ig_link")
    private String igLink;
    
    @Column(name="vk_link")
    private String vkLink;
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	 public String getFbLink() {
		return fbLink;
	}

	public void setFbLink(String fbLink) {
		this.fbLink = fbLink;
	}

	public String getIgLink() {
		return igLink;
	}

	public void setIgLink(String igLink) {
		this.igLink = igLink;
	}

	public String getVkLink() {
		return vkLink;
	}

	public void setVkLink(String vkLink) {
		this.vkLink = vkLink;
	}

}