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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class NormalizedInterestPerFile {
	@JsonProperty("File Path")
	private String filePath;
	@JsonProperty("Revision")
	private Long revisionCount;
	@JsonProperty("Normalized Interest (In €)")
	private BigDecimal normalizedInterestEu;
	@JsonProperty("Normalized Interest (In Hours)")
	private BigDecimal normalizedInterestHours;
	
	public NormalizedInterestPerFile() {
	}
	
	public NormalizedInterestPerFile(String filePath, Long revisionCount, BigDecimal normalizedInterestEu, BigDecimal normalizedInterestHours) {
		this.filePath = filePath;
		this.revisionCount = revisionCount;
		this.normalizedInterestEu = normalizedInterestEu;
		this.normalizedInterestHours = normalizedInterestHours;
	}
	
	public NormalizedInterestPerFile(String filePath, Long revisionCount, Double normalizedInterestEu, Double normalizedInterestHours) {
		this.filePath = filePath;
		this.revisionCount = revisionCount;
		this.normalizedInterestEu = BigDecimal.valueOf(normalizedInterestEu);
		this.normalizedInterestHours = BigDecimal.valueOf(normalizedInterestHours);
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public Long getRevisionCount() {
		return revisionCount;
	}
	
	public void setRevisionCount(Long revisionCount) {
		this.revisionCount = revisionCount;
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
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NormalizedInterestPerFile that = (NormalizedInterestPerFile) o;
		return Objects.equals(revisionCount, that.revisionCount) && Objects.equals(normalizedInterestEu, that.normalizedInterestEu) && Objects.equals(normalizedInterestHours, that.normalizedInterestHours);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(revisionCount, normalizedInterestEu, normalizedInterestHours);
	}
}
