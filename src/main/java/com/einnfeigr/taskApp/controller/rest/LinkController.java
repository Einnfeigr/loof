package com.einnfeigr.taskApp.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.einnfeigr.taskApp.pojo.LinkType;
import com.einnfeigr.taskApp.repository.LinkRepository;
import com.einnfeigr.taskApp.repository.LinkTypeRepository;

@RestController
public class LinkController {

	@Autowired
	private LinkTypeRepository linkTypeRepository;
	
	@Autowired
	private LinkRepository linkRepository;
	
	public List<LinkType> getAllLinkTypes() {
		return linkTypeRepository.findAll();
	}
	
}
