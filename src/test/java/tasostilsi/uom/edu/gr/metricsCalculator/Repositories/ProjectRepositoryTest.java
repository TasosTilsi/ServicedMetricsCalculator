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

import ch.qos.logback.classic.Logger;
import nonapi.io.github.classgraph.json.JSONSerializer;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedClass;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedJavaFile;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.Project;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.testng.Assert.*;

@SpringBootTest
class ProjectRepositoryTest {
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ProjectRepositoryTest.class);
	
	private static final String PROJECT_URL = "https://github.com/TasosTilsi/ServicedMetricsCalculator";
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Test
	void findByUrl() {
		try {
			Project project = createProject();
			saveProjectToDB(project);
			
			LOGGER.info("Retrieving Project from DB");
			Optional<Project> retrievedProjectFromDB = projectRepository.findByUrl(project.getUrl());
			assertTrue(retrievedProjectFromDB.isPresent(), "Project is Present");
			LOGGER.info("Project retrieved: " + retrievedProjectFromDB.orElseThrow());
			
			assertEquals(retrievedProjectFromDB.orElseThrow().getUrl(), project.getUrl(), "URL matches");
		} finally {
			projectRepository.deleteAll();
		}
	}
	
	
	@Test
	void findByOwnerAndRepo() {
		try {
			Project project = createProject();
			saveProjectToDB(project);
			
			LOGGER.info("Retrieving Project from DB");
			Optional<Project> retrievedProjectFromDB = projectRepository.findByOwnerAndRepo(project.getOwner(), project.getRepo());
			assertTrue(retrievedProjectFromDB.isPresent(), "Project is Present");
			LOGGER.info("Project retrieved: " + retrievedProjectFromDB.orElseThrow());
			
			assertEquals(retrievedProjectFromDB.orElseThrow().getUrl(), project.getUrl(), "URL matches");
			assertEquals(retrievedProjectFromDB.orElseThrow().getOwner(), project.getOwner(), "Owner matches");
			assertEquals(retrievedProjectFromDB.orElseThrow().getRepo(), project.getRepo(), "Owner matches");
		} finally {
			projectRepository.deleteAll();
		}
	}
	
	@Test
	void findByOwner() {
		try {
			Project project = createProject();
			saveProjectToDB(project);
			
			LOGGER.info("Retrieving Project from DB");
			List<Project> retrievedProjectFromDB = projectRepository.findByOwner(project.getOwner());
			assertFalse(retrievedProjectFromDB.isEmpty(), "List is Present");
			LOGGER.info("Projects retrieved: " + Arrays.toString(retrievedProjectFromDB.toArray()));
			
			assertEquals(retrievedProjectFromDB.size(), 1, "Size matches");
		} finally {
			projectRepository.deleteAll();
		}
	}
	
	@Test
	void findByRepo() {
		try {
			Project project = createProject();
			saveProjectToDB(project);
			
			LOGGER.info("Retrieving Project from DB");
			List<Project> retrievedProjectFromDB = projectRepository.findByRepo(project.getRepo());
			assertFalse(retrievedProjectFromDB.isEmpty(), "List is Present");
			LOGGER.info("Projects retrieved: " + Arrays.toString(retrievedProjectFromDB.toArray()));
			
			assertEquals(retrievedProjectFromDB.size(), 1, "Size matches");
		} finally {
			projectRepository.deleteAll();
		}
	}
	
	@Test
	void getIdByUrl() {
		try {
			Project project = createProject();
			saveProjectToDB(project);
			
			LOGGER.info("Retrieving Project from DB");
			Optional<Long> retrievedProjectFromDB = projectRepository.getIdByUrl(project.getUrl());
			assertTrue(retrievedProjectFromDB.isPresent(), "ID is Present");
			LOGGER.info("ID retrieved: " + retrievedProjectFromDB.orElseThrow());
			
			assertEquals(retrievedProjectFromDB.get(), project.getId(), "ID matches");
		} finally {
			projectRepository.deleteAll();
		}
	}
	
	@Test
	void getIdByOwnerAndRepo() {
		try {
			Project project = createProject();
			saveProjectToDB(project);
			
			LOGGER.info("Retrieving Project from DB");
			Optional<Long> retrievedProjectFromDB = projectRepository.getIdByOwnerAndRepo(project.getOwner(), project.getRepo());
			assertTrue(retrievedProjectFromDB.isPresent(), "ID is Present");
			LOGGER.info("ID retrieved: " + retrievedProjectFromDB.orElseThrow());
			
			assertEquals(retrievedProjectFromDB.get(), project.getId(), "ID matches");
		} finally {
			projectRepository.deleteAll();
		}
	}
	
	@Test
	void getProjectPathByUrl() {
		try {
			Project project = createProject();
			saveProjectToDB(project);
			
			LOGGER.info("Retrieving Project from DB");
			Optional<String> retrievedProjectFromDB = projectRepository.getProjectPathByUrl(project.getUrl());
			assertTrue(retrievedProjectFromDB.isPresent(), "ID is Present");
			LOGGER.info("Path retrieved: " + retrievedProjectFromDB.orElseThrow());
			
			assertEquals(retrievedProjectFromDB.get(), project.getClonePath(), "ID matches");
		} finally {
			projectRepository.deleteAll();
		}
	}
	
	@Test
	void initializeProjectAnalysis() {
		try {
			Project project = createProject();
			saveProjectToDB(project);

			Set<CalculatedClass> calculatedClasses = ConcurrentHashMap.newKeySet();
			CalculatedJavaFile javaFile = new CalculatedJavaFile("", calculatedClasses);
			
			LOGGER.info("Retrieving Project from DB");
			int retrievedProjectFromDB = projectRepository.initializeProjectAnalysis(javaFile, project.getUrl());
			
			assertEquals(retrievedProjectFromDB, 1, "Status returned");
		} finally {
			projectRepository.deleteAll();
		}
	}
	
	private void saveProjectToDB(Project project) {
		LOGGER.info("Saving project into DB");
		projectRepository.save(project);
		LOGGER.info("Project Saved into DB");
	}
	
	@NotNull
	private static Project createProject() {
		LOGGER.info("Creating a project");
		Project project = new Project(PROJECT_URL);
		LOGGER.info("Project created: " + JSONSerializer.serializeObject(project));
		return project;
	}
}
