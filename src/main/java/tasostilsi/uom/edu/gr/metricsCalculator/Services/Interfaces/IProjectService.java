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

import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.Project;

import java.util.List;

public interface IProjectService {
	
	List<Project> getProjects();
	
	List<Project> getProjectsByOwner(String owner);
	
	List<Project> getProjectsByRepo(String repo);
	
	Project getProjectById(Long id);
	
	Project getProjectByUrl(String url);
	
	Project getProjectByOwnerAndRepo(String owner, String repo);
	
	void deleteProjectByUrl(String url);
	
	void deleteProjectById(Long id);
}
