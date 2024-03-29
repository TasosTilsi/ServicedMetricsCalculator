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
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.Project;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
	
	Optional<Project> findByUrl(String url);
	
	Optional<Project> findByOwnerAndRepo(String owner, String repo);
	
	List<Project> findByOwner(String owner);
	
	List<Project> findByRepo(String repo);
	
	@Query("select p.id from Project p where p.url = ?1")
	Optional<Long> getIdByUrl(String url);
	
	@Query("select p.id from Project p where p.owner = ?1 and p.repo = ?2")
	Optional<Long> getIdByOwnerAndRepo(String owner, String repo);
	
	@Query("select p.clonePath from Project p where p.url = ?1")
	Optional<String> getProjectPathByUrl(String url);
	
	@Query("select p.state from Project p where p.url = ?1")
	Optional<String> getProjectStateByUrl(String url);
	
	@Transactional
	@Modifying
	@Query("delete from Project p where p.id = ?1")
	void deleteById(Long id);
	
	@Transactional
	@Modifying
	@Query("delete from Project p where p.url = ?1")
	void deleteByUrl(String url);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM classes cc WHERE cc.classes_id IN (SELECT cc.classes_id FROM classes cc " +
			"JOIN java_files cjf ON cjf.id = cc.java_file_id " +
			"JOIN project p ON p.id = cjf.project_id " +
			"WHERE p.id = :projectId)", nativeQuery = true)
	void deleteCalculatedClassesByProjectIdNative(@Param("projectId") Long projectId);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM java_files cjf WHERE cjf.project_id = :projectId", nativeQuery = true)
	void deleteCalculatedJavaFileByProjectIdNative(@Param("projectId") Long projectId);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM metrics WHERE id NOT IN (SELECT quality_metrics_id FROM java_files) AND id NOT IN (SELECT quality_metrics_id FROM classes);", nativeQuery = true)
	void deleteUnusedQualityMetrics();
	
	
}
