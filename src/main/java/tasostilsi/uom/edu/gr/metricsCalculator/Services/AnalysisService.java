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

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
	
	// Updated code using ExecutorService and Callable interface
	@Transactional
	@Override
	public String startNewAnalysis(NewAnalysisDTO newAnalysisDTO) throws Exception {
		if (newAnalysisDTO == null || newAnalysisDTO.getGitUrl() == null || newAnalysisDTO.getGitUrl().isEmpty()) {
			throw new IllegalArgumentException("Invalid input");
		}
		String url = newAnalysisDTO.getGitUrl();
		Project project = projectRepository.findByUrl(url).orElseGet(() -> new Project(url));
		if (Objects.equals(project.getState(), State.RUNNING.name())) {
			return "Project " + url + " is analyzing currently!";
		}
		BackroundAnalysis backgroundAnalysis = new BackroundAnalysis(projectRepository, javaFilesRepository, newAnalysisDTO, project);
		ExecutorService executorService = Executors.newCachedThreadPool();
		executorService.submit(backgroundAnalysis);
		executorService.shutdown();
		return "The project with url: " + url + " analysis started";
	}
	
	@Override
	public Collection<CumulativeInterest> findInterestForAllCommits(String url) {
		return metricsRepository.findInterestForAllCommits(new ProjectDTO(url));
	}
	
	@Override
	public Collection<CumulativeInterest> findInterestByCommit(String url, String sha) {
		return metricsRepository.findInterestByCommit(new ProjectDTO(url), sha);
	}
	
	/*@Override
	public Collection<CumulativeInterest> findCumulativeInterestPerCommit(String url) {
		Collection<CumulativeInterest> cumulativeInterests = metricsRepository.findInterestForAllCommits(new ProjectDTO(url));
		AtomicReference<BigDecimal> cumulativeInterestEu = new AtomicReference<>(BigDecimal.ZERO);
		AtomicReference<BigDecimal> cumulativeInterestHours = new AtomicReference<>(BigDecimal.ZERO);
		cumulativeInterests.forEach(interest -> {
			cumulativeInterestEu.set(cumulativeInterestEu.get().add(interest.getInterestEu()));
			cumulativeInterestHours.set(cumulativeInterestHours.get().add(interest.getInterestHours()));
			interest.setInterestEu(cumulativeInterestEu.get());
			interest.setInterestHours(cumulativeInterestHours.get());
		});
		
		return cumulativeInterests;
	}*/
	
	@Override
	public Collection<CumulativeInterest> findCumulativeInterestPerCommit(String url) throws IllegalArgumentException {
		if (url == null || url.isEmpty()) {
			throw new IllegalArgumentException("URL cannot be null or empty");
		}
		Collection<CumulativeInterest> cumulativeInterests = metricsRepository.findInterestForAllCommits(new ProjectDTO(url));
		if (cumulativeInterests == null || cumulativeInterests.isEmpty()) {
			return Collections.emptyList();
		}
		BigDecimal cumulativeInterestEu = BigDecimal.valueOf(0);
		BigDecimal cumulativeInterestHours = BigDecimal.valueOf(0);
		Collection<CumulativeInterest> updatedInterests = new ArrayList<>();
		for (CumulativeInterest interest : cumulativeInterests) {
			cumulativeInterestEu = cumulativeInterestEu.add(interest.getInterestEu());
			cumulativeInterestHours = cumulativeInterestHours.add(interest.getInterestHours());
			updatedInterests.add(new CumulativeInterest(interest.getRevisionCount(), cumulativeInterestEu, cumulativeInterestHours));
		}
		return updatedInterests;
	}
	
	@Override
	public Collection<CumulativeInterest> findCumulativeInterestByCommit(String url, String sha) {
		ProjectDTO projectDTO = new ProjectDTO(url);
		Collection<CumulativeInterest> cumulativeInterests = metricsRepository.findInterestForAllCommits(projectDTO);
		BigDecimal cumulativeInterestEu = BigDecimal.ZERO;
		BigDecimal cumulativeInterestHours = BigDecimal.ZERO;
		Collection<CumulativeInterest> returnValues = new ArrayList<>();
		long revisionCount = metricsRepository.findDistinctRevisionCountByRevisionSha(sha);
		for (CumulativeInterest interest : cumulativeInterests) {
			if (interest.getRevisionCount() <= revisionCount) {
				cumulativeInterestEu = cumulativeInterestEu.add(interest.getInterestEu());
				cumulativeInterestHours = cumulativeInterestHours.add(interest.getInterestHours());
				interest.setInterestEu(cumulativeInterestEu);
				interest.setInterestHours(cumulativeInterestHours);
				returnValues.add(interest);
			}
		}
		return returnValues.stream().sorted(Comparator.comparingLong(CumulativeInterest::getRevisionCount)).collect(Collectors.toList());
	}
	
	@Override
	public Collection<InterestPerCommitFile> findInterestByCommitFile(String url, String sha, String filePath) {
		boolean shaExists = Utils.getInstance().parameterExists(sha);
		boolean filePathExists = Utils.getInstance().parameterExists(filePath);
		if (shaExists && filePathExists) {
			return metricsRepository.findInterestPerCommitFile(new ProjectDTO(url), sha, filePath);
		}
		if (shaExists) {
			Long revisionCount = metricsRepository.findDistinctRevisionCountByRevisionSha(sha);
			return metricsRepository.findInterestPerCommitFile(new ProjectDTO(url), revisionCount);
		}
		return metricsRepository.findInterestPerCommitFile(new ProjectDTO(url));
	}
	
	@Override
	public Collection<InterestChange> findInterestChangeByCommit(String url, String sha) {
		Collection<InterestChange> returnCollection = new ArrayList<>();
		Long commitCount = metricsRepository.findDistinctRevisionCountByRevisionSha(sha);
		if (commitCount <= 3) {
			LOGGER.warn("Commits under 3 counts, cannot be calculated, because there can not exist a divide with 0.\n" +
					"Please choose a revision sha that has revision count more than 3!");
			returnCollection.add(new InterestChange(commitCount, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
			return returnCollection;
		}
		try {
			return metricsRepository.findInterestChangeByCommit(new ProjectDTO(url), sha);
		} catch (Exception ignored) {
			returnCollection.add(new InterestChange(commitCount, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
			return returnCollection;
		}
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
	public Collection<NormalizedInterestPerFile> findNormalizedInterestPerFile(String url) {
		return metricsRepository.findNormalizedInterestPerFile(new ProjectDTO(url));
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
	
	@Override
	public Collection<InterestChange> findInterestChangeForAllCommits(String url) {
		ProjectDTO project = new ProjectDTO(url);
		Collection<InterestChange> returnedValue = new ArrayList<>();
		Slice<AnalyzedCommit> analyzedCommits = metricsRepository.findAnalyzedCommits(null, project);
		
		for (AnalyzedCommit commit : analyzedCommits) {
			if (commit.getRevisionCount() > 3) {
				try {
					InterestChange change = metricsRepository.findInterestChangeByCommit(project, commit.getSha()).stream().findFirst().get();
					if (change.getChangeEu() != null) {
						returnedValue.add(change);
					} else {
						returnedValue.add(new InterestChange(commit.getRevisionCount(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
					}
				} catch (Exception ignored) {
					returnedValue.add(new InterestChange(commit.getRevisionCount(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
				}
			} else {
				returnedValue.add(new InterestChange(commit.getRevisionCount(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
			}
		}
		
		return returnedValue;
	}
	
	@Override
	public float findInterestRanking(String url) {
		float returnValue = 0f;
		
		List<Project> projects = projectRepository.findAll();
		
		float totalInterestForAllProjects = 0f;
		float totalInterestForCalculatedProject = 0f;
		
		for (Project project : projects) {
			float dbInterest = metricsRepository.findInterestForAllCommits(new ProjectDTO(project.getUrl())).parallelStream().map(CumulativeInterest::getInterestEu).reduce(BigDecimal.valueOf(0), BigDecimal::add).floatValue();
			totalInterestForAllProjects += dbInterest;
//			System.out.println(project.getUrl() + " --> " + dbInterest);
			if (project.getUrl().equals(url)) {
				totalInterestForCalculatedProject = dbInterest;
			}
		}
		if (totalInterestForAllProjects != (float) 0) {
			returnValue = (totalInterestForCalculatedProject / totalInterestForAllProjects) * 100;
		}
		
		return returnValue;
	}
	
	@Override
	public Collection<NormalizedAndInterestChanges> findNormalizedAndInterestChangesByCommit(String url, String sha) {
		Collection<NormalizedAndInterestChanges> returnCollection = new ArrayList<>();
		Long commitCount = metricsRepository.findDistinctRevisionCountByRevisionSha(sha);
		if (commitCount <= 3) {
			LOGGER.warn("Commits under 3 counts, cannot be calculated, because there can not exist a divide with 0.\n" +
					"Please choose a revision sha that has revision count more than 3!");
			Collection<NormalizedInterest> normalizedInterests = metricsRepository.findNormalizedInterestByCommit(new ProjectDTO(url), sha);
			returnCollection.add(new NormalizedAndInterestChanges(commitCount,
					BigDecimal.ZERO,
					BigDecimal.ZERO,
					BigDecimal.ZERO,
					normalizedInterests.iterator().next().getNormalizedInterestEu(),
					normalizedInterests.iterator().next().getNormalizedInterestHours()));
			return returnCollection;
		}
		Collection<InterestChange> interestChanges;
		Collection<NormalizedInterest> normalizedInterests;
		try {
			interestChanges = metricsRepository.findInterestChangeByCommit(new ProjectDTO(url), sha);
			normalizedInterests = metricsRepository.findNormalizedInterestByCommit(new ProjectDTO(url), sha);
		} catch (Exception e) {
			LOGGER.error("An error occurred while retrieving interest changes and normalized interests for commit with sha " + sha, e);
			normalizedInterests = metricsRepository.findNormalizedInterestByCommit(new ProjectDTO(url), sha);
			returnCollection.add(new NormalizedAndInterestChanges(commitCount,
					BigDecimal.ZERO,
					BigDecimal.ZERO,
					BigDecimal.ZERO,
					normalizedInterests.iterator().next().getNormalizedInterestEu(),
					normalizedInterests.iterator().next().getNormalizedInterestHours()));
			return returnCollection;
		}
		returnCollection.add(new NormalizedAndInterestChanges(commitCount,
				interestChanges.iterator().next().getChangeEu(),
				interestChanges.iterator().next().getChangeHours(),
				interestChanges.iterator().next().getChangePercentage(),
				normalizedInterests.iterator().next().getNormalizedInterestEu(),
				normalizedInterests.iterator().next().getNormalizedInterestHours()));
		return returnCollection;
	}
	
	@Override
	public Collection<NormalizedAndInterestChanges> findNormalizedAndInterestChangeForAllCommits(String url) {
		ProjectDTO project = new ProjectDTO(url);
		Collection<NormalizedAndInterestChanges> returnedValue = new ArrayList<>();
		Slice<AnalyzedCommit> analyzedCommits = metricsRepository.findAnalyzedCommits(null, project);
		
		for (AnalyzedCommit commit : analyzedCommits) {
			NormalizedInterest normalized;
			try {
				normalized = metricsRepository.findNormalizedInterestByCommit(project, commit.getSha()).stream().findFirst().get();
			} catch (Exception e) {
				normalized = new NormalizedInterest(commit.getRevisionCount(), BigDecimal.ZERO, BigDecimal.ZERO);
			}
			if (commit.getRevisionCount() > 3) {
				InterestChange change;
				try {
					change = metricsRepository.findInterestChangeByCommit(project, commit.getSha()).stream().findFirst().get();
				} catch (Exception e) {
					change = new InterestChange(commit.getRevisionCount(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
				}
				
				if (change.getChangeEu() != null) {
					returnedValue.add(new NormalizedAndInterestChanges(commit.getRevisionCount(),
							change.getChangeEu(),
							change.getChangeHours(),
							change.getChangePercentage(),
							normalized.getNormalizedInterestEu(),
							normalized.getNormalizedInterestHours()));
				} else {
					returnedValue.add(new NormalizedAndInterestChanges(commit.getRevisionCount(),
							BigDecimal.ZERO,
							BigDecimal.ZERO,
							BigDecimal.ZERO,
							normalized.getNormalizedInterestEu(),
							normalized.getNormalizedInterestHours()));
				}
			} else {
				returnedValue.add(new NormalizedAndInterestChanges(commit.getRevisionCount(),
						BigDecimal.ZERO,
						BigDecimal.ZERO,
						BigDecimal.ZERO,
						normalized.getNormalizedInterestEu(),
						normalized.getNormalizedInterestHours()));
			}
		}
		
		return returnedValue;
	}
}
