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

package tasostilsi.uom.edu.gr.metricsCalculator.Helpers.Utils;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedJavaFile;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.Project;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.DiffEntry;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.Globals;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.PrincipalResponseEntity;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.Revision;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.MetricsCalculator;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Utils {
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(Utils.class);
	
	private Utils() {
	}
	
	public static Utils getInstance() {
		//double-checked locking - because second check of Singleton instance with lock
		return UtilsInstanceHolder.instance;
	}
	
	private static final class UtilsInstanceHolder {
		private static final Utils instance = new Utils();
	}
	
	/**
	 * Removes those files that are marked as 'DELETED' (new code's call)
	 *
	 * @param diffEntries the modified java files (new, modified, deleted)
	 */
	private void removeDeletedFiles(Set<DiffEntry> diffEntries, Project project) {
		Set<CalculatedJavaFile> deletedFiles = diffEntries.stream()
				.flatMap(diffEntry -> project.getJavaFiles().stream()
						.filter(file -> file.getPath().equals(diffEntry.getOldFilePath())))
				.collect(Collectors.toSet());
		Globals.getJavaFiles().removeAll(deletedFiles);
		project.getJavaFiles().removeAll(deletedFiles);
		deletedFiles.forEach(file -> file.setDeleted(true));
		Globals.getJavaFiles().addAll(deletedFiles);
		project.getJavaFiles().addAll(deletedFiles);
	}
	
	/**
	 * Finds java file by its path
	 *
	 * @param filePath the file path
	 * @return the java file (JavaFile) whose path matches the given one
	 */
	private CalculatedJavaFile getAlreadyDefinedFile(String filePath) {
		return Globals.getJavaFiles().stream()
				.filter(jf -> jf.getPath().equals(filePath))
				.findFirst()
				.orElse(null);
	}
	
	/**
	 * Sets the metrics of new and modified files.
	 *
	 * @param project         the project we are referring to
	 * @param currentRevision the revision we are analysing
	 * @param entity          the entity with the list containing the diff entries received.
	 */
	public Project setMetrics(Project project, Revision currentRevision, PrincipalResponseEntity entity) throws Exception {
		if (!entity.getDeleteDiffEntries().isEmpty()) {
			removeDeletedFiles(entity.getDeleteDiffEntries(), project);
		}
		Set<String> addFilePaths = entity.getAddDiffEntries().stream()
				.map(DiffEntry::getNewFilePath)
				.collect(Collectors.toSet());
		Set<String> modifyFilePaths = entity.getModifyDiffEntries().stream()
				.map(DiffEntry::getNewFilePath)
				.collect(Collectors.toSet());
		if (!addFilePaths.isEmpty()) {
			project = setMetrics(project, currentRevision, addFilePaths);
		}
		if (!modifyFilePaths.isEmpty()) {
			project = setMetrics(project, currentRevision, modifyFilePaths);
		}
		if (!entity.getRenameDiffEntries().isEmpty()) {
			final Project finalProject = project;
			entity.getRenameDiffEntries()
					.forEach(diffEntry -> {
						for (CalculatedJavaFile javaFile : Globals.getJavaFiles()) {
							if (javaFile.getPath().equals(diffEntry.getOldFilePath())) {
								javaFile.setPath(diffEntry.getNewFilePath());
							}
						}
						finalProject.getJavaFiles().stream().filter(file -> file.getPath().equals(diffEntry.getOldFilePath())).forEach(file -> file.setPath(diffEntry.getNewFilePath()));
					});
			project.setJavaFiles(finalProject.getJavaFiles());
		}
		return project;
	}
	
	
	/**
	 * Get Metrics from Metrics Calculator for every java file (initial calculation)
	 *
	 * @param project the project we are referring to
	 */
	public Project setMetrics(Project project, Revision currentRevision) throws Exception {
		MetricsCalculator mc = new MetricsCalculator(project, currentRevision);
		int resultCode = mc.start();
		if (resultCode == -1)
			throw new IllegalStateException("Something went wrong with Metrics Calculator!!!\nProbably first commit has no sources to analyze, or the project structure is not supported!");
		project = mc.getProject();
		String st = mc.printResults();
		String[] s = st.split("\\r?\\n");
		try {
			for (int i = 1; i < s.length; ++i) {
				String[] column = s[i].split("\t");
				String filePath = column[0];
				CalculatedJavaFile jf;
				if (Globals.getJavaFiles().stream().noneMatch(javaFile -> javaFile.getPath().equals(filePath.replace("\\", "/")))) {
					jf = project.getJavaFiles().stream().filter(file -> file.getPath().equals(filePath)).collect(Collectors.toList()).get(0);
					registerMetrics(column, jf);
					Globals.addJavaFile(jf);
				} else {
					jf = getAlreadyDefinedFile(filePath);
					if (Objects.nonNull(jf)) {
						registerMetrics(column, jf);
					}
				}
			}
			return project;
		} catch (Exception ignored) {
			throw new IllegalStateException("ERROR_TO_BE_DESCRIBED_HERE in setMetrics(Project project, Revision currentRevision)");
		}
	}
	
	/**
	 * Get Metrics from Metrics Calculator for specific java files (new or modified)
	 *
	 * @param project         the project we are referring to
	 * @param currentRevision the revision we are analysing
	 * @param jfs             the list of java files
	 */
	public Project setMetrics(Project project, Revision currentRevision, Set<String> jfs) throws Exception {
		if (jfs.isEmpty())
			throw new IllegalStateException("Java Files Set is empty!!!");
		MetricsCalculator mc = new MetricsCalculator(project, currentRevision);
		int resultCode = mc.start(jfs);
		if (resultCode == -1)
			throw new IllegalStateException("Something went wrong with Metrics Calculator!!!");
		project = mc.getProject();
		String st = mc.printResults(jfs);
		String[] s = st.split("\\r?\\n");
		Set<CalculatedJavaFile> toCalculate = new HashSet<>();
		project.getJavaFiles().forEach(javaFile -> {
			if (jfs.contains(javaFile.getPath()) && javaFile.getId() == null) {
				toCalculate.add(javaFile);
			}
		});
		for (int i = 1; i < s.length; ++i) {
			String[] column = s[i].split("\t");
			String filePath = column[0];
			CalculatedJavaFile jf;
			if (Globals.getJavaFiles().stream().noneMatch(javaFile -> javaFile.getPath().equals(filePath.replace("\\", "/")))) {
				jf = project.getJavaFiles().stream().filter(file -> file.getPath().equals(filePath)).collect(Collectors.toList()).get(0);
				registerMetrics(column, jf);
				Globals.addJavaFile(jf);
			} else {
				jf = getAlreadyDefinedFile(filePath);
				if (Objects.nonNull(jf)) {
					registerMetrics(column, jf);
				}
			}
		}
		toCalculate.forEach(CalculatedJavaFile::calculateInterest);
		return project;
	}
	
	/**
	 * Register Metrics to specified java file
	 *
	 * @param calcEntries entries taken from MetricsCalculator's results
	 * @param jf          the java file we are registering metrics to
	 */
	private void registerMetrics(String[] calcEntries, CalculatedJavaFile jf) {
		jf.getQualityMetrics().setClassesNum(jf.getClasses().size());
		jf.getQualityMetrics().setWMC(Double.parseDouble(calcEntries[2]));
		jf.getQualityMetrics().setDIT(Integer.parseInt(calcEntries[3]));
		jf.getQualityMetrics().setComplexity(Double.parseDouble(calcEntries[4]));
		jf.getQualityMetrics().setLCOM(Double.parseDouble(calcEntries[5]));
		jf.getQualityMetrics().setMPC(Double.parseDouble(calcEntries[6]));
		jf.getQualityMetrics().setNOM(Double.parseDouble(calcEntries[7]));
		jf.getQualityMetrics().setRFC(Double.parseDouble(calcEntries[8]));
		jf.getQualityMetrics().setDAC(Integer.parseInt(calcEntries[9]));
		jf.getQualityMetrics().setNOCC(Integer.parseInt(calcEntries[10]));
		jf.getQualityMetrics().setCBO(Double.parseDouble(calcEntries[11]));
		jf.getQualityMetrics().setSIZE1(Integer.parseInt(calcEntries[12]));
		jf.getQualityMetrics().setSIZE2(Integer.parseInt(calcEntries[13]));
	}
	
	public boolean parameterExists(Object parameter) {
		if ((parameter instanceof String)) {
			return !(((String) parameter).isBlank() || ((String) parameter).isEmpty());
		}
		return false;
	}
	
	public String preprocessURL(String url) {
		if (url == null) {
			return null;
		}
		return url.replaceAll("(\\.git/|\\.git|/$)", "");
	}
	
}
