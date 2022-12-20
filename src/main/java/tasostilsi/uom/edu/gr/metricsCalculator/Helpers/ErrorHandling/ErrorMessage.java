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

package tasostilsi.uom.edu.gr.metricsCalculator.Helpers.ErrorHandling;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class ErrorMessage {
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private Date timestamp;
	private String message;
	private String cause;
	private String requestUri;
	private Map<String, String[]> paramsUsed;
	private String stackTrace;
	
	
	public ErrorMessage(Exception ex, WebRequest request) {
		this.timestamp = new Date();
		this.requestUri = request.getHeader("host") + request.getDescription(false).replace("uri=", "");
		this.message = ex.getLocalizedMessage();
		this.cause = ex.toString();
		this.paramsUsed = request.getParameterMap();
		this.stackTrace = Arrays.toString(ex.getStackTrace());
	}
}
