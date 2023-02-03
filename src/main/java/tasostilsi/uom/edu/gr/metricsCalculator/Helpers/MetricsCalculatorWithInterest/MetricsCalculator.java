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

package tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest;

import ch.qos.logback.classic.Logger;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import org.slf4j.LoggerFactory;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedClass;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedJavaFile;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.Project;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.ClassVisitor;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.Revision;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Metrics.QualityMetrics;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class MetricsCalculator {
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(MetricsCalculator.class);
	
	private final Project project;
	private final Revision revision;
	
	public MetricsCalculator(Project project, Revision revision) {
		this.project = project;
		this.revision = revision;
	}
	
	/**
	 * Create the symbol solver
	 * that will be used to identify
	 * user-defined classes
	 */
	private static void createSymbolSolver(String projectDir) {
		TypeSolver javaParserTypeSolver = new JavaParserTypeSolver(new File(projectDir));
		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(javaParserTypeSolver);
		ParserConfiguration parserConfiguration = new ParserConfiguration();
		parserConfiguration
				.setSymbolResolver(symbolSolver)
				.setAttributeComments(false).setDetectOriginalLineSeparator(true);
		StaticJavaParser
				.setConfiguration(parserConfiguration);
	}
	
	/**
	 * Start the whole process
	 *
	 * @return 0 if everything went ok, -1 otherwise
	 */
	public int start() {
		ProjectRoot projectRoot = getProjectRoot(project.getClonePath());
		List<SourceRoot> sourceRoots = projectRoot.getSourceRoots();
		try {
			createSymbolSolver(project.getClonePath());
		} catch (IllegalStateException e) {
			return -1;
		}
		if (createFileSet(sourceRoots) == 0) {
			return -1;
		}
		startCalculations(sourceRoots);
		performAggregation();
		return 0;
	}
	
	/**
	 * Start the whole process
	 *
	 * @return 0 if everything went ok, -1 otherwise
	 */
	public int start(Set<String> filesToAnalyze) {
		ProjectRoot projectRoot = getProjectRoot(project.getClonePath());
		List<SourceRoot> sourceRoots = projectRoot.getSourceRoots();
		try {
			createSymbolSolver(project.getClonePath());
		} catch (IllegalStateException e) {
			return -1;
		}
		if (createFileSet(sourceRoots, filesToAnalyze) == 0) {
			return -1;
		}
		startCalculations(sourceRoots, filesToAnalyze);
		performAggregation(filesToAnalyze);
		return 0;
	}
	
	/**
	 * Aggregates quality metrics
	 */
	private void performAggregation() {
		project.getJavaFiles().forEach(CalculatedJavaFile::aggregateMetrics);
	}
	
	private void performAggregation(Set<String> filesToAnalyze) {
		project.getJavaFiles().stream().parallel().forEach(javaFile -> {
			if (filesToAnalyze.contains(javaFile.getPath()) && javaFile.getQualityMetrics().equals(new QualityMetrics())) {
				javaFile.aggregateMetrics();
			}
		});
	}
	
	/**
	 * Get the project root
	 */
	private ProjectRoot getProjectRoot(String projectDir) {
		return new SymbolSolverCollectionStrategy()
				.collect(Paths.get(projectDir));
	}
	
	/**
	 * Creates the file set (add appropriate classes)
	 *
	 * @param sourceRoots the source roots of project
	 * @return size of the file set (int)
	 */
	private int createFileSet(List<SourceRoot> sourceRoots) {
		try {
			sourceRoots
					.forEach(sourceRoot -> {
						try {
							sourceRoot.tryToParse()
									.stream()
									.parallel()
									.filter(res -> res.getResult().isPresent())
									.filter(cu -> cu.getResult().get().getStorage().isPresent())
									.forEach(cu -> {
										Set<CalculatedClass> classNames = cu.getResult().get().findAll(ClassOrInterfaceDeclaration.class)
												.stream()
												.parallel()
												.filter(classOrInterfaceDeclaration -> classOrInterfaceDeclaration.getFullyQualifiedName().isPresent())
												.map(classOrInterfaceDeclaration -> classOrInterfaceDeclaration.getFullyQualifiedName().get())
												.map(CalculatedClass::new)
												.collect(Collectors.toSet());
										Set<CalculatedClass> enumNames = cu.getResult().get().findAll(EnumDeclaration.class)
												.stream()
												.parallel()
												.filter(enumDeclaration -> enumDeclaration.getFullyQualifiedName().isPresent())
												.map(enumDeclaration -> enumDeclaration.getFullyQualifiedName().get())
												.map(CalculatedClass::new)
												.collect(Collectors.toSet());
										classNames.addAll(enumNames);
										try {
											String path = cu.getResult().get().getStorage().get().getPath().toString().replace("\\", "/").replace(project.getClonePath(), "").substring(1);
//											LOGGER.error("CHECK HERE " + path);
											AtomicReference<CalculatedJavaFile> jfile = new AtomicReference<>();
											jfile.set(new CalculatedJavaFile(path, revision, classNames));
											jfile.get().setProject(project);
											project.getJavaFiles().add(jfile.get());
										} catch (Throwable ignored) {
											LOGGER.error(ignored.getLocalizedMessage());
										}
									});
						} catch (Exception ignored) {
						}
					});
		} catch (Exception ignored) {
			LOGGER.error(ignored.getLocalizedMessage());
		}
		return project.getJavaFiles().size();
	}
	
	private int createFileSet(List<SourceRoot> sourceRoots, Set<String> filesToAnalyze) {
		try {
			sourceRoots
					.forEach(sourceRoot -> {
						try {
							sourceRoot.tryToParse()
									.stream()
									.parallel()
									.filter(res -> res.getResult().isPresent())
									.filter(cu -> cu.getResult().get().getStorage().isPresent())
									.forEach(cu -> {
										Set<CalculatedClass> classNames = cu.getResult().get().findAll(ClassOrInterfaceDeclaration.class)
												.stream()
												.parallel()
												.filter(classOrInterfaceDeclaration -> classOrInterfaceDeclaration.getFullyQualifiedName().isPresent())
												.map(classOrInterfaceDeclaration -> classOrInterfaceDeclaration.getFullyQualifiedName().get())
												.map(CalculatedClass::new)
												.collect(Collectors.toSet());
										Set<CalculatedClass> enumNames = cu.getResult().get().findAll(EnumDeclaration.class)
												.stream()
												.parallel()
												.filter(enumDeclaration -> enumDeclaration.getFullyQualifiedName().isPresent())
												.map(enumDeclaration -> enumDeclaration.getFullyQualifiedName().get())
												.map(CalculatedClass::new)
												.collect(Collectors.toSet());
										classNames.addAll(enumNames);
										try {
											String path = cu.getResult().get().getStorage().get().getPath().toString().replace("\\", "/").replace(project.getClonePath(), "").substring(1);
											if (filesToAnalyze.contains(path)) {
//											LOGGER.error("CHECK HERE " + path);
												AtomicReference<CalculatedJavaFile> jfile = new AtomicReference<>();
												jfile.set(new CalculatedJavaFile(path, revision, classNames));
												jfile.get().setProject(project);
												project.getJavaFiles().add(jfile.get());
											}
										} catch (Throwable ignored) {
											LOGGER.error(ignored.getLocalizedMessage());
										}
									});
						} catch (Exception ignored) {
						}
					});
		} catch (Exception ignored) {
			LOGGER.error(ignored.getLocalizedMessage());
		}
		return project.getJavaFiles().size();
	}
	
	/**
	 * Starts the calculations
	 *
	 * @param sourceRoots the list of source roots of project
	 */
	private void startCalculations(List<SourceRoot> sourceRoots) {
		sourceRoots
				.forEach(sourceRoot -> {
					try {
						sourceRoot.tryToParse()
								.stream()
								.parallel()
								.filter(res -> res.getResult().isPresent())
								.forEach(res -> {
									analyzeCompilationUnit(res.getResult().get());
								});
					} catch (Exception ignored) {
					}
				});
		
	}
	
	/**
	 * Starts the calculations
	 *
	 * @param sourceRoots the list of source roots of project
	 */
	private void startCalculations(List<SourceRoot> sourceRoots, Set<String> filesToAnalyze) {
		sourceRoots
				.forEach(sourceRoot -> {
					try {
						sourceRoot.tryToParse()
								.stream()
								.parallel()
								.filter(res -> res.getResult().isPresent())
								.filter(res -> res.getResult().get().getStorage().isPresent())
								.filter(res -> new ArrayList<>(filesToAnalyze).contains(res.getResult().get().getStorage().get().getPath().toString().replace("\\", "/").replace(project.getClonePath(), "").substring(1)))
								.forEach(res -> {
									analyzeCompilationUnit(res.getResult().get());
								});
					} catch (Exception ignored) {
					}
				});
		
	}
	
	/**
	 * Analyzes the compilation unit given.
	 *
	 * @param cu the compilation unit given
	 */
	private void analyzeCompilationUnit(CompilationUnit cu) {
		analyzeClassOrInterfaces(cu);
		analyzeEnums(cu);
	}
	
	/**
	 * Analyzes the classes (or interfaces) given a compilation unit.
	 *
	 * @param cu the compilation unit given
	 */
	private void analyzeClassOrInterfaces(CompilationUnit cu) {
		cu.findAll(ClassOrInterfaceDeclaration.class).stream().parallel().forEach(cl -> {
			try {
				cl.accept(new ClassVisitor(project.getJavaFiles(), cu.getStorage().get().getPath().toString().replace("\\", "/").replace(project.getClonePath(), "").substring(1), cl), null);
			} catch (Exception ignored) {
			}
		});
	}
	
	/**
	 * Analyzes the enumerations given a compilation unit.
	 *
	 * @param cu the compilation unit given
	 */
	private void analyzeEnums(CompilationUnit cu) {
		cu.findAll(EnumDeclaration.class).stream().parallel().forEach(cl -> {
			try {
				cl.accept(new ClassVisitor(project.getJavaFiles(), cu.getStorage().get().getPath().toString().replace("\\", "/").replace(project.getClonePath(), "").substring(1), cl), null);
			} catch (Exception ignored) {
			}
		});
	}
	
	public String printResults() {
		StringBuilder output = new StringBuilder();
		output.append("FilePath\tClassesNum\tWMC\tDIT\tComplexity\tLCOM\tMPC\tNOM\tRFC\tDAC\tNOCC\tCBO\tSize1\tSize2\tClassNames\n");
		try {
			project.getJavaFiles().forEach(javaFile -> output.append(javaFile.getPath()).append("\t").append(javaFile.getQualityMetrics()).append("\t").append(javaFile.getClassNames()).append("\n"));
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return output.toString();
	}
	
	public String printResults(Set<String> filesToAnalyze) {
		StringBuilder output = new StringBuilder();
		output.append("FilePath\tClassesNum\tWMC\tDIT\tComplexity\tLCOM\tMPC\tNOM\tRFC\tDAC\tNOCC\tCBO\tSize1\tSize2\tClassNames\n");
		try {
			for (String fileToAnalyze : filesToAnalyze) {
				for (CalculatedJavaFile javaFile : project.getJavaFiles()) {
					if (javaFile.getPath().equals(fileToAnalyze) && javaFile.getId() == null)
						output.append(javaFile.getPath()).append("\t").append(javaFile.getQualityMetrics()).append("\t").append(javaFile.getClassNames()).append("\n");
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return output.toString();
	}
	
	public Project getProject() {
		return project;
	}
}
