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


public class HighInterestFile {
	@JsonProperty("Revision")
    private Long revisionCount;
	@JsonProperty("File Path")
	private String filePath;
	@JsonProperty("Interest (In €)")
	private BigDecimal interestEu;
	@JsonProperty("Interest (In Hours)")
	private BigDecimal interestHours;
	@JsonProperty("Contribution to Project Interest")
	private BigDecimal interestPercentageOfProject;

    public HighInterestFile() { }
	
	public HighInterestFile(Long revisionCount, String filePath, BigDecimal interestEu, BigDecimal interestHours, BigDecimal interestPercentageOfProject) {
		this.revisionCount = revisionCount;
		this.filePath = filePath;
		this.interestEu = interestEu;
		this.interestHours = interestHours;
		this.interestPercentageOfProject = interestPercentageOfProject;
	}
	
	public HighInterestFile(Long revisionCount, String filePath, Double interestEu, Double interestHours, Double interestPercentageOfProject) {
		this.revisionCount = revisionCount;
		this.filePath = filePath;
		this.interestEu = BigDecimal.valueOf(interestEu);
		this.interestHours = BigDecimal.valueOf(interestHours);
		this.interestPercentageOfProject = BigDecimal.valueOf(interestPercentageOfProject);
	}

    public Long getRevisionCount() {
        return revisionCount;
    }

    public void setRevisionCount(Long revisionCount) {
        this.revisionCount = revisionCount;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public BigDecimal getInterestEu() {
        return interestEu.setScale(2, RoundingMode.HALF_UP);
    }

    public void setInterestEu(BigDecimal interestEu) {
        this.interestEu = interestEu;
    }

    public BigDecimal getInterestPercentageOfProject() {
        return interestPercentageOfProject;
    }

    public void setInterestPercentageOfProject(BigDecimal interestPercentageOfProject) {
        this.interestPercentageOfProject = interestPercentageOfProject;
    }

    public BigDecimal getInterestHours() {
        return interestHours.setScale(2, RoundingMode.HALF_UP);
    }

    public void setInterestHours(BigDecimal interestHours) {
        this.interestHours = interestHours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HighInterestFile that = (HighInterestFile) o;
	    return Objects.equals(revisionCount, that.revisionCount) && Objects.equals(filePath, that.filePath) && Objects.equals(interestEu, that.interestEu) && Objects.equals(interestHours, that.interestHours) && Objects.equals(interestPercentageOfProject, that.interestPercentageOfProject);
    }

    @Override
    public int hashCode() {
	    return Objects.hash(revisionCount, filePath, interestEu, interestHours, interestPercentageOfProject);
    }
}
