/*
 * ******************************************************************************
 *  * Copyright (C) 2022-2023 University of Macedonia
 *  *
 *  * This program AND the accompanying materials are made
 *  * available under the terms of the Eclipse Public License 2.0
 *  * which is available at https://www.eclipse.org/legal/epl-2.0/
 *  *
 *  * SPDX-License-Identifier: EPL-2.0
 *  *****************************************************************************
 */

package tasostilsi.uom.edu.gr.metricsCalculator.Repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Metrics.QualityMetrics;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.DTOs.ProjectDTO;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.*;

import java.util.Collection;

@Repository
public interface QualityMetricsRepository extends JpaRepository<QualityMetrics, Long> {
	
	@Query("select distinct c.qualityMetrics.revision.count from CalculatedJavaFile c where c.qualityMetrics.revision.sha = :sha ")
	Long findDistinctRevisionCountByRevisionSha(String sha);
	
	@Query(value = "SELECT new tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.CumulativeInterest(c.qualityMetrics.revision.sha," +
			"c.qualityMetrics.revision.count, " +
			"ROUND(SUM(c.interest.interestInEuros),2), " +
			"ROUND(SUM(c.interest.interestInHours),1))" +
			"FROM CalculatedJavaFile c " +
			"WHERE c.project.url = :#{#project.url} " +
			"GROUP BY c.qualityMetrics.revision.sha, c.qualityMetrics.revision.count " +
			"ORDER BY c.qualityMetrics.revision.count")
	Collection<CumulativeInterest> findCumulativeInterestPerCommit(ProjectDTO project);
	
	@Query(value = "SELECT new tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.CumulativeInterest(c.qualityMetrics.revision.sha," +
			"c.qualityMetrics.revision.count, " +
			"ROUND(SUM(c.interest.interestInEuros),2), " +
			"ROUND(SUM(c.interest.interestInHours),1))" +
			"FROM CalculatedJavaFile c " +
			"WHERE c.project.url = :#{#project.url} " +
			"AND c.qualityMetrics.revision.sha = :sha " +
			"GROUP BY c.qualityMetrics.revision.sha, c.qualityMetrics.revision.count " +
			"ORDER BY c.qualityMetrics.revision.count")
	Collection<CumulativeInterest> findCumulativeInterestByCommit(ProjectDTO project, @Param("sha") String sha);
	
	@Query(value = "SELECT new tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.InterestPerCommitFile(c.qualityMetrics.revision.sha, " +
			"c.path," +
			"c.qualityMetrics.revision.count, " +
			"c.interest.interestInEuros," +
			"c.interest.interestInHours) " +
			"FROM CalculatedJavaFile c " +
			"WHERE c.project.url = :#{#project.url} " +
			"AND c.qualityMetrics.revision.sha = :sha " +
			"AND c.path = :filePath " +
			"ORDER BY c.path")
	Collection<InterestPerCommitFile> findInterestPerCommitFile(ProjectDTO project, @Param("sha") String sha, @Param("filePath") String filePath);
	
	@Query(value = "SELECT new tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.InterestPerCommitFile(c.qualityMetrics.revision.sha, " +
			"c.path," +
			"c.qualityMetrics.revision.count, " +
			"c.interest.interestInEuros," +
			"c.interest.interestInHours) " +
			"FROM CalculatedJavaFile c " +
			"WHERE c.project.url = :#{#project.url} " +
			"ORDER BY c.qualityMetrics.revision.count")
	Collection<InterestPerCommitFile> findInterestPerCommitFile(ProjectDTO project);
	
	@Query(value = "SELECT new tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.InterestChange(c.qualityMetrics.revision.sha, " +
			"c.qualityMetrics.revision.count, " +
			"SUM(c.interest.interestInEuros) - " +
			"(SELECT SUM(c2.interest.interestInEuros) " +
			"FROM CalculatedJavaFile c2 " +
			"WHERE c2.project.url = :#{#project.url} " +
			"AND c2.qualityMetrics.revision.count = c.qualityMetrics.revision.count-1), " +
			"SUM(c.interest.interestInHours) - " +
			"(SELECT SUM(c2.interest.interestInHours) " +
			"FROM CalculatedJavaFile c2 " +
			"WHERE c2.project.url = :#{#project.url} " +
			"AND c2.qualityMetrics.revision.count = c.qualityMetrics.revision.count-1), " +
			"(SUM(c.interest.interestInEuros) - " +
			"(SELECT SUM(c2.interest.interestInEuros) " +
			"FROM CalculatedJavaFile c2 " +
			"WHERE c2.project.url = :#{#project.url} " +
			"AND c2.qualityMetrics.revision.count = c.qualityMetrics.revision.count-1))/" +
			"(SELECT SUM(c3.interest.interestInEuros) " +
			"FROM CalculatedJavaFile c3 " +
			"WHERE c3.project.url = :#{#project.url} " +
			"AND c3.qualityMetrics.revision.count = c.qualityMetrics.revision.count-1)) " +
			"FROM CalculatedJavaFile c " +
			"WHERE c.project.url = :#{#project.url} " +
			"AND c.qualityMetrics.revision.sha = :sha " +
			"GROUP BY c.qualityMetrics.revision.sha, c.qualityMetrics.revision.count")
	Collection<InterestChange> findInterestChangeByCommit(ProjectDTO project, @Param("sha") String sha);
	
