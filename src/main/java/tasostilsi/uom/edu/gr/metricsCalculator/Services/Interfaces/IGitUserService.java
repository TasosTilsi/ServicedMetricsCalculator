package tasostilsi.uom.edu.gr.metricsCalculator.Services.Interfaces;

import tasostilsi.uom.edu.gr.metricsCalculator.Models.GitUser;

public interface IGitUserService {
	GitUser getUser(String id);
	
	GitUser createUser(GitUser user);
}
