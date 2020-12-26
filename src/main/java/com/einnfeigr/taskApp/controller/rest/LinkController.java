package com.einnfeigr.taskApp.controller.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.einnfeigr.taskApp.pojo.Link;
import com.einnfeigr.taskApp.pojo.LinkType;
import com.einnfeigr.taskApp.repository.LinkRepository;
import com.einnfeigr.taskApp.repository.LinkTypeRepository;

@RestController
public class LinkController {

	@Autowired
	private LinkTypeRepository linkTypeRepository;
	
	@Autowired
	private LinkRepository linkRepository;
	
	public LinkType getTypeByName(String name) {
		return linkTypeRepository.getByName(name);
	}
	
	public List<String> getNames() {
		List<String> names = new ArrayList<>();
		linkTypeRepository.findAll().forEach(t -> names.add(t.getName()));
		return names;
	}
	
	public List<LinkType> getAllLinkTypes() {
		List<LinkType> linkTypes = linkTypeRepository.findAll().stream()
				.sorted()
				.collect(Collectors.toList());
		return linkTypes;
	}

	public Link save(Link link) {
		return linkRepository.save(link);
	}
	
	public void delete(Link link) {
		linkRepository.delete(link);
	}
	
}
