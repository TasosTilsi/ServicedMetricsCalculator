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

import java.math.BigDecimal;
import java.util.Objects;

public class NormalizedInterest {
    private String sha;
    private Long revisionCount;
    private BigDecimal normalizedInterestEu;
    private BigDecimal normalizedInterestHours;

    public NormalizedInterest() { }

    public NormalizedInterest(String sha, Long revisionCount, BigDecimal normalizedInterestEu, BigDecimal normalizedInterestHours) {
        this.sha = sha;
        this.revisionCount = revisionCount;
        this.normalizedInterestEu = normalizedInterestEu;
        this.normalizedInterestHours = normalizedInterestHours;
    }
    
    public NormalizedInterest(String sha, Long revisionCount, Double normalizedInterestEu, Double normalizedInterestHours) {
        this.sha = sha;
        this.revisionCount = revisionCount;
        this.normalizedInterestEu = BigDecimal.valueOf(normalizedInterestEu);
        this.normalizedInterestHours = BigDecimal.valueOf(normalizedInterestHours);
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public Long getRevisionCount() {
        return revisionCount;
    }

    public void setRevisionCount(Long revisionCount) {
        this.revisionCount = revisionCount;
    }

    public BigDecimal getNormalizedInterestEu() {
        return normalizedInterestEu;
    }

    public void setNormalizedInterestEu(BigDecimal normalizedInterestEu) {
        this.normalizedInterestEu = normalizedInterestEu;
    }

    public BigDecimal getNormalizedInterestHours() {
        return normalizedInterestHours;
    }

    public void setNormalizedInterestHours(BigDecimal normalizedInterestHours) {
        this.normalizedInterestHours = normalizedInterestHours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NormalizedInterest that = (NormalizedInterest) o;
        return Objects.equals(sha, that.sha) && Objects.equals(revisionCount, that.revisionCount) && Objects.equals(normalizedInterestEu, that.normalizedInterestEu) && Objects.equals(normalizedInterestHours, that.normalizedInterestHours);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sha, revisionCount, normalizedInterestEu, normalizedInterestHours);
    }
}
