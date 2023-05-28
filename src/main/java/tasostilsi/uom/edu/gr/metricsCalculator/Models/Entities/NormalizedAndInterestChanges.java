/*
 * ******************************************************************************
 *  * Copyright (C) 2022-2023 University of Macedonia
 *  *
 *  * This program and the accompanying materials are made
 *  * available under the terms of the Eclipse Public License 2.0
 *  * which is available at https://www.eclipse.org/legal/epl-2.0/
 *  *
 *  * SPDX-License-Identifier: EPL-2.0
 *  *****************************************************************************
 */

package tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor
@EqualsAndHashCode
public class NormalizedAndInterestChanges {
	@JsonProperty("Revision")
	private Long revisionCount;
	@JsonProperty("Changed Interest (In €)")
	private BigDecimal changeEu;
	@JsonProperty("Changed Interest (In Hours)")
	private BigDecimal changeHours;
	@JsonProperty("Change Between Revisions (In %)")
	private BigDecimal changePercentage;
	@JsonProperty("Normalized Interest (In €)")
	private BigDecimal normalizedInterestEu;
	@JsonProperty("Normalized Interest (In Hours)")
	private BigDecimal normalizedInterestHours;
	
	public NormalizedAndInterestChanges(Long revisionCount,
	                                    BigDecimal changeEu,
	                                    BigDecimal changeHours,
	                                    BigDecimal changePercentage,
	                                    BigDecimal normalizedInterestEu,
	                                    BigDecimal normalizedInterestHours) {
		this.revisionCount = revisionCount;
		this.changeEu = changeEu;
		this.changeHours = changeHours;
		this.changePercentage = changePercentage;
		this.normalizedInterestEu = normalizedInterestEu;
		this.normalizedInterestHours = normalizedInterestHours;
	}
	
	public NormalizedAndInterestChanges(Long revisionCount,
	                                    Double changeEu,
	                                    Double changeHours,
	                                    Double changePercentage,
	                                    Double normalizedInterestEu,
	                                    Double normalizedInterestHours) {
		this.revisionCount = revisionCount;
		this.changeEu = BigDecimal.valueOf(changeEu);
		this.changeHours = BigDecimal.valueOf(changeHours);
		this.changePercentage = BigDecimal.valueOf(changePercentage);
		this.normalizedInterestEu = BigDecimal.valueOf(normalizedInterestEu);
		this.normalizedInterestHours = BigDecimal.valueOf(normalizedInterestHours);
	}
	
	public Long getRevisionCount() {
		return revisionCount;
	}
	
	public void setRevisionCount(Long revisionCount) {
		this.revisionCount = revisionCount;
	}
	
	public BigDecimal getChangeEu() {
		return changeEu.setScale(2, RoundingMode.HALF_UP);
	}
	
	public void setChangeEu(BigDecimal changeEu) {
		this.changeEu = changeEu;
	}
	
	public BigDecimal getChangePercentage() {
		return changePercentage;
	}
	
	public void setChangePercentage(BigDecimal changePercentage) {
		this.changePercentage = changePercentage;
	}
	
	public BigDecimal getChangeHours() {
		return changeHours.setScale(1, RoundingMode.HALF_UP);
	}
	
	public void setChangeHours(BigDecimal changeHours) {
		this.changeHours = changeHours;
	}
	
	public BigDecimal getNormalizedInterestEu() {
		return normalizedInterestEu.setScale(4, RoundingMode.HALF_UP);
	}
	
	public void setNormalizedInterestEu(BigDecimal normalizedInterestEu) {
		this.normalizedInterestEu = normalizedInterestEu;
	}
	
	public BigDecimal getNormalizedInterestHours() {
		return normalizedInterestHours.setScale(4, RoundingMode.HALF_UP);
	}
	
	public void setNormalizedInterestHours(BigDecimal normalizedInterestHours) {
		this.normalizedInterestHours = normalizedInterestHours;
	}
}
