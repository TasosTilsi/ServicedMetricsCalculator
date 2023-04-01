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

package tasostilsi.uom.edu.gr.metricsCalculator.Helpers;

import ch.qos.logback.classic.Logger;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileSystemUtils;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.Enums.State;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.Project;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.Globals;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.PrincipalResponseEntity;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.Revision;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.Utils.GitUtils;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.Utils.Utils;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.DTOs.GitUserCredentialsDTO;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.DTOs.NewAnalysisDTO;
import tasostilsi.uom.edu.gr.metricsCalculator.Repositories.JavaFilesRepository;
import tasostilsi.uom.edu.gr.metricsCalculator.Repositories.ProjectRepository;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;


@AllArgsConstructor
@NoArgsConstructor
public class BackroundAnalysis implements Runnable {
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(BackroundAnalysis.class);
	
	private ProjectRepository projectRepository;
	private JavaFilesRepository javaFilesRepository;
	
	private NewAnalysisDTO newAnalysisDTO;
	private Project project;
	
	
	@Override
	public void run() {
		
		Optional<Project> existsInDb = projectRepository.findByUrl(newAnalysisDTO.getGitUrl());
		
		project.setState(State.RUNNING.name());
		project = projectRepository.save(project);
		
		LOGGER.info("Project : {} State: {}", project.getUrl(), project.getState());
		
		LOGGER.info("Cloning repository from: " + project.getRepo() + " into " + project.getClonePath());
		Git git = GitUtils.getInstance().cloneRepository(project, newAnalysisDTO.getUser());
		
		List<String> diffCommitIds = new ArrayList<>();
		List<String> commitIds = GitUtils.getInstance().getCommitIds(git);
		
		if (commitIds.isEmpty()) {
			LOGGER.error("No commits to analyze, or something is wrong with git url!\n" +
					"Please review the git url you provided.\n" +
					"If this repo is private please provide access token!");
			FileSystemUtils.deleteRecursively(new File(project.getClonePath()));
			project.setState(State.ABORTED.name());
			project = projectRepository.save(project);
			throw new RuntimeException("No commits to analyze, or something is wrong with git url!\n" +
					"Please review the git url you provided.\n" +
					"If this repo is private please provide access token!");
		}
		
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
			try {
				project = analyzeFirstDifferentCommit(project, newAnalysisDTO.getUser(), git, commitIds, currentRevision);
			} catch (GitAPIException e) {
				throw new RuntimeException(e);
			}
		} else {
			Long projectId = projectRepository.getIdByUrl(project.getUrl()).orElseThrow();
			Globals.getJavaFiles().addAll(javaFilesRepository.getAllByProjectId(projectId).orElseThrow());
			commitIds = new ArrayList<>(diffCommitIds);
			start = currentRevision.getCount().intValue();
		}
		
		try {
			project = loopThroughCommitsAndGetMetrics(project, newAnalysisDTO.getUser(), git, commitIds, start, currentRevision);
		} catch (GitAPIException e) {
			throw new RuntimeException(e);
		}
		
		LOGGER.info("Finished analysing {} revisions.\n", Objects.requireNonNull(currentRevision).getCount());
		project.setState(State.COMPLETED.name());
		project = projectRepository.save(project);
		LOGGER.info("Finished analysing " + currentRevision.getCount() + " revisions for " + project.getUrl() + ".");
		project = null;
	}
	
	private Project loopThroughCommitsAndGetMetrics(Project project, GitUserCredentialsDTO user, Git git, List<String> commitIds, int start, Revision currentRevision) throws GitAPIException {
		for (int i = start; i < commitIds.size(); ++i) {
			Objects.requireNonNull(currentRevision).setSha(commitIds.get(i));
			currentRevision.setCount(currentRevision.getCount() + 1L);
			GitUtils.getInstance().checkout(Objects.requireNonNull(project), user, Objects.requireNonNull(currentRevision), Objects.requireNonNull(git));
			LOGGER.info("Calculating metrics for commit {} ({})...\n", currentRevision.getSha(), currentRevision.getCount());
			try {
				PrincipalResponseEntity[] responseEntities = GitUtils.getInstance().getResponseEntitiesAtCommit(git, currentRevision.getSha());
				if (Objects.isNull(responseEntities) || responseEntities.length == 0) {
					project = projectRepository.save(project);
					LOGGER.info("Calculated metrics for all files in {},{} revision", currentRevision.getCount(), currentRevision.getSha());
					continue;
				}
				LOGGER.info("Analyzing new/modified commit files...");
				project = Utils.getInstance().setMetrics(project, currentRevision, responseEntities[0]);
				LOGGER.info("Calculated metrics for all files in {},{} revision", currentRevision.getCount(), currentRevision.getSha());
				project = projectRepository.save(project);
			} catch (Exception ignored) {
				project.setState(State.ABORTED.name());
				this.project = projectRepository.save(project);
				throw new IllegalStateException("Project analysis is interrupted at revision count " + currentRevision.getCount());
			}
		}
		return project;
	}
	
	@Nullable
	private Project analyzeFirstDifferentCommit(Project project, GitUserCredentialsDTO user, Git git, List<String> commitIds, Revision currentRevision) throws GitAPIException {
		Objects.requireNonNull(currentRevision).setSha(Objects.requireNonNull(commitIds.get(0)));
		Objects.requireNonNull(currentRevision).setCount(currentRevision.getCount() + 1L);
		GitUtils.getInstance().checkout(project, user, currentRevision, Objects.requireNonNull(git));
		LOGGER.info("Calculating metrics for commit {} ({})...\n", currentRevision.getSha(), currentRevision.getCount());
		try {
			project = Utils.getInstance().setMetrics(project, currentRevision);
			LOGGER.info("Calculated metrics for all files from first commit!");
			project = projectRepository.save(project);
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
				this.project = projectRepository.save(project);
				throw new IllegalStateException("No Different Commits exist or something is wrong with git revision");
			}
		}
		return diffCommitIds;
	}
}
