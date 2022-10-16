package tasostilsi.uom.edu.gr.metricsCalculator.Controllers;


import ch.qos.logback.classic.Logger;
import infrastructure.Project;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.GitUser;
import tasostilsi.uom.edu.gr.metricsCalculator.Services.GitUserService;
import tasostilsi.uom.edu.gr.metricsCalculator.Services.ProjectService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "api/v1/user")
public class GitUserController {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(GitUserController.class);

    @Autowired
    private final GitUserService service;
    private GitUser returnValue;

    public GitUserController(GitUserService service) {
        this.service = service;
    }

    @GetMapping(
            path = "/{userId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<GitUser> getUser(@PathVariable String userId) {
        returnValue = service.getUser(userId);
        LOGGER.info("HttpRequest: getUser");
        if (returnValue == null) {
            LOGGER.error("You must create a user first");
            throw new NullPointerException("You must create a user first");
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }

    @PostMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<GitUser> createUser(@Valid @NotNull @RequestBody GitUser user) {
        returnValue = service.createUser(user);
        LOGGER.info("HttpRequest: createUser");
        return new ResponseEntity<>(returnValue, HttpStatus.OK);
    }
}
