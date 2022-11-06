package tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
//@Entity
//@Table(name = "revision")
public class RevisionEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;
	
	@Column(name = "sha", nullable = false)
	private String sha;
	
	@Column(name = "revisionCount", nullable = false)
	private Integer revisionCount;
}
