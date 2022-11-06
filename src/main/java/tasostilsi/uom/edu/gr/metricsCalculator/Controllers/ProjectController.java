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
@RequestMapping(path = "api/v1/project")
public class ProjectController {
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ProjectController.class);
	
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
			LOGGER.error("Project Entity is null! You must post a project url first!");
			throw new NullPointerException("Project Entity is null! You must post a project url first!");
		}
		
		return new ResponseEntity<>(returnedList, HttpStatus.OK);
	}
	
	@GetMapping(
			path = "/by/owner",
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	)
	public ResponseEntity<List<Project>> getProjectsByOwner(@RequestParam String owner) {
		LOGGER.info("HttpRequest: getProjectsByOwner");
		List<Project> returnedList = service.getProjectsByOwner(owner);
		
		if (returnedList == null) {
			LOGGER.error("Project Entity is null! You must post a project url first!");
			throw new NullPointerException("Project Entity is null! You must post a project url first!");
		}
		
		return new ResponseEntity<>(returnedList, HttpStatus.OK);
	}
	
	@GetMapping(
			path = "/by/repo",
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	)
	public ResponseEntity<List<Project>> getProjectsByRepo(@RequestParam String repo) {
		LOGGER.info("HttpRequest: getProjectsByRepo");
		List<Project> returnedList = service.getProjectsByRepo(repo);
		
		if (returnedList == null) {
			LOGGER.error("Project Entity is null! You must post a project url first!");
			throw new NullPointerException("Project Entity is null! You must post a project url first!");
		}
		
		return new ResponseEntity<>(returnedList, HttpStatus.OK);
	}
	
	@GetMapping(
			path = "/by/url",
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	)
	public ResponseEntity<Project> getProjectByUrl(@RequestParam String url) {
		LOGGER.info("HttpRequest: getProjectByUrl");
		Project returned = service.getProjectByUrl(url);
		
		if (returned == null) {
			LOGGER.error("Project Entity is null! You must post a project url first!");
			throw new NullPointerException("Project Entity is null! You must post a project url first!");
		}
		
		return new ResponseEntity<>(returned, HttpStatus.OK);
	}
	
	@GetMapping(
			path = "/by/owner_and_repo",
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	)
	public ResponseEntity<Project> getProjectByOwnerAndRepo(@RequestParam String owner, @RequestParam String repo) {
		LOGGER.info("HttpRequest: getProjectByOwnerAndRepo");
		Project returned = service.getProjectByOwnerAndRepo(owner, repo);
		
		if (returned == null) {
			LOGGER.error("Project Entity is null! You must post a project url first!");
			throw new NullPointerException("Project Entity is null! You must post a project url first!");
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
			LOGGER.error("Project Entity is null! You must post a project url first!");
			throw new NullPointerException("Project Entity is null! You must post a project url first!");
		}
		
		return new ResponseEntity<>(returnedList, HttpStatus.OK);
	}
}
