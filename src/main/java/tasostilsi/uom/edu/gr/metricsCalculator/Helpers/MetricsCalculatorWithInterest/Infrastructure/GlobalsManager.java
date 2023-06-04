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

package tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalsManager {
	private static final Map<String, Globals> projectGlobalsMap = new ConcurrentHashMap<>();
	
	public static Globals getProjectGlobals(String projectUrl) {
		return projectGlobalsMap.computeIfAbsent(projectUrl, key -> new Globals());
	}
	
	public static void removeProjectGlobals(String projectUrl) {
		projectGlobalsMap.remove(projectUrl);
	}
}
