package tasostilsi.uom.edu.gr.metricsCalculator.Models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewAnalysisDTO {
	
	@NotEmpty(message = "The url is required.")
	//find some patterns that matches the git urls
//	@Pattern(regexp = "^[a-zA-Z0-9]+://[a-zA-Z0-9]+\\.[a-zA-Z0-9]+/[a-zA-Z0-9]+/[a-zA-Z0-9]+$" , message="Invalid URL provided")
	@Valid
	private String gitUrl;
	
	@Nullable
	private String accessToken;
}
