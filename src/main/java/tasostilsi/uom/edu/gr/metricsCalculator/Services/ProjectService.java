package tasostilsi.uom.edu.gr.metricsCalculator.Services;

import ch.qos.logback.classic.Logger;
import nonapi.io.github.classgraph.json.JSONSerializer;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.Project;
import tasostilsi.uom.edu.gr.metricsCalculator.Repositories.ProjectRepository;
import tasostilsi.uom.edu.gr.metricsCalculator.Services.Interfaces.IProjectService;

import java.util.List;

@Service
public class ProjectService implements IProjectService {
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ProjectService.class);
	
	private final ProjectRepository projectRepository;
	
	@Autowired
	public ProjectService(ProjectRepository projectRepository) {
		this.projectRepository = projectRepository;
	}
	
	
	public List<Project> getProjects() {
		List<Project> projects = projectRepository.findAll();
		LOGGER.info("Reply: " + JSONSerializer.serializeObject(projects));
		
		return projects;
	}
}
