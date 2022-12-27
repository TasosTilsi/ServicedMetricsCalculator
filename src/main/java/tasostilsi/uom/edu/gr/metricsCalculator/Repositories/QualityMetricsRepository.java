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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Metrics.QualityMetrics;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.DTOs.ProjectDTO;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.CumulativeInterest;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.InterestPerCommitFile;

import java.util.Collection;

@Repository
public interface QualityMetricsRepository extends JpaRepository<QualityMetrics, Long> {
	
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
	
/*

	@Query(value = "SELECT new InterestPerCommitFile(m.sha, f.filePath, m.revisionCount, m.interestEu, m.interestHours) "
			+ "FROM Metrics m JOIN Files f ON m.fid = f.fid AND m.sha = f.sha "
			+ "WHERE m.pid = (SELECT pid FROM Projects WHERE owner = :#{#project.owner} AND repo = :#{#project.repo}) AND m.sha = :sha AND f.filePath = :filePath ORDER BY f.filePath")
	Collection<InterestPerCommitFile> findInterestPerCommitFile(ProjectDTO project, @Param("sha") String sha, @Param("filePath") String filePath);

	@Query(value = "SELECT new InterestChange(m.sha, m.revisionCount, SUM(m.interestEu) - (SELECT SUM(m2.interestEu) FROM Metrics m2 WHERE m2.pid = (SELECT pid FROM Projects WHERE owner = :#{#project.owner} AND repo = :#{#project.repo}) AND m2.revisionCount = m.revisionCount-1), SUM(m.interestHours) - (SELECT SUM(m2.interestHours) FROM Metrics m2 WHERE m2.pid = (SELECT pid FROM Projects WHERE owner = :#{#project.owner} AND repo = :#{#project.repo}) AND m2.revisionCount = m.revisionCount-1), (SUM(m.interestEu) - (SELECT SUM(m2.interestEu) FROM Metrics m2 WHERE m2.pid = (SELECT pid FROM Projects WHERE owner = :#{#project.owner} AND repo = :#{#project.repo}) AND m2.revisionCount = m.revisionCount-1))/(SELECT SUM(m3.interestEu) FROM Metrics m3 WHERE m3.pid = (SELECT pid FROM Projects WHERE owner = :#{#project.owner} AND repo = :#{#project.repo}) AND m3.revisionCount = m.revisionCount-1))"
			+ "FROM Metrics m "
			+ "WHERE m.pid = (SELECT pid FROM Projects WHERE owner = :#{#project.owner} AND repo = :#{#project.repo}) AND m.sha = :sha GROUP BY m.sha, m.revisionCount")
	Collection<InterestChange> findInterestChangeByCommit(ProjectDTO project, @Param("sha") String sha);

	@Query(value = "SELECT new FileInterestChange(m.sha, m.revisionCount, f.filePath, m.interestEu - (SELECT m2.interestEu FROM Metrics m2 JOIN Files f2 ON m2.fid = f2.fid WHERE m2.pid = (SELECT pid FROM Projects WHERE owner = :#{#project.owner} AND repo = :#{#project.repo}) AND f.filePath = f2.filePath AND m2.revisionCount = m.revisionCount-1), m.interestHours - (SELECT m3.interestHours FROM Metrics m3 JOIN Files f3 ON m3.fid = f3.fid WHERE m3.pid = (SELECT pid FROM Projects WHERE owner = :#{#project.owner} AND f.filePath = f3.filePath AND repo = :#{#project.repo}) AND m3.revisionCount = m.revisionCount-1), (m.interestEu - (SELECT m4.interestEu FROM Metrics m4 JOIN Files f4 ON m4.fid = f4.fid WHERE m4.pid = (SELECT pid FROM Projects WHERE owner = :#{#project.owner} AND repo = :#{#project.repo}) AND f.filePath = f4.filePath AND m4.revisionCount = m.revisionCount-1))/NULLIF((SELECT m5.interestEu FROM Metrics m5 JOIN Files f5 ON m5.fid = f5.fid WHERE m5.pid = (SELECT pid FROM Projects WHERE owner = :#{#project.owner} AND repo = :#{#project.repo}) AND f.filePath = f5.filePath AND m5.revisionCount = m.revisionCount-1),0))"
			+ "FROM Metrics m JOIN Files f ON m.fid = f.fid AND m.sha = f.sha "
			+ "WHERE m.pid = (SELECT pid FROM Projects WHERE owner = :#{#project.owner} AND repo = :#{#project.repo}) AND m.sha = :sha AND f.filePath = :filePath")
	FileInterestChange findInterestChangeByCommitAndFile(ProjectDTO project, @Param("sha") String sha, @Param("filePath") String filePath);

	@Query(value = "SELECT new NormalizedInterest(m.sha, m.revisionCount, SUM(m.interestEu)/SUM(m.size1), SUM(m.interestHours)/SUM(m.size1)) "
			+ "FROM Metrics m "
			+ "WHERE m.pid = (SELECT pid FROM Projects WHERE owner = :#{#project.owner} AND repo = :#{#project.repo}) GROUP BY m.sha, m.revisionCount HAVING SUM(m.size1) <> 0 ORDER BY m.revisionCount")
	Collection<NormalizedInterest> findNormalizedInterest(ProjectDTO project);

	@Query(value = "SELECT new NormalizedInterest(m.sha, m.revisionCount, SUM(m.interestEu)/SUM(m.size1), SUM(m.interestHours)/SUM(m.size1)) "
			+ "FROM Metrics m "
			+ "WHERE m.pid = (SELECT pid FROM Projects WHERE owner = :#{#project.owner} AND repo = :#{#project.repo}) AND sha = :sha GROUP BY m.sha, m.revisionCount HAVING SUM(m.size1) <> 0")
	Collection<NormalizedInterest> findNormalizedInterestByCommit(ProjectDTO project, @Param("sha") String sha);

	@Query(value = "SELECT new HighInterestFile(m.sha, m.revisionCount, f.filePath, m.interestEu, m.interestHours, m.interestEu/(SELECT SUM(m2.interestEu) FROM Metrics m2 WHERE m2.pid = (SELECT pid FROM Projects WHERE owner = :#{#project.owner} AND repo = :#{#project.repo}) AND m2.sha = :sha)) "
			+ "FROM Metrics m JOIN Files f ON m.pid = f.pid AND m.fid = f.fid AND m.sha = f.sha "
			+ "WHERE m.pid = (SELECT pid FROM Projects WHERE owner = :#{#project.owner} AND repo = :#{#project.repo}) AND m.sha = :sha GROUP BY m.sha, m.revisionCount, f.filePath, m.interestEu, m.interestHours ORDER BY m.interestEu DESC")
	Slice<HighInterestFile> findHighInterestFiles(Pageable pageable, ProjectDTO project, @Param("sha") String sha);

	@Query(value = "SELECT new ProjectReusabilityMetrics(m.sha, m.revisionCount, AVG(m.cbo), AVG(m.dit), AVG(m.wmc), AVG(m.rfc), AVG(m.lcom), AVG(m.nocc)) "
			+ "FROM Metrics m "
			+ "WHERE m.pid = (SELECT pid FROM Projects WHERE owner = :#{#project.owner} AND repo = :#{#project.repo}) AND m.lcom >= 0 AND m.dit >= 0 GROUP BY m.sha, m.revisionCount ORDER BY m.revisionCount")
	Slice<ProjectReusabilityMetrics> findReusabilityMetrics(Pageable pageable, ProjectDTO project);

	@Query(value = "SELECT new FileReusabilityMetrics(m.sha, m.revisionCount, f.filePath, m.cbo, m.dit, m.wmc, m.rfc, m.lcom, m.nocc) "
			+ "FROM Metrics m JOIN Files f ON m.pid = f.pid AND m.fid = f.fid AND m.sha = f.sha "
			+ "WHERE m.pid = (SELECT pid FROM Projects WHERE owner = :#{#project.owner} AND repo = :#{#project.repo}) AND m.sha = :sha AND m.lcom >= 0 AND m.dit >= 0 ORDER BY f.filePath")
	Slice<FileReusabilityMetrics> findReusabilityMetrics(Pageable pageable, ProjectDTO project, @Param("sha") String sha);

	@Query(value = "SELECT new FileReusabilityMetrics(m.sha, m.revisionCount, f.filePath, m.cbo, m.dit, m.wmc, m.rfc, m.lcom, m.nocc) "
			+ "FROM Metrics m JOIN Files f ON m.pid = f.pid AND m.fid = f.fid AND m.sha = f.sha "
			+ "WHERE m.pid = (SELECT pid FROM Projects WHERE owner = :#{#project.owner} AND repo = :#{#project.repo}) AND m.sha = :sha AND f.filePath = :filePath AND m.lcom >= 0 AND m.dit >= 0 ORDER BY f.filePath")
	Slice<FileReusabilityMetrics> findReusabilityMetrics(Pageable pageable, ProjectDTO project, @Param("sha") String sha, @Param("filePath") String filePath);

	@Query(value = "SELECT DISTINCT new AnalyzedCommit(m.sha, m.revisionCount) "
			+ "FROM Metrics m "
			+ "WHERE m.pid = (SELECT pid FROM Projects WHERE owner = :#{#project.owner} AND repo = :#{#project.repo}) ORDER BY m.revisionCount DESC")
	Slice<AnalyzedCommit> findAnalyzedCommits(Pageable pageable, ProjectDTO project);*/
}
