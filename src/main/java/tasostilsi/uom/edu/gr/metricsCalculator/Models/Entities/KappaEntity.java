package tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities;

import javax.persistence.*;

@Entity
@Table(name = "kappa")
public class KappaEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private Long id;
	
	private Double value;
	
	private RevisionEntity revisionEntity;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
}
