package com.einnfeigr.taskApp.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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

    @Column(name="email")
    private String email;
    
    @OneToOne(mappedBy="user")
    private RecoveryCode recoveryCode;
    
    
    @OneToMany(mappedBy="user", cascade=CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Link> links = new ArrayList<>();
    
    @OneToOne(mappedBy="user")
    private Code code;
    
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

	public RecoveryCode getRecoveryCode() {
		return recoveryCode;
	}

	public void setRecoveryCode(RecoveryCode recoveryCode) {
		this.recoveryCode = recoveryCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Link> getLinks() {
		return links.stream().sorted().collect(Collectors.toList());
	}
	
	public List<Link> getLinks(List<LinkType> types) {
		List<Link> links = this.links.stream().sorted().collect(Collectors.toList());
		for(LinkType type : types) {
			if(!hasLink(type) || type.getName().toLowerCase().equals("custom")) {
				Link link = new Link();
				link.setType(type);
				link.setIsCustom(type.getName().toLowerCase().equals("custom") 
						|| type.getName().toLowerCase().equals("email"));
				links.add(link);
			}
		}
		return links;
	}
	
	public List<Link> getLinks(LinkType type) {
		return links.stream()
				.filter(l -> l.getType().getName().equals(type.getName()))
				.collect(Collectors.toList());
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public boolean hasLink(LinkType type) {
		for(Link link : links) {
			if(link.getType().getName().equals(type.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasLink(String name) {
		for(Link link : links) {
			if(link.getTitle().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public void addLink(Link link) {
		links.add(link);
	}
	
	public void removeLink(LinkType type) {
		int index = -1;
		for(Link link : links) {
			if(link.getType().getName().equals(type.getName())) {
				index = links.indexOf(link);
			}
		}
		if(index > -1) {
			links.remove(index);			
		}
	}

	public void removeLink(String name) {
		int index = -1;
		for(Link link : links) {
			if(link.getType().getName().equals(name)) {
				index = links.indexOf(link);
			}
		}
		if(index > -1) {
			links.remove(index);
		}
	}
	
	public Code getCode() {
		return code;
	}

	public void setCode(Code code) {
		this.code = code;
	}

	public boolean hasLinkUrl(String url) {
		for(Link link : links) {
			if(link.getLink().equals(url)) {
				return true;
			}
		}
		return false;
	}
	
}