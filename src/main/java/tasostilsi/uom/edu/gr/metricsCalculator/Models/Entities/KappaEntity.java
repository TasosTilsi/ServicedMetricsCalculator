package tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
//@Entity
//@Table(name = "kappa")
public class KappaEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;
	
	@OneToOne
	@JoinColumn(name = "java_file_id")
	private JavaFilesEntity javaFile;
	
	@Column(name = "value", nullable = false)
	private Double value;
	
	@OneToOne
	@JoinColumn(name = "revision_id")
	private RevisionEntity revisionEntity;
	
}
