package tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Metrics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedJavaFile;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.Revision;

import javax.persistence.*;


@Data
@NoArgsConstructor
@AttributeOverride(name = "value", column = @Column(name = "kappa_value"))
@Embeddable
@Transactional(isolation= Isolation.SERIALIZABLE)
public class Kappa {
	
	@Transient
	@JsonIgnore
	private CalculatedJavaFile javaFile;
	
	@Column(name = "kappa_value", nullable = false)
	private Double value;
	
	@Embedded
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
		this.setValue((this.getValue() * (getRevision().getCount() - 1) + (Math.abs(javaFile.getQualityMetrics().getSIZE1() - oldLOC))) / getRevision().getCount());
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
