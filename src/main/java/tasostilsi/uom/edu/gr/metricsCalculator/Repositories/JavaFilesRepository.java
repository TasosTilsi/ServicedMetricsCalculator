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
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedJavaFile;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.Revision;

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
	
	Optional<Double> findKappaValueById(Long javaFileId);
	
}
