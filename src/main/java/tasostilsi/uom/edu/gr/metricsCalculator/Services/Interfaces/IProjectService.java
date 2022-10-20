package tasostilsi.uom.edu.gr.metricsCalculator.Services.Interfaces;

import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.Project;

public interface IProjectService {
	Project getProject();
	
	//    Project setProject(Project project);
	Project setProject(String url);
}
