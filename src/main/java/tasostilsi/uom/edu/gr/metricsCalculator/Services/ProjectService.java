package tasostilsi.uom.edu.gr.metricsCalculator.Services;

import ch.qos.logback.classic.Logger;
import infrastructure.Project;
import nonapi.io.github.classgraph.json.JSONSerializer;
import org.eclipse.jgit.api.Git;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.GitUtils;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.Utils;

import java.io.File;

@Service
public class ProjectService implements IProjectService {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ProjectService.class);

    private Project project;


    public Project getProject() {
        LOGGER.info("Reply: " + JSONSerializer.serializeObject(this.project));
        LOGGER.info("You must post a project url first");
        return this.project;
    }

    public Project setProject(String url) {
        this.project = new Project(url);
        LOGGER.info("Reply: " + JSONSerializer.serializeObject(this.project));

        Git git = GitUtils.getInstance().cloneRepository(project, null);

        return this.project;
    }
}
