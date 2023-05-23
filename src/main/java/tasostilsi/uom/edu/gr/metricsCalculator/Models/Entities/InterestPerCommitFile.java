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


public class InterestPerCommitFile {
	@JsonProperty("File Path")
	private String filePath;
	@JsonProperty("Revision")
	private Long revisionCount;
	@JsonProperty("Interest (In €)")
	private BigDecimal interestEu;
	@JsonProperty("Interest (In Hours)")
	private BigDecimal interestHours;
	@JsonProperty("Contribution to Project Interest")
	private BigDecimal interestPercentageOfProject;
	@JsonProperty("Changed Interest (In €)")
	private BigDecimal changeEu;
	@JsonProperty("Changed Interest (In Hours)")
	private BigDecimal changeHours;
	@JsonProperty("Change Between Revisions (In %)")
	private BigDecimal changePercentage;
	
	public InterestPerCommitFile() {
	}
	
	public InterestPerCommitFile(String filePath, Long revisionCount, BigDecimal interestEu, BigDecimal interestHours, BigDecimal interestPercentageOfProject) {
		this.filePath = filePath;
		this.revisionCount = revisionCount;
		this.interestEu = interestEu;
		this.interestHours = interestHours;
		this.interestPercentageOfProject = interestPercentageOfProject;
	}
	
	public InterestPerCommitFile(String filePath, Long revisionCount, BigDecimal interestEu, BigDecimal interestHours, BigDecimal interestPercentageOfProject, BigDecimal changeEu, BigDecimal changeHours, BigDecimal changePercentage) {
		this.filePath = filePath;
		this.revisionCount = revisionCount;
		this.interestEu = interestEu;
		this.interestHours = interestHours;
		this.interestPercentageOfProject = interestPercentageOfProject;
		this.changeEu = changeEu;
		this.changeHours = changeHours;
		this.changePercentage = changePercentage;
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
	
	public BigDecimal getInterestEu() {
		return interestEu.setScale(2, RoundingMode.HALF_UP);
	}
	
	public void setInterestEu(BigDecimal interestEu) {
		this.interestEu = interestEu;
	}
	
	public BigDecimal getInterestHours() {
		return interestHours.setScale(1, RoundingMode.HALF_UP);
	}
	
	public void setInterestHours(BigDecimal interestHours) {
		this.interestHours = interestHours;
	}
	
	public BigDecimal getInterestPercentageOfProject() {
		return interestPercentageOfProject.setScale(5, RoundingMode.HALF_UP);
	}
	
	public void setInterestPercentageOfProject(BigDecimal interestPercentageOfProject) {
		this.interestPercentageOfProject = interestPercentageOfProject;
	}
	
	public BigDecimal getChangeEu() {
		return changeEu;
	}
	
	public void setChangeEu(BigDecimal changeEu) {
		this.changeEu = changeEu;
	}
	
	public BigDecimal getChangeHours() {
		return changeHours;
	}
	
	public void setChangeHours(BigDecimal changeHours) {
		this.changeHours = changeHours;
	}
	
	public BigDecimal getChangePercentage() {
		return changePercentage;
	}
	
	public void setChangePercentage(BigDecimal changePercentage) {
		this.changePercentage = changePercentage;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		InterestPerCommitFile that = (InterestPerCommitFile) o;
		return Objects.equals(filePath, that.filePath) &&
				Objects.equals(revisionCount, that.revisionCount) &&
				Objects.equals(interestEu, that.interestEu) &&
				Objects.equals(interestHours, that.interestHours) &&
				Objects.equals(interestPercentageOfProject, that.interestPercentageOfProject) &&
				Objects.equals(changeEu, that.changeEu) &&
				Objects.equals(changeHours, that.changeHours) &&
				Objects.equals(changePercentage, that.changePercentage);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(filePath,
				revisionCount,
				interestEu,
				interestHours,
				interestPercentageOfProject,
				changeEu,
				changeHours,
				changePercentage);
	}
}
