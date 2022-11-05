package tasostilsi.uom.edu.gr.metricsCalculator.Models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewAnalysisDTO {
	
	@NotNull
	@Valid
	private String gitUrl;
	
	@Nullable
	private String accessToken;
}
