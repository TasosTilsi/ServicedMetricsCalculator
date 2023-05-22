/*******************************************************************************
 * Copyright (C) 2021-2022 University of Macedonia
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AllFileMetricsAndInterest {
	@JsonIgnore
	private Long revisionCount;
	@JsonProperty("File Path")
	private String filePath;
	@JsonProperty("Interest (In €)")
	private BigDecimal interestEu;
	@JsonProperty("Interest (In Hours)")
	private BigDecimal interestHours;
	@JsonProperty("CBO")
	private Double cbo;
	@JsonProperty("DAC")
	private Integer dac;
	@JsonProperty("DIT")
	private Integer dit;
	@JsonProperty("LCOM")
	private Double lcom;
	@JsonProperty("MPC")
	private Double mpc;
	@JsonProperty("NOCC")
	private Integer nocc;
	@JsonProperty("NOM")
	private Double nom;
	@JsonProperty("RFC")
	private Double rfc;
	@JsonProperty("WMC")
	private Double wmc;
	@JsonProperty("SIZE1")
	private Integer size1;
	@JsonProperty("SIZE2")
	private Integer size2;
	@JsonProperty("Complexity")
	private Double complexity;
	@JsonProperty("Average Interest Per Lines Of Code")
	private BigDecimal avgInterestPerLoC;
	@JsonProperty("Interest In Average Lines Of Code")
	private BigDecimal interestInAvgLoC;
	@JsonProperty("Sum Interest Per Lines Of Code")
	private BigDecimal sumInterestPerLoC;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AllFileMetricsAndInterest that = (AllFileMetricsAndInterest) o;
		return Objects.equals(revisionCount, that.revisionCount) &&
				Objects.equals(filePath, that.filePath) &&
				Objects.equals(interestEu, that.interestEu) &&
				Objects.equals(interestHours, that.interestHours) &&
				Objects.equals(avgInterestPerLoC, that.avgInterestPerLoC) &&
				Objects.equals(interestInAvgLoC, that.interestInAvgLoC) &&
				Objects.equals(sumInterestPerLoC, that.sumInterestPerLoC) &&
				Objects.equals(cbo, that.cbo) &&
				Objects.equals(dac, that.dac) &&
				Objects.equals(dit, that.dit) &&
				Objects.equals(lcom, that.lcom) &&
				Objects.equals(mpc, that.mpc) &&
				Objects.equals(nocc, that.nocc) &&
				Objects.equals(nom, that.nom) &&
				Objects.equals(rfc, that.rfc) &&
				Objects.equals(wmc, that.wmc) &&
				Objects.equals(size1, that.size1) &&
				Objects.equals(size2, that.size2) &&
				Objects.equals(complexity, that.complexity)
				;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(revisionCount,
				filePath,
				interestEu,
				interestHours,
				avgInterestPerLoC,
				interestInAvgLoC,
				sumInterestPerLoC,
				cbo,
				dac,
				dit,
				lcom,
				mpc,
				nocc,
				nom,
				rfc,
				wmc,
				size1,
				size2,
				complexity
		);
	}
}
