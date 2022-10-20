package tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Metrics;

import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedJavaFile;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.Revision;

public class Kappa {
	
	private final CalculatedJavaFile javaFile;
	private Double value;
	private Revision revision;
	
	public Kappa(Revision revision, CalculatedJavaFile javaFile) {
		this.revision = revision;
		this.setValue(0.0);
		this.javaFile = javaFile;
	}
	
	public Kappa(Revision currentRevision, Double value, CalculatedJavaFile javaFile) {
		this.revision = currentRevision;
		this.setValue(value);
		this.javaFile = javaFile;
	}
	
	public void update(Integer oldLOC) {
		this.setValue((this.getValue() * (getRevision().getRevisionCount() - 1) + (Math.abs(javaFile.getQualityMetrics().getSIZE1() - oldLOC))) / getRevision().getRevisionCount());
	}
	
	public Double getValue() {
		return this.value;
	}
	
	public void setValue(Double newVal) {
		this.value = newVal;
	}
	
	public Revision getRevision() {
		return revision;
	}
	
	public void setRevision(Revision revision) {
		this.revision = revision;
	}
}
