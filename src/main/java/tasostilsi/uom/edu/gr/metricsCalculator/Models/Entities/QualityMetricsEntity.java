package tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities;

import javax.persistence.*;

@Entity
@Table(name = "metrics")
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
	
	public RevisionEntity getRevisionEntity() {
		return revisionEntity;
	}
	
	public void setRevisionEntity(RevisionEntity revisionEntity) {
		this.revisionEntity = revisionEntity;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Integer getClassesNum() {
		return classesNum;
	}
	
	public void setClassesNum(Integer classesNum) {
		this.classesNum = classesNum;
	}
	
	public Double getComplexity() {
		return complexity;
	}
	
	public void setComplexity(Double complexity) {
		this.complexity = complexity;
	}
	
	public Integer getDIT() {
		return DIT;
	}
	
	public void setDIT(Integer DIT) {
		this.DIT = DIT;
	}
	
	public Integer getNOCC() {
		return NOCC;
	}
	
	public void setNOCC(Integer NOCC) {
		this.NOCC = NOCC;
	}
	
	public Double getRFC() {
		return RFC;
	}
	
	public void setRFC(Double RFC) {
		this.RFC = RFC;
	}
	
	public Double getLCOM() {
		return LCOM;
	}
	
	public void setLCOM(Double LCOM) {
		this.LCOM = LCOM;
	}
	
	public Double getWMC() {
		return WMC;
	}
	
	public void setWMC(Double WMC) {
		this.WMC = WMC;
	}
	
	public Double getNOM() {
		return NOM;
	}
	
	public void setNOM(Double NOM) {
		this.NOM = NOM;
	}
	
	public Double getMPC() {
		return MPC;
	}
	
	public void setMPC(Double MPC) {
		this.MPC = MPC;
	}
	
	public Integer getDAC() {
		return DAC;
	}
	
	public void setDAC(Integer DAC) {
		this.DAC = DAC;
	}
	
	public Double getCBO() {
		return CBO;
	}
	
	public void setCBO(Double CBO) {
		this.CBO = CBO;
	}
	
	public Integer getSIZE1() {
		return SIZE1;
	}
	
	public void setSIZE1(Integer SIZE1) {
		this.SIZE1 = SIZE1;
	}
	
	public Integer getSIZE2() {
		return SIZE2;
	}
	
	public void setSIZE2(Integer SIZE2) {
		this.SIZE2 = SIZE2;
	}
	
	public Integer getOldSIZE1() {
		return oldSIZE1;
	}
	
	public void setOldSIZE1(Integer oldSIZE1) {
		this.oldSIZE1 = oldSIZE1;
	}
}
