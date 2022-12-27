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

package tasostilsi.uom.edu.gr.metricsCalculator.Services.Interfaces;

import tasostilsi.uom.edu.gr.metricsCalculator.Models.DTOs.NewAnalysisDTO;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.*;

import java.util.Collection;

public interface IAnalysisService {
	
	String startNewAnalysis(NewAnalysisDTO newAnalysisDTO) throws Exception;
	
	Collection<CumulativeInterest> findCumulativeInterestPerCommit(String url);
	
	Collection<CumulativeInterest> findCumulativeInterestByCommit(String url, String sha);
	
	Collection<InterestPerCommitFile> findInterestByCommitFile(String url, String sha, String filePath);
	
	Collection<InterestChange> findInterestChangeByCommit(String url, String sha);
	
	FileInterestChange findInterestChangeByCommitAndFile(String url, String sha, String filePath);
	
	Collection<NormalizedInterest> findNormalizedInterest(String url);
	
	Collection<NormalizedInterest> findNormalizedInterestByCommit(String url, String sha);

	/*
	
	Slice<HighInterestFile> findHighInterestFiles(Pageable pageable, String url, String sha);
	
	Slice<ProjectReusabilityMetrics> findReusabilityMetrics(Pageable pageable, String url);
	
	Slice<FileReusabilityMetrics> findReusabilityMetrics(Pageable pageable, String url, String sha);
	
	Slice<FileReusabilityMetrics> findReusabilityMetrics(Pageable pageable, String url, String sha, String filePath);
	
	Slice<AnalyzedCommit> findAnalyzedCommits(Pageable pageable, String url);*/
}
