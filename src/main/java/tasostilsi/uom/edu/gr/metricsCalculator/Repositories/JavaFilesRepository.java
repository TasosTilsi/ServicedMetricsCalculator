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

package tasostilsi.uom.edu.gr.metricsCalculator.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedJavaFile;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.Revision;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.DTOs.ProjectDTO;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface JavaFilesRepository extends JpaRepository<CalculatedJavaFile, Long> {
	
	List<CalculatedJavaFile> findDistinctRevisionShaByProjectId(Long project_id);
	
	List<CalculatedJavaFile> findDistinctRevisionCountByProjectId(Long project_id);
	
	@Query("select distinct c.k.revision.sha from CalculatedJavaFile c where c.project.id = :project_id ")
	List<String> getDistinctRevisionShaByProjectId(Long project_id);
	
	@Query("select distinct c.k.revision " +
			"from CalculatedJavaFile c " +
			"where c.project.id = :project_id " +
			"order by c.k.revision.count DESC ")
	Optional<List<Revision>> getRevisionByProjectIdOrderByRevisionCountDesc(Long project_id);
	
	@Query("select jf.path from CalculatedJavaFile jf where jf.path = ?1")
	Optional<String> getJavaFilePathById(String path);
	
	@Query("select c from CalculatedJavaFile c where c.project.id = ?1")
	Optional<Set<CalculatedJavaFile>> getAllByProjectId(Long project_id);
	
	@Transactional
	@Modifying
	@Query(value = "Insert into java_files c set c = ?1",
			nativeQuery = true)
	void insertJavaFileToDB(CalculatedJavaFile jf);
	
	@Query("select c from CalculatedJavaFile c " +
			"where c.interest.interestInEuros = ?1 " +
			"and c.interest.interestInHours = ?2 " +
			"and c.interest.interestInAvgLOC = ?3 " +
			"and c.interest.avgInterestPerLOC = ?4 " +
			"and c.interest.sumInterestPerLOC = ?5 " +
			"and c.k.revision.sha = ?6 " +
			"and c.k.revision.count = ?7 " +
			"and c.project.url = ?8 " +
			"and c.project.owner = ?9 " +
			"and c.project.repo = ?10 " +
			"and c.qualityMetrics.DIT = ?11")
	Collection<CalculatedJavaFile> test(Double interestInEuros, Double interestInHours, Double interestInAvgLOC, Double avgInterestPerLOC, Double sumInterestPerLOC, String sha, Integer count, String url, String owner, String repo, Integer DIT);
	
	@Query("select c from CalculatedJavaFile c where c.path = ?1")
	Optional<CalculatedJavaFile> test2(String path);
	
	
	
}
