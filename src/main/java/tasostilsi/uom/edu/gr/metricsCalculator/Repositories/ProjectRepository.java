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
	
}
