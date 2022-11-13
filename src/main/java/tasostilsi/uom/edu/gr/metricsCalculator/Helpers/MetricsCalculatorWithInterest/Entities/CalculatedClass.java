package tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Metrics.QualityMetrics;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
		name = "classes"
		,uniqueConstraints = {
				@UniqueConstraint(
						name = "classes_java_file_id_unique",
						columnNames = "java_file_id"
				)
		}
)
public class CalculatedClass {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "classes_id", nullable = false, updatable = false)
	private Long id;
	
	@Column(name = "name", nullable = false)
	private String qualifiedName;
	
	@Transient
	private QualityMetrics qualityMetrics;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "java_file_id")
//	@JsonIgnore
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
