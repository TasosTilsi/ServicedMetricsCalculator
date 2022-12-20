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
