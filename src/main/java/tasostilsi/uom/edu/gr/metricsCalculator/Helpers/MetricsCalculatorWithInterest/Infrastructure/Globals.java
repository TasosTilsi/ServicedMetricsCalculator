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

import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedJavaFile;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class Globals {
	
	private static final Set<CalculatedJavaFile> javaFiles;
	
	static {
		javaFiles = ConcurrentHashMap.newKeySet();
	}
	
	public static void addJavaFile(CalculatedJavaFile jf) {
		javaFiles.stream().filter(file -> file.getPath().equals(jf.getPath())).forEach(javaFiles::remove);
		javaFiles.add(jf);
	}
	
	public static Set<CalculatedJavaFile> getJavaFiles() {
		return javaFiles;
	}
}
