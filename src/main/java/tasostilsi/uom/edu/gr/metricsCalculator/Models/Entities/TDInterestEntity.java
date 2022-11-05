package tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities;


import javax.persistence.*;

@Entity
@Table(name = "td_interest")
public class TDInterestEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;
	@Column(name = "interestInEuros")
	private Double interestInEuros;
	@Column(name = "interestInHours")
	private Double interestInHours;
	@Column(name = "interestInAvgLOC")
	private Double interestInAvgLOC;
	@Column(name = "avgInterestPerLOC")
	private Double avgInterestPerLOC;
	@Column(name = "sumInterestPerLOC")
	private Double sumInterestPerLOC;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Double getInterestInEuros() {
		return interestInEuros;
	}
	
	public void setInterestInEuros(Double interestInEuros) {
		this.interestInEuros = interestInEuros;
	}
	
	public Double getInterestInHours() {
		return interestInHours;
	}
	
	public void setInterestInHours(Double interestInHours) {
		this.interestInHours = interestInHours;
	}
	
	public Double getInterestInAvgLOC() {
		return interestInAvgLOC;
	}
	
	public void setInterestInAvgLOC(Double interestInAvgLOC) {
		this.interestInAvgLOC = interestInAvgLOC;
	}
	
	public Double getAvgInterestPerLOC() {
		return avgInterestPerLOC;
	}
	
	public void setAvgInterestPerLOC(Double avgInterestPerLOC) {
		this.avgInterestPerLOC = avgInterestPerLOC;
	}
	
	public Double getSumInterestPerLOC() {
		return sumInterestPerLOC;
	}
	
	public void setSumInterestPerLOC(Double sumInterestPerLOC) {
		this.sumInterestPerLOC = sumInterestPerLOC;
	}
}
