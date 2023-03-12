/*
 * ******************************************************************************
 *  * Copyright (C) 2022-2023 University of Macedonia
 *  *
 *  * This program and the accompanying materials are made
 *  * available under the terms of the Eclipse Public License 2.0
 *  * which is available at https://www.eclipse.org/legal/epl-2.0/
 *  *
 *  * SPDX-License-Identifier: EPL-2.0
 *  *****************************************************************************
 */

package tasostilsi.uom.edu.gr.metricsCalculator.Controllers;


import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.Project;
import tasostilsi.uom.edu.gr.metricsCalculator.Services.ProjectService;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/project")
public class ProjectController {
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ProjectController.class);
	
	private static final String PROJECT_ENTITY_NULL_MESSAGE = "Project Entity is null! You must post a project url first!";
	
	@Autowired
	private final ProjectService service;
	
	public ProjectController(ProjectService service) {
		this.service = service;
	}
	
	@GetMapping(
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	)
	public ResponseEntity<List<Project>> getProjects() {
		LOGGER.info("HttpRequest: getProjects");
		List<Project> returnedList = service.getProjects();
		
		if (returnedList == null) {
			LOGGER.error(PROJECT_ENTITY_NULL_MESSAGE);
			throw new NullPointerException(PROJECT_ENTITY_NULL_MESSAGE);
		}
		
		return new ResponseEntity<>(returnedList, HttpStatus.OK);
	}
	
	@GetMapping(
			path = "/owner",
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	)
	public ResponseEntity<List<Project>> getProjectsByOwner(@RequestParam String owner) {
		LOGGER.info("HttpRequest: getProjectsByOwner");
		List<Project> returnedList = service.getProjectsByOwner(owner);
		
		if (returnedList == null) {
			LOGGER.error(PROJECT_ENTITY_NULL_MESSAGE);
			throw new NullPointerException(PROJECT_ENTITY_NULL_MESSAGE);
		}
		
		return new ResponseEntity<>(returnedList, HttpStatus.OK);
	}
	
	@GetMapping(
			path = "/repo",
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	)
	public ResponseEntity<List<Project>> getProjectsByRepo(@RequestParam String repo) {
		LOGGER.info("HttpRequest: getProjectsByRepo");
		List<Project> returnedList = service.getProjectsByRepo(repo);
		
		if (returnedList == null) {
			LOGGER.error(PROJECT_ENTITY_NULL_MESSAGE);
			throw new NullPointerException(PROJECT_ENTITY_NULL_MESSAGE);
		}
		
		return new ResponseEntity<>(returnedList, HttpStatus.OK);
	}
	
	@GetMapping(
			path = "/url",
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	)
	public ResponseEntity<Project> getProjectByUrl(@RequestParam String url) {
		LOGGER.info("HttpRequest: getProjectByUrl");
		Project returned = service.getProjectByUrl(url);
		
		if (returned == null) {
			LOGGER.error(PROJECT_ENTITY_NULL_MESSAGE);
			throw new NullPointerException(PROJECT_ENTITY_NULL_MESSAGE);
		}
		
		return new ResponseEntity<>(returned, HttpStatus.OK);
	}
	
	@GetMapping(
			path = "/owner_and_repo",
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	)
	public ResponseEntity<Project> getProjectByOwnerAndRepo(@RequestParam String owner, @RequestParam String repo) {
		LOGGER.info("HttpRequest: getProjectByOwnerAndRepo");
		Project returned = service.getProjectByOwnerAndRepo(owner, repo);
		
		if (returned == null) {
			LOGGER.error(PROJECT_ENTITY_NULL_MESSAGE);
			throw new NullPointerException(PROJECT_ENTITY_NULL_MESSAGE);
		}
		
		return new ResponseEntity<>(returned, HttpStatus.OK);
	}
	
	@GetMapping(
			path = "{id}",
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	)
	public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
		LOGGER.info("HttpRequest: getProjectsById");
		Project returnedList = service.getProjectById(id);
		
		if (returnedList == null) {
			LOGGER.error(PROJECT_ENTITY_NULL_MESSAGE);
			throw new NullPointerException(PROJECT_ENTITY_NULL_MESSAGE);
		}
		
		return new ResponseEntity<>(returnedList, HttpStatus.OK);
	}
	
	@DeleteMapping(
			path = "/url"
	)
	public ResponseEntity<String> deleteProjectByUrl(@RequestParam String url) {
		LOGGER.info("HttpRequest: deleteProjectByUrl");
		service.deleteProjectByUrl(url);
		
		return new ResponseEntity<>("Project with url " + url + " deleted", HttpStatus.OK);
	}
	
	@DeleteMapping(
			path = "{id}"
	)
	public ResponseEntity<String> deleteProjectById(@PathVariable Long id) {
		LOGGER.info("HttpRequest: deleteProjectById");
		service.deleteProjectById(id);
		
		return new ResponseEntity<>("Project with id " + id + " deleted", HttpStatus.OK);
	}
}
