/*
 * ******************************************************************************
 *  * Copyright (C) 2022-2023 University of Macedonia
 *  *
 *  * This program and the accompanying materials are made
 *  * available under the terms of the Eclipse Public License 2.0
 *  * which is available at https://www.eclipse.org/legal/epl-2.0/
 *  *
 *  * SPDX-License-Identifier: EPL-2.0
 *  *****************************************************************************
 */
package tasostilsi.uom.edu.gr.metricsCalculator.Services;

import ch.qos.logback.classic.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.Enums.State;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.Project;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.Globals;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.PrincipalResponseEntity;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.Revision;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.Utils.GitUtils;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.Utils.Utils;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.DTOs.NewAnalysisDTO;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.DTOs.ProjectDTO;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.*;
import tasostilsi.uom.edu.gr.metricsCalculator.Repositories.JavaFilesRepository;
import tasostilsi.uom.edu.gr.metricsCalculator.Repositories.ProjectRepository;
import tasostilsi.uom.edu.gr.metricsCalculator.Repositories.QualityMetricsRepository;
import tasostilsi.uom.edu.gr.metricsCalculator.Services.Interfaces.IAnalysisService;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

@Service
public class AnalysisService implements IAnalysisService {
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(AnalysisService.class);
	
	private final ProjectRepository projectRepository;
	private final JavaFilesRepository javaFilesRepository;
	private final QualityMetricsRepository metricsRepository;
	
	@Autowired
	public AnalysisService(ProjectRepository projectRepository,
	                       JavaFilesRepository javaFilesRepository,
	                       QualityMetricsRepository metricsRepository) {
		this.projectRepository = projectRepository;
		this.javaFilesRepository = javaFilesRepository;
		this.metricsRepository = metricsRepository;
	}
	
