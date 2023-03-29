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
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.BackroundAnalysis;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.Enums.State;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.Project;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.Utils.Utils;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.DTOs.NewAnalysisDTO;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.DTOs.ProjectDTO;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.*;
import tasostilsi.uom.edu.gr.metricsCalculator.Repositories.JavaFilesRepository;
import tasostilsi.uom.edu.gr.metricsCalculator.Repositories.ProjectRepository;
import tasostilsi.uom.edu.gr.metricsCalculator.Repositories.QualityMetricsRepository;
import tasostilsi.uom.edu.gr.metricsCalculator.Services.Interfaces.IAnalysisService;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Transactional
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
	
	@Transactional
	@Override
	public String startNewAnalysis(NewAnalysisDTO newAnalysisDTO) throws Exception {
		String returnString = "The project with url: " + newAnalysisDTO.getGitUrl() + " analysis started";
		
		Optional<Project> existsInDb = projectRepository.findByUrl(newAnalysisDTO.getGitUrl());
		
		Project project = existsInDb.orElseGet(() -> new Project(newAnalysisDTO.getGitUrl()));
		
		if (!Objects.equals(project.getState(), State.RUNNING.name())) {
			
			new Thread(new BackroundAnalysis(projectRepository, javaFilesRepository, newAnalysisDTO, project)).start();
			
		} else {
			return "Project " + newAnalysisDTO.getGitUrl() + " is analyzing currently!";
		}
		
		return returnString;
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
		boolean shaExists = Utils.getInstance().parameterExists(sha);
		boolean filePathExists = Utils.getInstance().parameterExists(filePath);
		if (shaExists && filePathExists) {
			return metricsRepository.findInterestPerCommitFile(new ProjectDTO(url), sha, filePath);
		}
		return metricsRepository.findInterestPerCommitFile(new ProjectDTO(url));
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
	public Collection<FileInterestChange> findInterestChange(String url) {
		return metricsRepository.findInterestChange(new ProjectDTO(url));
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
		boolean shaExists = Utils.getInstance().parameterExists(sha);
		if (shaExists) {
			return metricsRepository.findHighInterestFiles(pageable, new ProjectDTO(url), sha);
		}
		return metricsRepository.findHighInterestFiles(pageable, new ProjectDTO(url));
	}
	
	public Slice<ProjectReusabilityMetrics> findProjectReusabilityMetrics(Pageable pageable, String url) {
		return metricsRepository.findProjectReusabilityMetrics(pageable, new ProjectDTO(url));
	}
	
	@Override
	public Slice<FileReusabilityMetrics> findFileReusabilityMetrics(Pageable pageable, String url, String sha) {
		return metricsRepository.findFileReusabilityMetrics(pageable, new ProjectDTO(url), sha);
	}
	
	@Override
	public Slice<FileReusabilityMetrics> findFileReusabilityMetrics(Pageable pageable, String url, String sha, String filePath) {
		return metricsRepository.findFileReusabilityMetrics(pageable, new ProjectDTO(url), sha, filePath);
	}
	
	@Override
	public Slice<FileReusabilityMetrics> findFileReusabilityMetrics(Pageable pageable, String url) {
		return metricsRepository.findFileReusabilityMetrics(pageable, new ProjectDTO(url));
	}
	
	@Override
	public Slice<AnalyzedCommit> findAnalyzedCommits(Pageable pageable, String url) {
		return metricsRepository.findAnalyzedCommits(pageable, new ProjectDTO(url));
	}
	
	@Override
	public Slice<AllFileMetricsAndInterest> findAllFileMetricsAndInterest(Pageable pageable, String url, String sha) {
		boolean shaExists = Utils.getInstance().parameterExists(sha);
		if (shaExists) {
			Long revisionCount = metricsRepository.findDistinctRevisionCountByRevisionSha(sha);
			return metricsRepository.findAllFileMetricsAndInterest(pageable, new ProjectDTO(url), revisionCount);
		}
		return metricsRepository.findAllFileMetricsAndInterest(pageable, new ProjectDTO(url));
	}
}
