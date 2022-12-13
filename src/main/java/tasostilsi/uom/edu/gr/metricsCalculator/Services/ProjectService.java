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
//		LOGGER.info("Reply: " + JSONSerializer.serializeObject(projects));
		
		return projects;
	}
	
	@Override
	public List<Project> getProjectsByOwner(String owner) {
		List<Project> projects = projectRepository.findByOwner(owner);
//		LOGGER.info("Reply: " + JSONSerializer.serializeObject(projects));
		
		return projects;
	}
	
	@Override
	public List<Project> getProjectsByRepo(String repo) {
		List<Project> projects = projectRepository.findByRepo(repo);
//		LOGGER.info("Reply: " + JSONSerializer.serializeObject(projects));
		
		return projects;
	}
	
	@Override
	public Project getProjectById(Long id) {
		Project project = projectRepository.findById(id).orElseThrow();
//		LOGGER.info("Reply: " + JSONSerializer.serializeObject(project));
		
		return project;
	}
	
	@Override
	public Project getProjectByUrl(String url) {
		Project project = projectRepository.findByUrl(url).orElseThrow();
//		LOGGER.info("Reply: " + JSONSerializer.serializeObject(project));
		
		return project;
	}
	
	@Override
	public Project getProjectByOwnerAndRepo(String owner, String repo) {
		Project project = projectRepository.findByOwnerAndRepo(owner, repo).orElseThrow();
//		LOGGER.info("Reply: " + JSONSerializer.serializeObject(project));
		
		return project;
	}
}
