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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GitUserCredentialsDTO {
	
	private String username;
	
	private String passwordOrAccessToken;
	
	public boolean hasPassword() {
		return !passwordOrAccessToken.isEmpty() || !passwordOrAccessToken.isBlank();
	}
	
	public boolean hasUsername() {
		return !username.isEmpty() || !username.isBlank();
	}
	
	public boolean isBlank() {
		return hasUsername() && hasPassword();
	}
}
