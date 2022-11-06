package tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
//@Entity
//@Table(name = "metrics")
public class QualityMetricsEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;
	
	@OneToOne
	@JoinColumn(name = "revision_entity_id")
	private RevisionEntity revisionEntity;
	
	@Column(name = "classesNum")
	private Integer classesNum;
	
	@Column(name = "complexity")
	private Double complexity;
	
	@Column(name = "DIT")
	private Integer DIT;
	
	@Column(name = "NOCC")
	private Integer NOCC;
	
	@Column(name = "RFC")
	private Double RFC;
	
	@Column(name = "LCOM")
	private Double LCOM;
	
	@Column(name = "WMC")
	private Double WMC;
	
	@Column(name = "NOM")
	private Double NOM;
	
	@Column(name = "MPC")
	private Double MPC;
	
	@Column(name = "DAC")
	private Integer DAC;
	
	@Column(name = "CBO")
	private Double CBO;
	
	@Column(name = "SIZE1")
	private Integer SIZE1;
	
	@Column(name = "SIZE2")
	private Integer SIZE2;
	
	@Column(name = "oldSIZE1")
	private Integer oldSIZE1;
	
}
