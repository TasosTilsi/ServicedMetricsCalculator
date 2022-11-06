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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/project")
public class ProjectController {
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ProjectController.class);
	
	@Autowired
	private final ProjectService service;
	
	private Project returnValue;
	
	public ProjectController(ProjectService service) {
		this.service = service;
	}
	
	@GetMapping(
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	)
	public ResponseEntity<List<Project>> getProjects() {
		List<Project> returnedList = service.getProjects();
		LOGGER.info("HttpRequest: getProject");
		if (returnedList == null) {
			LOGGER.error("Project Entity is null! You must post a project url first!");
			throw new NullPointerException("Project Entity is null! You must post a project url first!");
		}
		
		return new ResponseEntity<>(returnedList, HttpStatus.OK);
	}
}
