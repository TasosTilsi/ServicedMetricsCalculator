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
//@Table(name = "td_interest")
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
	
}
