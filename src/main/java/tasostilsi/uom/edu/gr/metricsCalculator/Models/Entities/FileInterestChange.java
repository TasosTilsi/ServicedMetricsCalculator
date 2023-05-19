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
import java.util.Objects;

public class FileInterestChange {
    @JsonProperty("Revision")
    private Long revisionCount;
    @JsonProperty("File Path")
    private String filePath;
    @JsonProperty("Changed Interest (In €)")
    private BigDecimal changeEu;
    @JsonProperty("Changed Interest (In Hours)")
    private BigDecimal changeHours;
    @JsonProperty("Change Between Revisions (In %)")
    private BigDecimal changePercentage;

    public FileInterestChange() { }
    
    public FileInterestChange(Long revisionCount, String filePath, BigDecimal changeEu, BigDecimal changeHours, BigDecimal changePercentage) {
        this.revisionCount = revisionCount;
        this.filePath = filePath;
        this.changeEu = changeEu;
        this.changeHours = changeHours;
        this.changePercentage = changePercentage;
    }

    public Long getRevisionCount() {
        return revisionCount;
    }

    public void setRevisionCount(Long revisionCount) {
        this.revisionCount = revisionCount;
    }

    public BigDecimal getChangeEu() {
        return changeEu;
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
        return changeHours;
    }

    public void setChangeHours(BigDecimal changeHours) {
        this.changeHours = changeHours;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileInterestChange that = (FileInterestChange) o;
        return Objects.equals(revisionCount, that.revisionCount) && Objects.equals(filePath, that.filePath) && Objects.equals(changeEu, that.changeEu) && Objects.equals(changeHours, that.changeHours) && Objects.equals(changePercentage, that.changePercentage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(revisionCount, filePath, changeEu, changeHours, changePercentage);
    }
}
