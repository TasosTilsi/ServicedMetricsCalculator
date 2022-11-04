package tasostilsi.uom.edu.gr.metricsCalculator.Services;

import ch.qos.logback.classic.Logger;
import jdk.jshell.execution.Util;
import nonapi.io.github.classgraph.json.JSONSerializer;
import org.eclipse.jgit.api.Git;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.Project;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.PrincipalResponseEntity;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.Revision;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.Utils.GitUtils;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.Utils.Utils;
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
	public void startNewAnalysis(String gitUrl) throws Exception{
		
		String accessToken = null;
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
		
		LOGGER.debug("Cloning repository from: " + project.getRepo() + " into " + project.getClonePath());
		Git git = GitUtils.getInstance().cloneRepository(project, accessToken);
		
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
		
		existsInDb = projectRepository.findById(projectEntity.getGitUrl());
		if (existsInDb.isPresent()) {
			LOGGER.error("EXISTS IN DB");
			/*List<String> existingCommitIds = getExistingCommitIds(project); // db connection to getExistingCommitIds
			diffCommitIds = GitUtils.getInstance().findDifferenceInCommitIds(commitIds, existingCommitIds);
			if (!diffCommitIds.isEmpty())
				currentRevision = getLastRevision(project); // db connection for getLastRevision
			else
				throw new Exception("ERROR_TO_BE_DESCRIBED_HERE");*/
		}
		
		if (!existsInDb.isPresent() || new HashSet<>(diffCommitIds).containsAll(commitIds)) {
			start = 1;
			Objects.requireNonNull(currentRevision).setSha(Objects.requireNonNull(commitIds.get(0)));
			Objects.requireNonNull(currentRevision).setRevisionCount(currentRevision.getRevisionCount() + 1);
			GitUtils.getInstance().checkout(project, accessToken, currentRevision, Objects.requireNonNull(git));
			System.out.printf("Calculating metrics for commit %s (%d)...\n", currentRevision.getSha(), currentRevision.getRevisionCount());
			Utils.getInstance().setMetrics(project, currentRevision);
			System.out.println("Calculated metrics for all files from first commit!");
//			InsertToDB.insertProjectToDatabase(project); //connection to db need here
			projectRepository.save(projectEntity);
			Utils.getInstance().insertData(project, currentRevision);
		} else {
//			retrieveJavaFiles(project);  //connection to db need here
			commitIds = new ArrayList<>(diffCommitIds);
		}
		
		for (int i = start; i < commitIds.size(); ++i) {
			Objects.requireNonNull(currentRevision).setSha(commitIds.get(i));
			currentRevision.setRevisionCount(currentRevision.getRevisionCount() + 1);
			GitUtils.getInstance().checkout(Objects.requireNonNull(project), accessToken, Objects.requireNonNull(currentRevision), Objects.requireNonNull(git));
			System.out.printf("Calculating metrics for commit %s (%d)...\n", currentRevision.getSha(), currentRevision.getRevisionCount());
			try {
//				PrincipalResponseEntity[] responseEntities = getResponseEntitiesAtCommit(git, currentRevision.getSha());
//				if (Objects.isNull(responseEntities) || responseEntities.length == 0) {
//					insertData(project, currentRevision);
//					System.out.println("Calculated metrics for all files!");
//					continue;
//				}
				System.out.println("Analyzing new/modified commit files...");
//				Utils.getInstance().setMetrics(project, currentRevision, responseEntities[0]);
				System.out.println("Calculated metrics for all files!");
//				insertData(project, currentRevision);
			} catch (Exception ignored) {
			}
		}
		System.out.printf("Finished analysing %d revisions.\n", Objects.requireNonNull(currentRevision).getRevisionCount());
		
	}
}
