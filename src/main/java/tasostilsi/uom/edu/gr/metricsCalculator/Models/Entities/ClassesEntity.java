package tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Metrics.QualityMetrics;

import javax.persistence.*;

@Entity
@Table(name = "classes")
public class ClassesEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "classes_id", nullable = false)
	private Long id;
	
	@Column(name = "name", nullable = false)
	private String name;
	
	@ManyToOne
	@JoinColumn(name = "java_file_id")
	@JsonIgnore
	private JavaFilesEntity javaFilesEntity;
	
	
	@OneToOne
	@JoinColumn(name = "quality_metrics_entity_id")
	private QualityMetricsEntity qualityMetricsEntity;
	
	public QualityMetricsEntity getQualityMetricsEntity() {
		return qualityMetricsEntity;
	}
	
	public void setQualityMetricsEntity(QualityMetricsEntity qualityMetricsEntity) {
		this.qualityMetricsEntity = qualityMetricsEntity;
	}
	
	
}
