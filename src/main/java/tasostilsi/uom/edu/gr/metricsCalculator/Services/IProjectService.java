package tasostilsi.uom.edu.gr.metricsCalculator.Services;

import infrastructure.Project;

public interface IProjectService {
    Project getProject();

    //    Project setProject(Project project);
    Project setProject(String url);
}
