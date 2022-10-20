package tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities;

import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Metrics.QualityMetrics;

import java.util.Objects;

public class CalculatedClass {
	
	
	private String qualifiedName;
	private QualityMetrics qualityMetrics;
	
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
