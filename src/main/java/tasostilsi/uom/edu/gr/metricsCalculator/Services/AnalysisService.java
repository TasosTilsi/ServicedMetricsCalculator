package tasostilsi.uom.edu.gr.metricsCalculator.Services;

import ch.qos.logback.classic.Logger;
import nonapi.io.github.classgraph.json.JSONSerializer;
import org.eclipse.jgit.api.Git;
import org.hibernate.id.IncrementGenerator;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.Project;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.Revision;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.Utils.GitUtils;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.JavaFilesEntity;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.ProjectEntity;
import tasostilsi.uom.edu.gr.metricsCalculator.Repositories.ProjectRepository;
import tasostilsi.uom.edu.gr.metricsCalculator.Services.Interfaces.IAnalysisService;

import java.util.*;

@Service
public class AnalysisService implements IAnalysisService {
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(AnalysisService.class);
	
	private final ProjectRepository projectRepository;
	private Project project;
	
	@Autowired
	public AnalysisService(ProjectRepository projectRepository) {
		this.projectRepository = projectRepository;
	}
	
	@Override
	public void startNewAnalysis(String gitUrl) {
		project = new Project(gitUrl);
		LOGGER.info("Project : " + JSONSerializer.serializeObject(project));
		
		ProjectEntity projectEntity = new ProjectEntity();
		projectEntity.setId(Math.abs(new Random().nextLong()));
		projectEntity.setClonePath(project.getClonePath());
		projectEntity.setGitUrl(project.getUrl());
		projectEntity.setRepo(project.getRepo());
		projectEntity.setOwner(project.getOwner());
		Set<JavaFilesEntity> javaFilesEntities = new HashSet<>();
//		javaFilesEntities.addAll(project.getJavaFiles());
		
		projectRepository.save(projectEntity);
		
		LOGGER.debug("Cloning repository from: " + project.getRepo() + " into " + project.getClonePath());
		Git git = GitUtils.getInstance().cloneRepository(project, null);
		
		List<String> diffCommitIds = new ArrayList<>();
		List<String> commitIds = GitUtils.getInstance().getCommitIds(git);
		
		try {
			Collections.reverse(commitIds);
		} catch (Exception e) {
			LOGGER.error("CommitIds List might be null");
			e.printStackTrace();
		}
		
		int start = 0;
		Optional<ProjectEntity> existsInDb;
		Revision currentRevision = new Revision("", 0);
		
		
		try {
			existsInDb = projectRepository.findById(projectEntity.getGitUrl());
			if (existsInDb.isPresent()) {
				LOGGER.error("EXISTS IN DB");
//				List<String> existingCommitIds = getExistingCommitIds(project);
//				diffCommitIds = findDifferenceInCommitIds(commitIds, existingCommitIds);
//				if (!diffCommitIds.isEmpty())
//					currentRevision = getLastRevision(project);
//				else
//					System.exit(0);
			}
		} catch (Exception ignored) {
		}
		
	}
}