	@Query(value = "SELECT new tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.FileInterestChange(c.qualityMetrics.revision.sha, " +
			"c.qualityMetrics.revision.count, " +
			"c.path, " +
			"c.interest.interestInEuros - " +
			"(SELECT c2.interest.interestInEuros " +
			"FROM CalculatedJavaFile c2 " +
			"WHERE c2.project.url = :#{#project.url} " +
			"AND c.path = c2.path " +
			"AND c2.qualityMetrics.revision.count = c.qualityMetrics.revision.count-1), " +
			"c.interest.interestInHours - " +
			"(SELECT c3.interest.interestInHours " +
			"FROM CalculatedJavaFile c3 " +
			"WHERE c3.project.url = :#{#project.url} " +
			"AND c.path = c3.path " +
			"AND c3.qualityMetrics.revision.count = c.qualityMetrics.revision.count-1), " +
			"(c.interest.interestInEuros - " +
			"(SELECT c4.interest.interestInEuros " +
			"FROM CalculatedJavaFile c4 " +
			"WHERE c4.project.url = :#{#project.url} " +
			"AND c.path = c4.path " +
			"AND c4.qualityMetrics.revision.count = c.qualityMetrics.revision.count-1))/NULLIF((" +
			"SELECT c5.interest.interestInEuros " +
			"FROM CalculatedJavaFile c5 " +
			"WHERE c5.project.url = :#{#project.url} " +
			"AND c.path = c5.path " +
			"AND c5.qualityMetrics.revision.count = c.qualityMetrics.revision.count-1),0)) " +
			"FROM CalculatedJavaFile c " +
			"WHERE c.project.url = :#{#project.url} " +
			"AND c.qualityMetrics.revision.sha = :sha " +
			"AND c.path = :filePath")
	FileInterestChange findInterestChangeByCommitAndFile(ProjectDTO project, @Param("sha") String sha, @Param("filePath") String filePath);
	
	@Query(value = "SELECT new tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.NormalizedInterest(c.qualityMetrics.revision.sha, " +
			"c.qualityMetrics.revision.count, " +
			"SUM(c.interest.interestInEuros)/SUM(c.qualityMetrics.SIZE1), " +
			"SUM(c.interest.interestInHours)/SUM(c.qualityMetrics.SIZE1)) " +
			"FROM CalculatedJavaFile c " +
			"WHERE c.project.url = :#{#project.url} " +
			"GROUP BY c.qualityMetrics.revision.sha, c.qualityMetrics.revision.count " +
			"HAVING SUM(c.qualityMetrics.SIZE1) <> 0 " +
			"ORDER BY c.qualityMetrics.revision.count")
	Collection<NormalizedInterest> findNormalizedInterest(ProjectDTO project);
	
	@Query(value = "SELECT new tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.NormalizedInterest(c.qualityMetrics.revision.sha, " +
			"c.qualityMetrics.revision.count, " +
			"SUM(c.interest.interestInEuros)/SUM(c.qualityMetrics.SIZE1), " +
			"SUM(c.interest.interestInHours)/SUM(c.qualityMetrics.SIZE1)) " +
			"FROM CalculatedJavaFile c " +
			"WHERE c.project.url = :#{#project.url} " +
			"AND c.qualityMetrics.revision.sha = :sha " +
			"GROUP BY c.qualityMetrics.revision.sha, c.qualityMetrics.revision.count " +
			"HAVING SUM(c.qualityMetrics.SIZE1) <> 0")
	Collection<NormalizedInterest> findNormalizedInterestByCommit(ProjectDTO project, @Param("sha") String sha);
	
	@Query(value = "SELECT new tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.HighInterestFile(c.qualityMetrics.revision.sha, " +
			"c.qualityMetrics.revision.count, " +
			"c.path, " +
			"c.interest.interestInEuros, " +
			"c.interest.interestInHours, " +
			"c.interest.interestInEuros/" +
			"(SELECT SUM(c2.interest.interestInEuros) " +
			"FROM CalculatedJavaFile c2 " +
			"WHERE c2.project.url = :#{#project.url} " +
			"AND c2.qualityMetrics.revision.sha = :sha)) " +
			"FROM CalculatedJavaFile c " +
			"WHERE c.project.url = :#{#project.url} " +
			"AND c.qualityMetrics.revision.sha = :sha " +
			"GROUP BY c.qualityMetrics.revision.sha, " +
			"c.qualityMetrics.revision.count, " +
			"c.path, " +
			"c.interest.interestInEuros, " +
			"c.interest.interestInHours " +
			"ORDER BY c.interest.interestInEuros DESC")
	Slice<HighInterestFile> findHighInterestFiles(Pageable pageable, ProjectDTO project, @Param("sha") String sha);
	
