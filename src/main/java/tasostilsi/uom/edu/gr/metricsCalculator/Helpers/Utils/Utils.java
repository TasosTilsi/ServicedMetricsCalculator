package tasostilsi.uom.edu.gr.metricsCalculator.Helpers.Utils;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedClass;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedJavaFile;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.Project;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.DiffEntry;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.Globals;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.PrincipalResponseEntity;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.Revision;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.MetricsCalculator;
import tasostilsi.uom.edu.gr.metricsCalculator.Repositories.JavaFilesRepository;
import tasostilsi.uom.edu.gr.metricsCalculator.Repositories.ProjectRepository;

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
	 * Inserts the data of the first revision (in list).
	 *
	 * @param project  the project we are referring to
	 * @param javaFile the javaFile
	 */
	public void insertData(Project project, CalculatedJavaFile javaFile, ProjectRepository projectRepository, JavaFilesRepository javaFilesRepository) {
		if (Globals.getJavaFiles().isEmpty()) {
			//			InsertToDB.insertEmpty(project, currentRevision);  //connection to db need here
			projectRepository.initializeProjectAnalysis(javaFile, project.getUrl());
			
		} else {
			Globals.getJavaFiles().forEach(javaFilesRepository::insertJavaFileToDB); //connection to db need here
//									Globals.getJavaFiles().forEach(jf -> InsertToDB.insertMetricsToDatabase(project, jf, currentRevision));  //connection to db need here
		}
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
			removeDeletedFiles(currentRevision, entity.getDeleteDiffEntries());
		if (!entity.getAddDiffEntries().isEmpty())
			project = setMetrics(project, currentRevision, entity.getAddDiffEntries().stream().map(DiffEntry::getNewFilePath).collect(Collectors.toSet()));
		if (!entity.getModifyDiffEntries().isEmpty())
			project = setMetrics(project, currentRevision, entity.getModifyDiffEntries().stream().map(DiffEntry::getNewFilePath).collect(Collectors.toSet()));
		if (!entity.getRenameDiffEntries().isEmpty())
			entity.getRenameDiffEntries()
					.forEach(diffEntry -> {
						for (CalculatedJavaFile javaFile : Globals.getJavaFiles()) {
							if (javaFile.getPath().equals(diffEntry.getOldFilePath())) {
								javaFile.setPath(diffEntry.getNewFilePath());
							}
						}
					});
		return project;
	}
	
	
	/**
	 * Get Metrics from Metrics Calculator for every java file (initial calculation)
	 *
	 * @param project the project we are referring to
	 */
	public Project setMetrics(Project project, Revision currentRevision) {
		MetricsCalculator mc = new MetricsCalculator(project);
		int resultCode = mc.start();
		if (resultCode == -1)
			throw new IllegalStateException("Something went wrong with Metrics Calculator!!!");
		project = mc.getProject();
		String st = mc.printResults();
		String[] s = st.split("\\r?\\n");
		try {
			Set<CalculatedJavaFile> calculatedJavaFileSet = new HashSet<>(project.getJavaFiles());
//			project.getJavaFiles().clear();
			for (int i = 1; i < s.length; ++i) {
				String[] column = s[i].split("\t");
				String filePath = column[0];
				List<String> classNames;
				
				try {
					classNames = Arrays.asList(column[14].split(","));
				} catch (Throwable e) {
					classNames = new ArrayList<>();
				}
				
				CalculatedJavaFile jf;
				if (Globals.getJavaFiles().stream().noneMatch(javaFile -> javaFile.getPath().equals(filePath.replace("\\", "/")))) {
					Set<CalculatedClass> classes = calculatedJavaFileSet.stream().filter(file -> file.getPath().equals(filePath)).map(CalculatedJavaFile::getClasses).collect(Collectors.toList()).get(0);
					classes.forEach(calculatedClass -> calculatedClass.getQualityMetrics().setRevision(currentRevision));
					jf = project.getJavaFiles().stream().filter(file -> file.getPath().equals(filePath)).collect(Collectors.toList()).get(0);
					jf.setClasses(classes);
					jf.getQualityMetrics().setRevision(currentRevision);
					jf.getK().setRevision(currentRevision);
//					jf = new CalculatedJavaFile(filePath, currentRevision, classes);
					registerMetrics(column, jf, classNames);
//					project.getJavaFiles().add(jf);
					Globals.addJavaFile(jf);
				} else {
					jf = getAlreadyDefinedFile(filePath);
					if (Objects.nonNull(jf)) {
						registerMetrics(column, jf, classNames);
					}
				}
				project.getJavaFiles().add(jf);
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
		MetricsCalculator mc = new MetricsCalculator(project);
		int resultCode = mc.start(jfs);
		if (resultCode == -1)
			throw new IllegalStateException("Something went wrong with Metrics Calculator!!!");
		project = mc.getProject();
		String st = mc.printResults(jfs);
		String[] s = st.split("\\r?\\n");
		
		Set<CalculatedJavaFile> toCalculate = new HashSet<>();
		project.getJavaFiles().forEach(javaFile -> {
			if (jfs.contains(javaFile.getPath())) {
				toCalculate.add(javaFile);
			}
		});
//		project.getJavaFiles().removeAll(toCalculate);
		
		for (int i = 1; i < s.length; ++i) {
			String[] column = s[i].split("\t");
			String filePath = column[0];
			List<String> classNames;
			try {
				classNames = Arrays.asList(column[14].split(","));
			} catch (Throwable e) {
				classNames = new ArrayList<>();
			}
			
			CalculatedJavaFile jf;
			if (Globals.getJavaFiles().stream().noneMatch(javaFile -> javaFile.getPath().equals(filePath.replace("\\", "/")))) {
				Set<CalculatedClass> classes = toCalculate.stream().filter(file -> file.getPath().equals(filePath)).map(CalculatedJavaFile::getClasses).collect(Collectors.toList()).get(0);
				classes.forEach(calculatedClass -> calculatedClass.getQualityMetrics().setRevision(currentRevision));
				jf = project.getJavaFiles().stream().filter(file -> file.getPath().equals(filePath)).collect(Collectors.toList()).get(0);
				jf.setClasses(classes);
				jf.getQualityMetrics().setRevision(currentRevision);
				jf.getK().setRevision(currentRevision);
				registerMetrics(column, jf, classNames);
				Globals.addJavaFile(jf);
				toCalculate.remove(toCalculate.stream().filter(javaFile -> javaFile.getPath().equals(jf.getPath())).collect(Collectors.toList()).get(0));
				toCalculate.add(jf);
			} else {
				jf = getAlreadyDefinedFile(filePath);
				if (Objects.nonNull(jf)) {
					toCalculate.remove(toCalculate.stream().filter(javaFile -> javaFile.getPath().equals(jf.getPath())).collect(Collectors.toList()).get(0));
					toCalculate.add(jf);
					registerMetrics(column, jf, classNames);
				}
			}
		}
		toCalculate.forEach(CalculatedJavaFile::calculateInterest);
		project.getJavaFiles().addAll(toCalculate);
		return project;
	}
	
	/**
	 * Register Metrics to specified java file
	 *
	 * @param calcEntries entries taken from MetricsCalculator's results
	 * @param jf          the java file we are registering metrics to
	 */
	private void registerMetrics(String[] calcEntries, CalculatedJavaFile jf, List<String> classNames) {
//		jf.getQualityMetrics().setClassesNum(Integer.parseInt(calcEntries[1]));
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
		for (String className : classNames)
			jf.addClassName(className);
	}
	
	/**
	 * Deletes source code (if exists) before the analysis
	 * procedure.
	 *
	 * @param file the directory that the repository will be cloned
	 */
	public void deleteSourceCode(File file) throws NullPointerException {
		if (file.isDirectory()) {
			/* If directory is empty, then delete it */
			if (Objects.requireNonNull(file.list()).length == 0)
				file.delete();
			else {
				/* List all the directory contents */
				String[] files = file.list();
				
				for (String temp : files) {
					/* Construct the file structure */
					File fileDelete = new File(file, temp);
					/* Recursive delete */
					deleteSourceCode(fileDelete);
				}
				
				/* Check the directory again, if empty then delete it */
				if (Objects.requireNonNull(file.list()).length == 0)
					file.delete();
			}
		} else {
			/* If file, then delete it */
			file.delete();
		}
	}
}
