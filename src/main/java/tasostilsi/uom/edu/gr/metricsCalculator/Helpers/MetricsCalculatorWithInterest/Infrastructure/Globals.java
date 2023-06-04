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
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.Project;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class Globals {
	private final Set<CalculatedJavaFile> javaFiles;
	
	Globals() {
		javaFiles = ConcurrentHashMap.newKeySet();
	}
	
	public synchronized void addJavaFile(CalculatedJavaFile jf) {
		if (javaFiles.stream().anyMatch(file -> file.getPath().equalsIgnoreCase(jf.getPath()))) {
			removeFile(jf);
			javaFiles.add(jf);
		} else {
			javaFiles.add(jf);
		}
	}
	
	public Set<CalculatedJavaFile> getJavaFiles() {
		return javaFiles;
	}
	
	public synchronized void removeFiles(Set<CalculatedJavaFile> paths) {
		Set<String> pathSet = paths.stream().map(CalculatedJavaFile::getPath).collect(Collectors.toSet());
		javaFiles.removeIf(file -> pathSet.contains(file.getPath()));
	}
	
	public synchronized void removeFile(CalculatedJavaFile jf) {
		javaFiles.removeIf(file -> jf.getPath().equalsIgnoreCase(file.getPath()));
	}
	
	public synchronized void removeFiles(Project project) {
		javaFiles.removeIf(file -> file.getProject().getUrl().equalsIgnoreCase(project.getUrl()));
	}
	
}
