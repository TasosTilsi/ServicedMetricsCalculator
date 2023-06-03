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

package tasostilsi.uom.edu.gr.metricsCalculator.Services;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.Project;
import tasostilsi.uom.edu.gr.metricsCalculator.Repositories.JavaFilesRepository;
import tasostilsi.uom.edu.gr.metricsCalculator.Repositories.ProjectRepository;
import tasostilsi.uom.edu.gr.metricsCalculator.Services.Interfaces.IProjectService;

import java.util.List;

@Service
public class ProjectService implements IProjectService {
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ProjectService.class);
	
	private final ProjectRepository projectRepository;
	private final JavaFilesRepository javaFilesRepository;
	
	@Autowired
	public ProjectService(ProjectRepository projectRepository, JavaFilesRepository javaFilesRepository) {
		this.projectRepository = projectRepository;
		this.javaFilesRepository = javaFilesRepository;
	}
	
	
	public List<Project> getProjects() {
		List<Project> projects = projectRepository.findAll();
//		LOGGER.info("Reply: " + JSONSerializer.serializeObject(projects));
		
		return projects;
	}
	
	@Override
	public List<Project> getProjectsByOwner(String owner) {
		List<Project> projects = projectRepository.findByOwner(owner);
//		LOGGER.info("Reply: " + JSONSerializer.serializeObject(projects));
		
		return projects;
	}
	
	@Override
	public List<Project> getProjectsByRepo(String repo) {
		List<Project> projects = projectRepository.findByRepo(repo);
//		LOGGER.info("Reply: " + JSONSerializer.serializeObject(projects));
		
		return projects;
	}
	
	@Override
	public Project getProjectById(Long id) {
		Project project = projectRepository.findById(id).orElseThrow();
//		LOGGER.info("Reply: " + JSONSerializer.serializeObject(project));
		
		return project;
	}
	
	@Override
	public Project getProjectByUrl(String url) {
		Project project = projectRepository.findByUrl(url).orElseThrow();
//		LOGGER.info("Reply: " + JSONSerializer.serializeObject(project));
		
		return project;
	}
	
	@Override
	public Project getProjectByOwnerAndRepo(String owner, String repo) {
		Project project = projectRepository.findByOwnerAndRepo(owner, repo).orElseThrow();
//		LOGGER.info("Reply: " + JSONSerializer.serializeObject(project));
		
		return project;
	}
	
	@Override
	public String getProjectStateByUrl(String url) {
		String projectState = projectRepository.getProjectStateByUrl(url).orElseThrow();
//		LOGGER.info("Reply: " + JSONSerializer.serializeObject(project));
		
		return projectState;
	}
	
	@Override
	public void deleteProjectByUrl(String url) {
		Project project = projectRepository.findByUrl(url).orElseThrow();
		projectRepository.deleteCalculatedClassesByProjectIdNative(project.getId());
		projectRepository.deleteCalculatedJavaFileByProjectIdNative(project.getId());
		projectRepository.deleteByUrl(url);
		projectRepository.deleteUnusedQualityMetrics();
//		LOGGER.info("Reply: " + JSONSerializer.serializeObject(project));
	
	}
	
	@Override
	public void deleteProjectById(Long id) {
		Project project = projectRepository.findById(id).orElseThrow();
		projectRepository.deleteCalculatedClassesByProjectIdNative(project.getId());
		projectRepository.deleteCalculatedJavaFileByProjectIdNative(project.getId());
		projectRepository.deleteById(project.getId());
		projectRepository.deleteUnusedQualityMetrics();
//		LOGGER.info("Reply: " + JSONSerializer.serializeObject(project));
	
	}
}
