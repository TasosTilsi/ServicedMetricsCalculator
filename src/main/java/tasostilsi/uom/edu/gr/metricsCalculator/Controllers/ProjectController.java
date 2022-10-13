package tasostilsi.uom.edu.gr.metricsCalculator.Controllers;


import ch.qos.logback.classic.Logger;
import infrastructure.Project;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tasostilsi.uom.edu.gr.metricsCalculator.Services.ProjectService;

@RestController
@RequestMapping(path = "api/v1/project")
public class ProjectController {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ProjectController.class);

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<Project> getProject() {
        Project returnValue = projectService.getProject();
        LOGGER.info("HttpRequest: getProject");
        if (returnValue == null) {
            LOGGER.info("Project entity is null");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }

    @PostMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<Project> setProject(@RequestParam("url") String url) {
        Project returnValue = projectService.setProject(url);
        LOGGER.info("HttpRequest: setProject");
        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }
}