	@Override
	public String startNewAnalysis(NewAnalysisDTO newAnalysisDTO) {
		final String[] returnString = {"The project with url: " + newAnalysisDTO.getGitUrl() + " analysis started"};
		
		Optional<Project> existsInDb = projectRepository.findByUrl(newAnalysisDTO.getGitUrl());
		
		Project project = existsInDb.orElseGet(() -> new Project(newAnalysisDTO.getGitUrl()));
		
		if (!Objects.equals(project.getState(), State.RUNNING.name())) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						returnString[0] = backgroundAnalysis(newAnalysisDTO);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}).start();
			
		} else {
			return "Project " + newAnalysisDTO.getGitUrl() + " is analyzing currently!";
		}
		
		return returnString[0];
	}
	
	private String backgroundAnalysis(NewAnalysisDTO newAnalysisDTO) throws Exception {
		Project project;
		String accessToken = newAnalysisDTO.getAccessToken();
		
		Optional<Project> existsInDb = projectRepository.findByUrl(newAnalysisDTO.getGitUrl());
		
		project = existsInDb.orElseGet(() -> new Project(newAnalysisDTO.getGitUrl()));
		if (!Objects.equals(project.getState(), State.RUNNING.name())) {
			project.setState(State.RUNNING.name());
			
			LOGGER.info("Project : {} State: {}", project.getUrl(), project.getState());
			
			LOGGER.info("Cloning repository from: " + project.getRepo() + " into " + project.getClonePath());
			Git git = GitUtils.getInstance().cloneRepository(project, accessToken);
			
			List<String> diffCommitIds = new ArrayList<>();
			List<String> commitIds = GitUtils.getInstance().getCommitIds(git);
			
			if (commitIds.isEmpty()) {
				LOGGER.error("No commits to analyze, or something is wrong with git url!\n" +
						"Please review the git url you provided.\n" +
						"If this repo is private please provide access token!");
				FileSystemUtils.deleteRecursively(new File(project.getClonePath()));
				throw new RuntimeException("No commits to analyze, or something is wrong with git url!\n" +
						"Please review the git url you provided.\n" +
						"If this repo is private please provide access token!");
			}
			projectRepository.save(project);
			
			try {
				Collections.reverse(commitIds);
			} catch (Exception e) {
				LOGGER.error("CommitIds List might be null");
				e.printStackTrace();
			}
			
			int start = 0;
			Revision currentRevision = new Revision("", 0L);
			
			diffCommitIds = isThereAnyNewCommit(project, existsInDb, diffCommitIds, commitIds, currentRevision);
			
			if (existsInDb.isEmpty() || new HashSet<>(diffCommitIds).containsAll(commitIds)) {
				start = 1;
				project = analyzeFirstDifferentCommit(project, accessToken, git, commitIds, currentRevision);
			} else {
				Long projectId = projectRepository.getIdByUrl(project.getUrl()).orElseThrow();
				Globals.getJavaFiles().addAll(javaFilesRepository.getAllByProjectId(projectId).orElseThrow());
				commitIds = new ArrayList<>(diffCommitIds);
				start = currentRevision.getCount().intValue();
			}
			
			project = loopThroughCommitsAndGetMetrics(project, accessToken, git, commitIds, start, currentRevision);
			
			LOGGER.info("Finished analysing {} revisions.\n", Objects.requireNonNull(currentRevision).getCount());
			project.setState(State.COMPLETED.name());
			projectRepository.save(project);
			return "Finished analysing " + currentRevision.getCount() + " revisions for " + project.getUrl() + ".";
		} else {
			return "Project " + newAnalysisDTO.getGitUrl() + " is analyzing currently!";
		}
	}
	
	private Project loopThroughCommitsAndGetMetrics(Project project, String accessToken, Git git, List<String> commitIds, int start, Revision currentRevision) throws GitAPIException {
		for (int i = start; i < commitIds.size(); ++i) {
			Objects.requireNonNull(currentRevision).setSha(commitIds.get(i));
			currentRevision.setCount(currentRevision.getCount() + 1L);
			GitUtils.getInstance().checkout(Objects.requireNonNull(project), accessToken, Objects.requireNonNull(currentRevision), Objects.requireNonNull(git));
			LOGGER.info("Calculating metrics for commit {} ({})...\n", currentRevision.getSha(), currentRevision.getCount());
			try {
				PrincipalResponseEntity[] responseEntities = GitUtils.getInstance().getResponseEntitiesAtCommit(git, currentRevision.getSha());
				if (Objects.isNull(responseEntities) || responseEntities.length == 0) {
					projectRepository.save(project);
					LOGGER.info("Calculated metrics for all files in {},{} revision", currentRevision.getCount(), currentRevision.getSha());
					continue;
				}
				LOGGER.info("Analyzing new/modified commit files...");
				project = Utils.getInstance().setMetrics(project, currentRevision, responseEntities[0]);
				LOGGER.info("Calculated metrics for all files in {},{} revision", currentRevision.getCount(), currentRevision.getSha());
				projectRepository.save(project);
			} catch (Exception ignored) {
				project.setState(State.ABORTED.name());
				projectRepository.save(project);
				throw new IllegalStateException("Project analysis is interrupted at revision count " + currentRevision.getCount());
			}
		}
		return project;
	}
	
	@Nullable
	private Project analyzeFirstDifferentCommit(Project project, String accessToken, Git git, List<String> commitIds, Revision currentRevision) throws GitAPIException {
		Objects.requireNonNull(currentRevision).setSha(Objects.requireNonNull(commitIds.get(0)));
		Objects.requireNonNull(currentRevision).setCount(currentRevision.getCount() + 1L);
		GitUtils.getInstance().checkout(project, accessToken, currentRevision, Objects.requireNonNull(git));
		LOGGER.info("Calculating metrics for commit {} ({})...\n", currentRevision.getSha(), currentRevision.getCount());
		try {
			project = Utils.getInstance().setMetrics(project, currentRevision);
			LOGGER.info("Calculated metrics for all files from first commit!");
			projectRepository.save(project);
		} catch (Exception e) {
			LOGGER.warn("This commit has no source roots to analyze, continuing!!!");
			e.printStackTrace();
		}
		return project;
	}
	
	private List<String> isThereAnyNewCommit(Project project, Optional<Project> existsInDb, List<String> diffCommitIds, List<String> commitIds, Revision currentRevision) {
		if (existsInDb.isPresent()) {
			LOGGER.info("Project EXISTS IN DB");
			
			Long projectId = projectRepository.getIdByUrl(project.getUrl()).orElseThrow();
			
			List<String> existingCommitIds = javaFilesRepository.getDistinctRevisionShaByProjectId(projectId);
			diffCommitIds = GitUtils.getInstance().findDifferenceInCommitIds(commitIds, existingCommitIds);
			if (!diffCommitIds.isEmpty()) {
				List<Revision> revisionFromDb = javaFilesRepository.getRevisionByProjectIdOrderByRevisionCountDesc(projectId).orElseThrow();
				currentRevision.setSha(revisionFromDb.get(0).getSha());
				currentRevision.setCount(revisionFromDb.get(0).getCount());
			} else {
				project.setState(State.ABORTED.name());
				projectRepository.save(project);
				throw new IllegalStateException("No Different Commits exist or something is wrong with git revision");
			}
		}
		return diffCommitIds;
	}
	
	@Override
	public Collection<CumulativeInterest> findCumulativeInterestPerCommit(String url) {
		return metricsRepository.findCumulativeInterestPerCommit(new ProjectDTO(url));
	}
	
	@Override
	public Collection<CumulativeInterest> findCumulativeInterestByCommit(String url, String sha) {
		return metricsRepository.findCumulativeInterestByCommit(new ProjectDTO(url), sha);
	}
	
	@Override
	public Collection<InterestPerCommitFile> findInterestByCommitFile(String url, String sha, String filePath) {
		return metricsRepository.findInterestPerCommitFile(new ProjectDTO(url), sha, filePath);
	}
	
	@Override
	public Collection<InterestChange> findInterestChangeByCommit(String url, String sha) {
		if (metricsRepository.findDistinctRevisionCountByRevisionSha(sha) <= 3) {
			throw new IllegalStateException("Please choose a revision sha that has revision count more than 3!");
		}
		return metricsRepository.findInterestChangeByCommit(new ProjectDTO(url), sha);
	}
	
	@Override
	public FileInterestChange findInterestChangeByCommitAndFile(String url, String sha, String filePath) {
		return metricsRepository.findInterestChangeByCommitAndFile(new ProjectDTO(url), sha, filePath);
	}
	
	@Override
	public Collection<NormalizedInterest> findNormalizedInterest(String url) {
		return metricsRepository.findNormalizedInterest(new ProjectDTO(url));
	}
	
	@Override
	public Collection<NormalizedInterest> findNormalizedInterestByCommit(String url, String sha) {
		return metricsRepository.findNormalizedInterestByCommit(new ProjectDTO(url), sha);
	}
	
	
	@Override
	public Slice<HighInterestFile> findHighInterestFiles(Pageable pageable, String url, String sha) {
		return metricsRepository.findHighInterestFiles(pageable, new ProjectDTO(url), sha);
	}
	
	@Override
	public Slice<ProjectReusabilityMetrics> findReusabilityMetrics(Pageable pageable, String url) {
		return metricsRepository.findReusabilityMetrics(pageable, new ProjectDTO(url));
	}
	
	@Override
	public Slice<FileReusabilityMetrics> findReusabilityMetrics(Pageable pageable, String url, String sha) {
		return metricsRepository.findReusabilityMetrics(pageable, new ProjectDTO(url), sha);
	}
	
	@Override
	public Slice<FileReusabilityMetrics> findReusabilityMetrics(Pageable pageable, String url, String sha, String filePath) {
		return metricsRepository.findReusabilityMetrics(pageable, new ProjectDTO(url), sha, filePath);
	}
	
	@Override
	public Slice<AnalyzedCommit> findAnalyzedCommits(Pageable pageable, String url) {
		return metricsRepository.findAnalyzedCommits(pageable, new ProjectDTO(url));
	}
}
