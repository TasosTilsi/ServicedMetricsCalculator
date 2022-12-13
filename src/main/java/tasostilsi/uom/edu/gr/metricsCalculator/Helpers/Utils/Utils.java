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

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Utils {
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(Utils.class);
	
	private static Utils instance;
	
	private Utils() {
	}
	
	public static Utils getInstance() {
		if (instance == null) {
			instance = new Utils();
		}
		return instance;
	}
	
	/**
	 * Removes those files that are marked as 'DELETED' (new code's call)
	 *
	 * @param diffEntries the modified java files (new, modified, deleted)
	 */
	public Set<CalculatedJavaFile> removeDeletedFiles(Revision currentRevision, Set<DiffEntry> diffEntries) {
		Set<CalculatedJavaFile> deletedFiles = ConcurrentHashMap.newKeySet();
		diffEntries
				.forEach(diffEntry -> {
					deletedFiles.add(new CalculatedJavaFile(diffEntry.getOldFilePath(), currentRevision));
					Globals.getJavaFiles().removeIf(javaFile -> javaFile.getPath().endsWith(diffEntry.getOldFilePath()));
				});
		return deletedFiles;
	}
	
	/**
	 * Finds java file by its path
	 *
	 * @param filePath the file path
	 * @return the java file (JavaFile) whose path matches the given one
	 */
	public CalculatedJavaFile getAlreadyDefinedFile(String filePath) {
		for (CalculatedJavaFile jf : Globals.getJavaFiles())
			if (jf.getPath().equals(filePath))
				return jf;
		return null;
	}
	
	/**
	 * Sets the metrics of new and modified files.
	 *
	 * @param project         the project we are referring to
	 * @param currentRevision the revision we are analysing
	 * @param entity          the entity with the list containing the diff entries received.
	 */
	public Project setMetrics(Project project, Revision currentRevision, PrincipalResponseEntity entity) {
		if (!entity.getDeleteDiffEntries().isEmpty())
			project.getJavaFiles().removeAll(removeDeletedFiles(currentRevision, entity.getDeleteDiffEntries()));
		if (!entity.getAddDiffEntries().isEmpty())
			project = setMetrics(project, currentRevision, entity.getAddDiffEntries().stream().map(DiffEntry::getNewFilePath).collect(Collectors.toSet()));
		if (!entity.getModifyDiffEntries().isEmpty())
			project = setMetrics(project, currentRevision, entity.getModifyDiffEntries().stream().map(DiffEntry::getNewFilePath).collect(Collectors.toSet()));
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
	public Project setMetrics(Project project, Revision currentRevision) {
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
	public Project setMetrics(Project project, Revision currentRevision, Set<String> jfs) {
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
	
}
