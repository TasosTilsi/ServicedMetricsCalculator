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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.DTOs.NewAnalysisDTO;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.CumulativeInterest;
import tasostilsi.uom.edu.gr.metricsCalculator.Services.AnalysisService;

import java.util.Collection;
import java.util.Objects;

@RestController
@RequestMapping(path = "/api/analysis")
public class AnalysisController {
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(AnalysisService.class);
	@Autowired
	private AnalysisService analysisService;
	
	@PostMapping
	public ResponseEntity<String> makeNewAnalysis(@RequestBody NewAnalysisDTO newAnalysisDTO) throws Exception {
		LOGGER.info("New Analysis requested for " + newAnalysisDTO.getGitUrl() + " completed with " + newAnalysisDTO.getAccessToken() + " access token");
		String response = analysisService.startNewAnalysis(newAnalysisDTO);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@CrossOrigin(origins = "*")
	@GetMapping(value = "/cumulativeInterest")
	Collection<CumulativeInterest> getCumulativeInterestPerCommit(@RequestParam(required = true) String url, @RequestParam(required = false) String sha) {
		if (Objects.isNull(sha))
			return analysisService.findCumulativeInterestPerCommit(url);
		return analysisService.findCumulativeInterestByCommit(url, sha);
	}
	
	/*
	@GetMapping(value = "/interestPerCommitFile")
	Collection<InterestPerCommitFile> getInterestPerCommitFile(@RequestParam(required = true) String url, @RequestParam(required = true) String sha, @RequestParam(required = true) String filePath) {
		return analysisService.findInterestByCommitFile(url, sha, filePath);
	}
	
	@GetMapping(value = "/interestChange")
	Collection<InterestChange> getLastCommitInterestChange(@RequestParam(required = true) String url, @RequestParam(required = true) String sha) {
		return analysisService.findInterestChangeByCommit(url, sha);
	}
	
	@GetMapping(value = "/fileInterestChange")
	FileInterestChange getFileInterestChange(@RequestParam(required = true) String url, @RequestParam(required = true) String sha, @RequestParam(required = true) String filePath) {
		return analysisService.findInterestChangeByCommitAndFile(url, sha, filePath);
	}
	
	@GetMapping(value = "/normalizedInterest")
	Collection<NormalizedInterest> getNormalizedInterest(@RequestParam(required = true) String url, @RequestParam(required = false) String sha) {
		return (Objects.isNull(sha)) ? analysisService.findNormalizedInterest(url) : analysisService.findNormalizedInterestByCommit(url, sha);
	}
	
	@GetMapping(value = "/highInterestFiles")
	Collection<HighInterestFile> getHighInterestFiles(@RequestParam(required = true) String url, @RequestParam(required = true) String sha, @RequestParam(required = false) Integer limit) {
		return Objects.isNull(limit) ? analysisService.findHighInterestFiles(null, url, sha).getContent() : analysisService.findHighInterestFiles(PageRequest.of(0, limit), url, sha).getContent();
	}
	
	@GetMapping(value = "/reusabilityMetrics")
	Collection<ProjectReusabilityMetrics> getReusabilityMetrics(@RequestParam(required = true) String url, @RequestParam(required = false) Integer limit) {
		return Objects.isNull(limit) ? analysisService.findReusabilityMetrics(null, url).getContent() : analysisService.findReusabilityMetrics(PageRequest.of(0, limit), url).getContent();
	}
	
	@GetMapping(value = "/reusabilityMetricsByCommit")
	Collection<FileReusabilityMetrics> getReusabilityMetricsByCommit(@RequestParam(required = true) String url, @RequestParam(required = true) String sha, @RequestParam(required = false) Integer limit) {
		return Objects.isNull(limit) ? analysisService.findReusabilityMetrics(null, url, sha).getContent() : analysisService.findReusabilityMetrics(PageRequest.of(0, limit), url, sha).getContent();
	}
	
	@GetMapping(value = "/reusabilityMetricsByCommitAndFile")
	Collection<FileReusabilityMetrics> getReusabilityMetricsByCommitAndFile(@RequestParam(required = true) String url, @RequestParam(required = true) String sha, @RequestParam(required = true) String filePath, @RequestParam(required = false) Integer limit) {
		return Objects.isNull(limit) ? analysisService.findReusabilityMetrics(null, url, sha, filePath).getContent() : analysisService.findReusabilityMetrics(PageRequest.of(0, limit), url, sha, filePath).getContent();
	}
	
	@GetMapping(value = "/analyzedCommits")
	Collection<AnalyzedCommit> getAnalyzedCommitIds(@RequestParam(required = true) String url, @RequestParam(required = false) Integer limit) {
		return Objects.isNull(limit) ? analysisService.findAnalyzedCommits(null, url).getContent() : analysisService.findAnalyzedCommits(PageRequest.of(0, limit), url).getContent();
	}*/
}
