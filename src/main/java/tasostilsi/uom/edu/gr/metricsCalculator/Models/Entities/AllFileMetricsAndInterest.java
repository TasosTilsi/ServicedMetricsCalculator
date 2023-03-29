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
	private String filePath;
	private BigDecimal interestEu;
	private BigDecimal interestHours;
	private Double cbo;
	private Integer dac;
	private Integer dit;
	private Double lcom;
	private Double mpc;
	private Integer nocc;
	private Double nom;
	private Double rfc;
	private Double wmc;
	private Integer size1;
	private Integer size2;
	private Double complexity;
	private BigDecimal avgInterestPerLoC;
	private BigDecimal interestInAvgLoC;
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
