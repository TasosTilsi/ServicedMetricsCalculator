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
}
