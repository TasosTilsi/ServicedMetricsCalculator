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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Metrics.QualityMetrics;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "classes")
@Transactional(isolation= Isolation.SERIALIZABLE)
public class CalculatedClass {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "classes_id", nullable = false, updatable = false)
	private Long id;
	
	@Column(name = "name", nullable = false)
	private String qualifiedName;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "quality_metrics_id")
	private QualityMetrics qualityMetrics;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "java_file_id")
	@JsonIgnore
	private CalculatedJavaFile javaFile;
	
	public CalculatedClass(String name) {
		this.qualifiedName = name;
		this.qualityMetrics = new QualityMetrics();
	}
	
	public String getQualifiedName() {
		return qualifiedName;
	}
	
	public void setQualifiedName(String name) {
		this.qualifiedName = name;
	}
	
	public QualityMetrics getQualityMetrics() {
		return qualityMetrics;
	}
	
	public void setQualityMetrics(QualityMetrics qualityMetrics) {
		this.qualityMetrics = qualityMetrics;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CalculatedClass aClass = (CalculatedClass) o;
		return Objects.equals(qualifiedName, aClass.qualifiedName);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(qualifiedName);
	}
	
	@Override
	public String toString() {
		return qualifiedName;
	}
}
