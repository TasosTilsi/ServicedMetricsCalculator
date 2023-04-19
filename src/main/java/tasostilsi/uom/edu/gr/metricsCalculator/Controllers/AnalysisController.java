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

package tasostilsi.uom.edu.gr.metricsCalculator.Controllers;


import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.DTOs.NewAnalysisDTO;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.*;
import tasostilsi.uom.edu.gr.metricsCalculator.Services.AnalysisService;

import java.util.Collection;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/api/analysis")
public class AnalysisController {
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(AnalysisService.class);
	@Autowired
	private AnalysisService analysisService;
	
	@PostMapping
	public ResponseEntity<String> makeNewAnalysis(@RequestBody NewAnalysisDTO newAnalysisDTO) throws Exception {
		LOGGER.info("New Analysis requested for " + newAnalysisDTO.getGitUrl() + " with user: " + newAnalysisDTO.getUser());
		String response = analysisService.startNewAnalysis(newAnalysisDTO);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(value = "/cumulativeInterest")
	public ResponseEntity<Collection<CumulativeInterest>> getCumulativeInterestPerCommit(@RequestParam(required = true) String url, @RequestParam(required = false) String sha) {
		Collection<CumulativeInterest> response;
		if (Objects.isNull(sha))
			response = analysisService.findCumulativeInterestPerCommit(url);
		else
			response = analysisService.findCumulativeInterestByCommit(url, sha);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(value = "/interestPerCommitFile")
	public ResponseEntity<Collection<InterestPerCommitFile>> getInterestPerCommitFile(@RequestParam(required = true) String url, @RequestParam(required = false) String sha, @RequestParam(required = false) String filePath) {
		Collection<InterestPerCommitFile> response = analysisService.findInterestByCommitFile(url, sha, filePath);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(value = "/interestChange")
	public ResponseEntity<Collection<InterestChange>> getLastCommitInterestChange(@RequestParam(required = true) String url, @RequestParam(required = false) String sha) {
		Collection<InterestChange> response;
		if (!Objects.isNull(sha)) {
			response = analysisService.findInterestChangeByCommit(url, sha);
		} else {
			response = analysisService.findTotalInterestChange(url);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(value = "/fileInterestChangeByCommitAndFile")
	public ResponseEntity<FileInterestChange> getFileInterestChangeByCommitAndFile(@RequestParam(required = true) String url, @RequestParam(required = true) String sha, @RequestParam(required = true) String filePath) {
		FileInterestChange response = analysisService.findInterestChangeByCommitAndFile(url, sha, filePath);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(value = "/fileInterestChange")
	public ResponseEntity<Collection<FileInterestChange>> getFileInterestChange(@RequestParam(required = true) String url) {
		Collection<FileInterestChange> response = analysisService.findInterestChange(url);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(value = "/normalizedInterest")
	public ResponseEntity<Collection<NormalizedInterest>> getNormalizedInterest(@RequestParam(required = true) String url, @RequestParam(required = false) String sha) {
		Collection<NormalizedInterest> response;
		if (Objects.isNull(sha))
			response = analysisService.findNormalizedInterest(url);
		else
			response = analysisService.findNormalizedInterestByCommit(url, sha);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(value = "/highInterestFiles")
	ResponseEntity<Collection<HighInterestFile>> getHighInterestFiles(@RequestParam(required = true) String url, @RequestParam(required = false) String sha, @RequestParam(required = false) Integer limit) {
		Collection<HighInterestFile> response;
		if (Objects.isNull(limit))
			response = analysisService.findHighInterestFiles(null, url, sha).getContent();
		else
			response = analysisService.findHighInterestFiles(PageRequest.of(0, limit), url, sha).getContent();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(value = "/projectReusabilityMetrics")
	ResponseEntity<Collection<ProjectReusabilityMetrics>> getProjectReusabilityMetrics(@RequestParam(required = true) String url, @RequestParam(required = false) Integer limit) {
		Collection<ProjectReusabilityMetrics> response;
		if (Objects.isNull(limit))
			response = analysisService.findProjectReusabilityMetrics(null, url).getContent();
		else
			response = analysisService.findProjectReusabilityMetrics(PageRequest.of(0, limit), url).getContent();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(value = "/fileReusabilityMetrics")
	ResponseEntity<Collection<FileReusabilityMetrics>> getFileReusabilityMetricsByCommit(@RequestParam(required = true) String url, @RequestParam(required = false) Integer limit) {
		Collection<FileReusabilityMetrics> response;
		if (Objects.isNull(limit))
			response = analysisService.findFileReusabilityMetrics(null, url).getContent();
		else
			response = analysisService.findFileReusabilityMetrics(PageRequest.of(0, limit), url).getContent();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(value = "/fileReusabilityMetricsByCommit")
	ResponseEntity<Collection<FileReusabilityMetrics>> getReusabilityMetricsByCommit(@RequestParam(required = true) String url, @RequestParam(required = true) String sha, @RequestParam(required = false) Integer limit) {
		Collection<FileReusabilityMetrics> response;
		if (Objects.isNull(limit))
			response = analysisService.findFileReusabilityMetrics(null, url, sha).getContent();
		else
			response = analysisService.findFileReusabilityMetrics(PageRequest.of(0, limit), url, sha).getContent();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(value = "/fileReusabilityMetricsByCommitAndFile")
	ResponseEntity<Collection<FileReusabilityMetrics>> getReusabilityMetricsByCommitAndFile(@RequestParam(required = true) String url, @RequestParam(required = true) String sha, @RequestParam(required = true) String filePath, @RequestParam(required = false) Integer limit) {
		Collection<FileReusabilityMetrics> response;
		if (Objects.isNull(limit))
			response = analysisService.findFileReusabilityMetrics(null, url, sha, filePath).getContent();
		else
			response = analysisService.findFileReusabilityMetrics(PageRequest.of(0, limit), url, sha, filePath).getContent();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(value = "/analyzedCommits")
	ResponseEntity<Collection<AnalyzedCommit>> getAnalyzedCommitIds(@RequestParam(required = true) String url, @RequestParam(required = false) Integer limit) {
		Collection<AnalyzedCommit> response;
		if (Objects.isNull(limit))
			response = analysisService.findAnalyzedCommits(null, url).getContent();
		else
			response = analysisService.findAnalyzedCommits(PageRequest.of(0, limit), url).getContent();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(value = "/allFileMetricsAndInterest")
	ResponseEntity<Collection<AllFileMetricsAndInterest>> findAllFileMetricsAndInterest(@RequestParam(required = true) String url, @RequestParam(required = false) String sha, @RequestParam(required = false) Integer limit) {
		Collection<AllFileMetricsAndInterest> response;
		if (Objects.isNull(limit))
			response = analysisService.findAllFileMetricsAndInterest(null, url, sha).getContent();
		else
			response = analysisService.findAllFileMetricsAndInterest(PageRequest.of(0, limit), url, sha).getContent();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(value = "/interest")
	public ResponseEntity<Collection<CumulativeInterest>> getInterestPerCommit(@RequestParam(required = true) String url, @RequestParam(required = false) String sha) {
		Collection<CumulativeInterest> response;
		if (Objects.isNull(sha))
			response = analysisService.findInterestPerCommit(url);
		else
			response = analysisService.findInterestByCommit(url, sha);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
