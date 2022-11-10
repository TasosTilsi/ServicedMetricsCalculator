package tasostilsi.uom.edu.gr.metricsCalculator.Services;

import ch.qos.logback.classic.Logger;
import nonapi.io.github.classgraph.json.JSONSerializer;
import org.eclipse.jgit.api.Git;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedClass;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedJavaFile;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.Project;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.Globals;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.PrincipalResponseEntity;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.Revision;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.Utils.GitUtils;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.Utils.Utils;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.DTOs.NewAnalysisDTO;
import tasostilsi.uom.edu.gr.metricsCalculator.Repositories.JavaFilesRepository;
import tasostilsi.uom.edu.gr.metricsCalculator.Repositories.ProjectRepository;
import tasostilsi.uom.edu.gr.metricsCalculator.Repositories.QualityMetricsRepository;
import tasostilsi.uom.edu.gr.metricsCalculator.Services.Interfaces.IAnalysisService;

import java.util.*;

@Service
public class AnalysisService implements IAnalysisService {
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(AnalysisService.class);
	
	private final ProjectRepository projectRepository;
	private final QualityMetricsRepository metricsRepository;
	private final JavaFilesRepository javaFilesRepository;
	private Project project;
	
	@Autowired
	public AnalysisService(ProjectRepository projectRepository, QualityMetricsRepository metricsRepository, JavaFilesRepository javaFilesRepository) {
		this.projectRepository = projectRepository;
		this.metricsRepository = metricsRepository;
		this.javaFilesRepository = javaFilesRepository;
	}
	
	@Override
	public void startNewAnalysis(NewAnalysisDTO newAnalysisDTO) throws Exception {
		
		String accessToken = newAnalysisDTO.getAccessToken();
		project = new Project(newAnalysisDTO.getGitUrl());
		
		LOGGER.info("Project : " + JSONSerializer.serializeObject(project));
		
		LOGGER.info("Cloning repository from: " + project.getRepo() + " into " + project.getClonePath());
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
		Optional<Project> existsInDb;
		Revision currentRevision = new Revision("", 0);
		
		existsInDb = projectRepository.findByUrl(project.getUrl());
		
		
		if (existsInDb.isPresent()) {
			LOGGER.info("EXISTS IN DB");
			
			/***********************************
			 //			List<String> existingCommitIds = javaFilesRepository.findDistinctRevisionShaByProjectId(projectId).stream().map(file -> file.getK().getRevision().getSha()).collect(Collectors.toList());// db connection to getExistingCommitIds
			 ***********************************/
			Long projectId = projectRepository.getIdByUrl(project.getUrl()).orElseThrow();
			
			List<String> existingCommitIds = javaFilesRepository.getDistinctRevisionShaByProjectId(projectId);  // db connection to getExistingCommitIds
			// db connection to getExistingCommitIds
			diffCommitIds = GitUtils.getInstance().findDifferenceInCommitIds(commitIds, existingCommitIds);
			if (!diffCommitIds.isEmpty()) {
				Revision revisionFromDb = javaFilesRepository.getRevisionByProjectIdOrderByRevisionCountDesc(projectId).orElseThrow();
				currentRevision.setSha(revisionFromDb.getSha()); // db connection for getLastRevision
				currentRevision.setCount(revisionFromDb.getCount()); // db connection for getLastRevision
			} else {
				throw new Exception("ERROR_TO_BE_DESCRIBED_HERE");
			}
		}
		
		if (!existsInDb.isPresent() || new HashSet<>(diffCommitIds).containsAll(commitIds)) {
			start = 1;
			Objects.requireNonNull(currentRevision).setSha(Objects.requireNonNull(commitIds.get(0)));
			Objects.requireNonNull(currentRevision).setCount(currentRevision.getCount() + 1);
			GitUtils.getInstance().checkout(project, accessToken, currentRevision, Objects.requireNonNull(git));
			LOGGER.info("Calculating metrics for commit %s (%d)...\n", currentRevision.getSha(), currentRevision.getCount());
			Utils.getInstance().setMetrics(project, currentRevision);
			LOGGER.info("Calculated metrics for all files from first commit!");
			projectRepository.save(project);
		} else {
			Long projectId = projectRepository.getIdByUrl(project.getUrl()).orElseThrow();
			Globals.getJavaFiles().addAll(javaFilesRepository.getAllByProjectId(projectId).orElseThrow()); // in retrieveMethod it uses the Globals class
			commitIds = new ArrayList<>(diffCommitIds);
		}
		
		for (int i = start; i < commitIds.size(); ++i) {
			Objects.requireNonNull(currentRevision).setSha(commitIds.get(i));
			currentRevision.setCount(currentRevision.getCount() + 1);
			GitUtils.getInstance().checkout(Objects.requireNonNull(project), accessToken, Objects.requireNonNull(currentRevision), Objects.requireNonNull(git));
			LOGGER.info(String.format("Calculating metrics for commit %s (%d)...\n", currentRevision.getSha(), currentRevision.getCount()));
			try {
				PrincipalResponseEntity[] responseEntities = GitUtils.getInstance().getResponseEntitiesAtCommit(git, currentRevision.getSha());
				if (Objects.isNull(responseEntities) || responseEntities.length == 0) {
					if (Globals.getJavaFiles().isEmpty()) {
						//			InsertToDB.insertEmpty(project, currentRevision);  //connection to db need here
						Set<CalculatedClass> calculatedClasses = new HashSet<>();
						CalculatedJavaFile javaFile = new CalculatedJavaFile("", calculatedClasses);
						projectRepository.initializeProjectAnalysis(javaFile, project.getUrl());
						
					} else {
						//			Globals.getJavaFiles().forEach(jf -> InsertToDB.insertFileToDatabase(project, jf, currentRevision)); //connection to db need here
						//			Globals.getJavaFiles().forEach(jf -> InsertToDB.insertMetricsToDatabase(project, jf, currentRevision));  //connection to db need here
					}
					LOGGER.info("Calculated metrics for all files!");
					continue;
				}
				LOGGER.info("Analyzing new/modified commit files...");
//				Utils.getInstance().setMetrics(project, currentRevision, responseEntities[0]);
				LOGGER.info("Calculated metrics for all files!");
//				insertData(project, currentRevision);
			} catch (Exception ignored) {
			}
		}
		LOGGER.info("Finished analysing %d revisions.\n", Objects.requireNonNull(currentRevision).getCount());
		
	}
}
