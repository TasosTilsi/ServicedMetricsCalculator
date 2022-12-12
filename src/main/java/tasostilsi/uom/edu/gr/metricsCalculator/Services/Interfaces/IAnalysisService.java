package tasostilsi.uom.edu.gr.metricsCalculator.Services.Interfaces;

import tasostilsi.uom.edu.gr.metricsCalculator.Models.DTOs.NewAnalysisDTO;

public interface IAnalysisService {
	
	String startNewAnalysis(NewAnalysisDTO newAnalysisDTO) throws Exception;
}
