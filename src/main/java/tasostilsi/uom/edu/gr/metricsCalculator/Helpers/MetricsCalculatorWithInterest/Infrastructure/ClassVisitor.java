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

import ch.qos.logback.classic.Logger;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import org.slf4j.LoggerFactory;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedClass;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedJavaFile;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Metrics.QualityMetrics;

import java.util.AbstractSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class ClassVisitor extends VoidVisitorAdapter<Void> {
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ClassVisitor.class);
	private final Set<String> efferentCoupledClasses = ConcurrentHashMap.newKeySet();
	private final List<TreeSet<String>> methodIntersection = new CopyOnWriteArrayList<>();
	
	private final Set<String> responseSet = ConcurrentHashMap.newKeySet();
	private final Set<String> methodsCalled = ConcurrentHashMap.newKeySet();
	
	private final String filePath;
	private final TypeDeclaration<?> javaClass;
	
	private final Set<CalculatedJavaFile> javaFiles;
	
	public ClassVisitor(Set<CalculatedJavaFile> javaFiles, String filePath, ClassOrInterfaceDeclaration javaClass) {
		this.javaFiles = javaFiles;
		this.filePath = filePath;
		this.javaClass = javaClass;
	}
	
	public ClassVisitor(Set<CalculatedJavaFile> javaFiles, String filePath, EnumDeclaration javaClass) {
		this.javaFiles = javaFiles;
		this.filePath = filePath;
		this.javaClass = javaClass;
	}
	
	@Override
	public void visit(EnumDeclaration javaClass, Void arg) {
		if (javaFiles.stream().anyMatch(javaFile -> javaFile.getPath().equals(filePath))) {
			CalculatedJavaFile jf = javaFiles
					.stream()
					.filter(javaFile -> javaFile.getPath().equals(filePath)).findFirst().get();
			
			if (javaClass.getFullyQualifiedName().isPresent()) {
				CalculatedClass currentClassObject = jf.getClasses().stream().filter(cl -> cl.getQualifiedName().equals(javaClass.getFullyQualifiedName().get())).findFirst().get();
				
				investigateExtendedTypes();
				visitAllClassMethods();
				
				calculateClassMetrics(currentClassObject);
			}
		}
	}
	
	@Override
	public void visit(ClassOrInterfaceDeclaration javaClass, Void arg) {
		if (javaFiles.stream().anyMatch(javaFile -> javaFile.getPath().equals(filePath))) {
			CalculatedJavaFile jf = javaFiles
					.stream()
					.filter(javaFile -> javaFile.getPath().equals(filePath)).filter(javaFile -> javaFile.getId() == null).findFirst().get();
			
			if (jf == null) {
				jf = javaFiles
						.stream()
						.filter(javaFile -> javaFile.getPath().equals(filePath)).findFirst().get();
			}
			
			if (javaClass.getFullyQualifiedName().isPresent()) {
				CalculatedClass currentClassObject = jf.getClasses().stream().filter(cl -> cl.getQualifiedName().equals(javaClass.getFullyQualifiedName().get())).findFirst().get();
				
				investigateExtendedTypes();
				visitAllClassMethods();
				
				calculateClassMetrics(currentClassObject);
				
			}
		}
	}
	
	private void calculateClassMetrics(CalculatedClass currentClassObject) {
		QualityMetrics metrics = currentClassObject.getQualityMetrics();
		metrics.setComplexity(calculateCC());
		metrics.setLCOM((double) calculateLCOM());
		metrics.setSIZE1(calculateSize1());
		metrics.setSIZE2(calculateSize2());
		metrics.setMPC(calculateMPC());
		metrics.setWMC(calculateWmc());
		double wmc = metrics.getWMC();
		metrics.setRFC(calculateRFC(wmc));
		metrics.setDAC(calculateDac());
		metrics.setCBO((double) efferentCoupledClasses.size());
		metrics.setDIT(calculateDit());
		metrics.setNOM(wmc);
	}
	
	/**
	 * Calculate MPC metric value for the class we are referring to
	 *
	 * @return DIT metric value
	 */
	private double calculateMPC() {
		for (MethodCallExpr methodCallExpr : javaClass.findAll(MethodCallExpr.class)) {
			try {
				methodsCalled.add(methodCallExpr.resolve().getQualifiedName());
			} catch (Throwable ignored) {
			}
		}
		return methodsCalled.size();
	}
	
	/**
	 * Calculate RFC metric value for the class we are referring to
	 *
	 * @return DIT metric value
	 */
	private double calculateRFC(double wmc) {
		return wmc + methodsCalled.size();
	}
	
	/**
	 * Visit all class methods & register metrics values
	 */
	private void visitAllClassMethods() {
		javaClass.getMethods()
				.forEach(this::visitMethod);
	}
	
	/**
	 * Calculate DIT metric value for the class we are referring to
	 *
	 * @return DIT metric value
	 */
	private int calculateDit() {
		try {
			return javaClass.resolve().getAllAncestors().size();
		} catch (Throwable t) {
			return 0;
		}
	}
	
	/**
	 * Calculate CC (Cyclomatic Complexity) metric value for
	 * the class we are referring to
	 *
	 * @return CC metric value
	 */
	private double calculateCC() {
		
		float totalIfs = 0.0f;
		int validClasses = 0;
		
		for (MethodDeclaration method : javaClass.getMethods()) {
			int ifs;
			if (!method.isAbstract() && !method.isNative()) {
				ifs = countIfs(method) + countSwitch(method) + 1;
				totalIfs += ifs;
				++validClasses;
			}
		}
		if (javaClass.getConstructors().isEmpty())
			++validClasses;
		
		return validClasses > 0 ? (totalIfs / validClasses) : -1;
	}
	
	/**
	 * Count how many switch statements there are within a method
	 *
	 * @param method the method we are referring to
	 * @return switch count
	 */
	private int countSwitch(MethodDeclaration method) {
		final int[] count = {0};
		method.findAll(SwitchStmt.class).stream().forEach(switchStmt -> count[0] += switchStmt.getEntries().size());
		return count[0];
	}
	
	/**
	 * Count how many if statements there are within a method
	 *
	 * @param method the method we are referring to
	 * @return if count
	 */
	private int countIfs(MethodDeclaration method) {
		return method.findAll(IfStmt.class).size();
	}
	
	/**
	 * Calculate Size1 (LOC) metric value for
	 * the class we are referring to
	 *
	 * @return Size1 metric value
	 */
	private int calculateSize1() {
		int size = 0;
		for (BodyDeclaration<?> member : javaClass.getMembers())
			if (member.getBegin().isPresent() && member.getEnd().isPresent())
				size += member.getEnd().get().line - member.getBegin().get().line;
		return size;
	}
	
	/**
	 * Calculate Size2 (Fields + Methods size) metric value for
	 * the class we are referring to
	 *
	 * @return Size2 metric value
	 */
	private int calculateSize2() {
		return javaClass.getFields().size() + javaClass.getMethods().size();
	}
	
	/**
	 * Calculate DAC metric value for
	 * the class we are referring to
	 *
	 * @return DAC metric value
	 */
	private int calculateDac() {
		int dac = 0;
		for (FieldDeclaration field : javaClass.getFields()) {
			if (field.getElementType().isPrimitiveType())
				continue;
			String typeName;
			try {
				typeName = field.getElementType().resolve().asReferenceType().getQualifiedName();
			} catch (Throwable t) {
				continue;
			}
			if (withinAnalysisBounds(typeName)) {
				++dac;
			}
		}
		return dac;
	}
	
	/**
	 * Calculate LCOM metric value for
	 * the class we are referring to
	 *
	 * @return LCOM metric value
	 */
	private int calculateLCOM() {
		javaClass.getMethods().stream().forEach(methodDeclaration -> methodIntersection.add(new TreeSet<>()));
		int lcom = 0;
		for (int i = 0; i < methodIntersection.size(); ++i) {
			for (int j = i + 1; j < methodIntersection.size(); ++j) {
				AbstractSet<String> intersection = (TreeSet<String>) (methodIntersection.get(i)).clone();
				if ((!intersection.isEmpty()) || (!methodIntersection.isEmpty())) {
					intersection.retainAll(methodIntersection.get(j));
					if (intersection.isEmpty())
						++lcom;
					else
						--lcom;
				}
			}
		}
		return methodIntersection.isEmpty() ? -1 : Math.max(lcom, 0);
	}
	
	private double calculateWmc() {
		return javaClass.getMethods().stream().filter(methodDeclaration -> !methodDeclaration.isConstructorDeclaration()).count();
	}
	
	/**
	 * Register field access
	 *
	 * @param fieldName the field we are referring to
	 */
	private void registerFieldAccess(String fieldName) {
		registerCoupling(javaClass.resolve().getQualifiedName());
		this.methodIntersection.get(this.methodIntersection.size() - 1).add(fieldName);
	}
	
	/**
	 * Register coupling of java class given
	 *
	 * @param className class name coupled with
	 *                  the class we are referring to
	 */
	private void registerCoupling(String className) {
		efferentCoupledClasses.add(className);
	}
	
	/**
	 * Register extended types for the class we are referring to
	 */
	private void investigateExtendedTypes() {
		for (ClassOrInterfaceType extendedType : ((ClassOrInterfaceDeclaration) javaClass).getExtendedTypes()) {
			String extendedTypeQualifiedName;
			try {
				extendedTypeQualifiedName = extendedType.resolve().asReferenceType().getQualifiedName();
			} catch (Throwable ignored) {
				return;
			}
			registerCoupling(extendedTypeQualifiedName);
			CalculatedClass extendedClassObject = findClassByQualifiedName(extendedTypeQualifiedName);
			if (extendedClassObject != null)
				extendedClassObject.getQualityMetrics().setNOCC(extendedClassObject.getQualityMetrics().getNOCC() + 1);
		}
	}
	
	private CalculatedClass findClassByQualifiedName(String classQualifiedName) {
		return javaFiles.stream()
				.filter(javaFile -> javaFile.containsClass(classQualifiedName))
				.findFirst().flatMap(jf -> jf.getClasses().stream()
						.filter(cl -> cl.getQualifiedName().equals(classQualifiedName))
						.findFirst())
				.orElse(null);
	}
	
	/**
	 * Register field access of method given
	 *
	 * @param method the method we are referring to
	 */
	private void investigateFieldAccess(MethodDeclaration method) {
		try {
			method.findAll(NameExpr.class).stream().forEach(expr -> javaClass.getFields().forEach(classField -> classField.getVariables()
					.stream().filter(var -> var.getNameAsString().equals(expr.getNameAsString()))
					.forEach(var -> registerFieldAccess(expr.getNameAsString()))));
		} catch (Throwable ignored) {
		}
	}
	
	/**
	 * Register exception usage of method given
	 *
	 * @param method the method we are referring to
	 */
	private void investigateExceptions(MethodDeclaration method) {
		try {
			for (ResolvedType exception : method.resolve().getSpecifiedExceptions()) {
				registerCoupling(exception.asReferenceType().getQualifiedName());
			}
		} catch (Exception e) {
			// handle the exception appropriately
		}
	}
	
	/**
	 * Register parameters of method given
	 *
	 * @param method the method we are referring to
	 */
	private void investigateParameters(MethodDeclaration method) {
		for (Parameter p : method.getParameters()) {
			try {
				String qualifiedName = p.getType().resolve().asReferenceType().getQualifiedName();
				registerCoupling(qualifiedName);
			} catch (Throwable ignored) {
				// log or handle the exception here
			}
		}
	}
	
	/**
	 * Register invocation of method given
	 *
	 * @param method the method we are referring to
	 */
	private void investigateInvocation(MethodDeclaration method) {
		List<MethodCallExpr> methodCalls = method.findAll(MethodCallExpr.class);
		for (MethodCallExpr methodCall : methodCalls) {
			try {
				ResolvedMethodDeclaration resolvedMethod = methodCall.resolve();
				String packageName = resolvedMethod.getPackageName();
				String className = resolvedMethod.getClassName();
				String qualifiedSignature = resolvedMethod.getQualifiedSignature();
				registerMethodInvocation(String.join(".", packageName, className), qualifiedSignature);
			} catch (Exception e) {
				// Log the exception or rethrow it if necessary
			}
		}
	}
	
	/**
	 * Register method invocation of class given
	 *
	 * @param className the name of the class we are referring to
	 */
	private void registerMethodInvocation(String className, String signature) {
		registerCoupling(className);
		responseSet.add(signature);
		methodsCalled.add(signature);
	}
	
	/**
	 * Visit the method given & register metrics values
	 *
	 * @param method the method of javaClass we are referring to
	 */
	public void visitMethod(MethodDeclaration method) {
		
		try {
			registerCoupling(method.resolve().getReturnType().asReferenceType().getQualifiedName());
		} catch (Throwable ignored) {
		}
		
		responseSet.add(method.resolve().getQualifiedSignature());
		investigateExceptions(method);
		investigateParameters(method);
		investigateInvocation(method);
		investigateFieldAccess(method);
	}
	
	private boolean withinAnalysisBounds(String name) {
		for (CalculatedJavaFile javaFile : javaFiles) {
			if (javaFile.containsClass(name)) {
				return true;
			}
		}
		return false;
	}
	
}
