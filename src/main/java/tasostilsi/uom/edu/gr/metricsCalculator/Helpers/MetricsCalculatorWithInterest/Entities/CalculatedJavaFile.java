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

package tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.Revision;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Metrics.Kappa;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Metrics.QualityMetrics;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Metrics.TDInterest;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@Entity
@Table(name = "java_files")
@Transactional(isolation = Isolation.SERIALIZABLE)
public class CalculatedJavaFile {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "project_id")
	@JsonIgnore
	private Project project;
	@Embedded
	private TDInterest interest;
	@OneToMany(mappedBy = "javaFile", cascade = CascadeType.ALL)
	@JsonIgnore
	private Set<CalculatedClass> classes;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "quality_metrics_id")
	private QualityMetrics qualityMetrics;
	
	@Embedded
	private Kappa k;
	
	@Column(name = "path", nullable = false)
	private String path;
	
	public CalculatedJavaFile(String path, Set<CalculatedClass> classes) {
		this.path = path;
		this.classes = classes;
		this.qualityMetrics = new QualityMetrics();
		this.interest = new TDInterest(this);
		this.k = new Kappa(qualityMetrics.getRevision(), this);
		this.classes.forEach(calculatedClass -> {
			calculatedClass.getQualityMetrics().setRevision(qualityMetrics.getRevision());
			calculatedClass.setJavaFile(this);
		});
	}
	
	public CalculatedJavaFile(String path, QualityMetrics qualityMetrics) {
		this.path = path;
		this.qualityMetrics = qualityMetrics;
		this.interest = new TDInterest(this);
		this.k = new Kappa(qualityMetrics.getRevision(), this);
	}
	
	public CalculatedJavaFile(String path) {
		this.path = path;
		this.qualityMetrics = new QualityMetrics();
		this.classes = new HashSet<>();
		this.interest = new TDInterest(this);
	}
	
	public CalculatedJavaFile(String path, Revision revision) {
		this.path = path;
		this.classes = new HashSet<>();
		this.qualityMetrics = new QualityMetrics(revision);
		this.interest = new TDInterest(this);
		this.k = new Kappa(qualityMetrics.getRevision(), this);
	}
	
	public CalculatedJavaFile(String path, Revision revision, Set<CalculatedClass> classes) {
		this.path = path;
		this.classes = classes;
		this.qualityMetrics = new QualityMetrics(revision);
		this.interest = new TDInterest(this);
		this.k = new Kappa(qualityMetrics.getRevision(), this);
		this.classes.forEach(calculatedClass -> {
			calculatedClass.getQualityMetrics().setRevision(revision);
			calculatedClass.setJavaFile(this);
		});
	}
	
	public CalculatedJavaFile(String path,
	                          QualityMetrics qualityMetrics,
	                          BigDecimal interestInEuros,
	                          BigDecimal interestInHours,
	                          BigDecimal interestInAvgLOC,
	                          BigDecimal avgInterestPerLOC,
	                          BigDecimal sumInterestPerLOC,
	                          Double kappa,
	                          Set<CalculatedClass> classes,
	                          Revision revision) {
		this.path = path;
		this.qualityMetrics = qualityMetrics;
		this.interest = new TDInterest(this, interestInEuros, interestInHours, interestInAvgLOC, avgInterestPerLOC, sumInterestPerLOC);
		this.k = new Kappa(qualityMetrics.getRevision(), kappa, this);
		this.classes = classes;
		this.classes.forEach(calculatedClass -> {
			calculatedClass.getQualityMetrics().setRevision(revision);
			calculatedClass.setJavaFile(this);
		});
	}
	
	public void aggregateMetrics() {
		for (CalculatedClass aClass : getClasses()) {
			getQualityMetrics().add(aClass.getQualityMetrics());
		}
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public QualityMetrics getQualityMetrics() {
		return qualityMetrics;
	}
	
	public void setQualityMetrics(QualityMetrics qualityMetrics) {
		this.qualityMetrics = qualityMetrics;
	}
	
	public Set<CalculatedClass> getClasses() {
		return classes;
	}
	
	public void setClasses(Set<CalculatedClass> classes) {
		this.classes = classes;
	}
	
	public String getClassNames() {
		StringBuilder classesAsStringBuilder = new StringBuilder();
		for (CalculatedClass aClass : this.getClasses()) {
			classesAsStringBuilder.append(aClass.getQualifiedName()).append(",");
		}
		String classesAsString = classesAsStringBuilder.toString();
		return classesAsString.isEmpty() ? "" : classesAsString.substring(0, classesAsString.length() - 1);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CalculatedJavaFile javaFile = (CalculatedJavaFile) o;
		return Objects.equals(path, javaFile.path) && Objects.equals(qualityMetrics, javaFile.qualityMetrics);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(path, qualityMetrics);
	}
	
	@Override
	public String toString() {
		return this.getPath();
	}
	
	public void calculateInterest() {
		this.getK().update(this.getQualityMetrics().getOldSIZE1());
		this.getInterest().calculate();
	}
	
	public TDInterest getInterest() {
		return this.interest;
	}
	
	public void setInterest(TDInterest interest) {
		this.interest = interest;
	}
	
	public Kappa getK() {
		return this.k;
	}
	
	public void setK(Kappa k) {
		this.k = k;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Project getProject() {
		return project;
	}
	
	public void setProject(Project project) {
		this.project = project;
	}
}