	@Query(value = "SELECT new tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.ProjectReusabilityMetrics(c.qualityMetrics.revision.sha, " +
			"c.qualityMetrics.revision.count, " +
			"AVG(c.qualityMetrics.CBO), " +
			"AVG(c.qualityMetrics.DIT), " +
			"AVG(c.qualityMetrics.WMC), " +
			"AVG(c.qualityMetrics.RFC), " +
			"AVG(c.qualityMetrics.LCOM), " +
			"AVG(c.qualityMetrics.NOCC)) " +
			"FROM CalculatedJavaFile c " +
			"WHERE c.project.url = :#{#project.url} " +
			"AND c.qualityMetrics.LCOM >= 0 " +
			"AND c.qualityMetrics.DIT >= 0 " +
			"GROUP BY c.qualityMetrics.revision.sha, c.qualityMetrics.revision.count " +
			"ORDER BY c.qualityMetrics.revision.count")
	Slice<ProjectReusabilityMetrics> findProjectReusabilityMetrics(Pageable pageable, ProjectDTO project);
	
	@Query(value = "SELECT new tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.FileReusabilityMetrics(c.qualityMetrics.revision.sha, " +
			"c.qualityMetrics.revision.count, " +
			"c.path, " +
			"c.qualityMetrics.CBO, " +
			"c.qualityMetrics.DIT, " +
			"c.qualityMetrics.WMC, " +
			"c.qualityMetrics.RFC, " +
			"c.qualityMetrics.LCOM, " +
			"c.qualityMetrics.NOCC) " +
			"FROM CalculatedJavaFile c " +
			"WHERE c.project.url = :#{#project.url} " +
			"AND c.qualityMetrics.revision.sha = :sha " +
			"AND c.qualityMetrics.LCOM >= 0 AND c.qualityMetrics.DIT >= 0 " +
			"ORDER BY c.path")
	Slice<FileReusabilityMetrics> findFileReusabilityMetrics(Pageable pageable, ProjectDTO project, @Param("sha") String sha);
	
	@Query(value = "SELECT new tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.FileReusabilityMetrics(c.qualityMetrics.revision.sha, " +
			"c.qualityMetrics.revision.count, " +
			"c.path, " +
			"c.qualityMetrics.CBO, " +
			"c.qualityMetrics.DIT, " +
			"c.qualityMetrics.WMC, " +
			"c.qualityMetrics.RFC, " +
			"c.qualityMetrics.LCOM, " +
			"c.qualityMetrics.NOCC) " +
			"FROM CalculatedJavaFile c " +
			"WHERE c.project.url = :#{#project.url} " +
			"AND c.qualityMetrics.revision.sha = :sha " +
			"AND c.path = :filePath " +
			"AND c.qualityMetrics.LCOM >= 0 " +
			"AND c.qualityMetrics.DIT >= 0 " +
			"ORDER BY c.path")
	Slice<FileReusabilityMetrics> findFileReusabilityMetrics(Pageable pageable, ProjectDTO project, @Param("sha") String sha, @Param("filePath") String filePath);
	
	@Query(value = "SELECT new tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.FileReusabilityMetrics(c.qualityMetrics.revision.sha, " +
			"c.qualityMetrics.revision.count, " +
			"c.path, " +
			"c.qualityMetrics.CBO, " +
			"c.qualityMetrics.DIT, " +
			"c.qualityMetrics.WMC, " +
			"c.qualityMetrics.RFC, " +
			"c.qualityMetrics.LCOM, " +
			"c.qualityMetrics.NOCC) " +
			"FROM CalculatedJavaFile c " +
			"WHERE c.project.url = :#{#project.url} " +
			"AND c.qualityMetrics.LCOM >= 0 " +
			"AND c.qualityMetrics.DIT >= 0 " +
			"ORDER BY c.path")
	Slice<FileReusabilityMetrics> findFileReusabilityMetrics(Pageable pageable, ProjectDTO project);
	
	@Query(value = "SELECT DISTINCT new tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.AnalyzedCommit(c.qualityMetrics.revision.sha, " +
			"c.qualityMetrics.revision.count) " +
			"FROM CalculatedJavaFile c " +
			"WHERE c.project.url = :#{#project.url} " +
			"ORDER BY c.qualityMetrics.revision.count DESC")
	Slice<AnalyzedCommit> findAnalyzedCommits(Pageable pageable, ProjectDTO project);
	
}
