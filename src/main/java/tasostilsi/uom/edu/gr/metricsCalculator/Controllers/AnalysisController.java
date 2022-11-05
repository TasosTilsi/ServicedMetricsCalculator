package tasostilsi.uom.edu.gr.metricsCalculator.Controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.DTOs.NewAnalysisDTO;
import tasostilsi.uom.edu.gr.metricsCalculator.Services.AnalysisService;

@RestController
@RequestMapping(path = "/api/analysis")
public class AnalysisController {
	
	@Autowired
	private AnalysisService analysisService;
	
	@PostMapping
	public ResponseEntity<String> makeNewAnalysis(@RequestBody NewAnalysisDTO newAnalysisDTO) throws Exception {
		analysisService.startNewAnalysis(newAnalysisDTO);
		return new ResponseEntity<>("New Analysis for " + newAnalysisDTO.getGitUrl() + " has started with " + newAnalysisDTO.getAccessToken() + " access token", HttpStatus.OK);
	}
}
