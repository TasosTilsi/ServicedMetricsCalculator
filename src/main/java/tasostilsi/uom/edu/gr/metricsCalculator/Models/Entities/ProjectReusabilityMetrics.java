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

import java.util.Objects;

public class ProjectReusabilityMetrics {
    private String sha;
    private Long revisionCount;
    private Double cbo;
    private Double dit;
    private Double wmc;
    private Double rfc;
    private Double lcom;
    private Double nocc;

    public ProjectReusabilityMetrics() { }

    public ProjectReusabilityMetrics(String sha, Long revisionCount, Double cbo, Double dit, Double wmc, Double rfc, Double lcom, Double nocc) {
        this.sha = sha;
        this.revisionCount = revisionCount;
        this.cbo = cbo;
        this.dit = dit;
        this.wmc = wmc;
        this.rfc = rfc;
        this.lcom = lcom;
        this.nocc = nocc;
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

    public Double getCbo() {
        return cbo;
    }

    public void setCbo(Double cbo) {
        this.cbo = cbo;
    }

    public Double getDit() {
        return dit;
    }

    public void setDit(Double dit) {
        this.dit = dit;
    }

    public Double getWmc() {
        return wmc;
    }

    public void setWmc(Double wmc) {
        this.wmc = wmc;
    }

    public Double getRfc() {
        return rfc;
    }

    public void setRfc(Double rfc) {
        this.rfc = rfc;
    }

    public Double getLcom() {
        return lcom;
    }

    public void setLcom(Double lcom) {
        this.lcom = lcom;
    }

    public Double getNocc() {
        return nocc;
    }

    public void setNocc(Double nocc) {
        this.nocc = nocc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectReusabilityMetrics that = (ProjectReusabilityMetrics) o;
        return Objects.equals(sha, that.sha) && Objects.equals(revisionCount, that.revisionCount) && Objects.equals(cbo, that.cbo) && Objects.equals(dit, that.dit) && Objects.equals(wmc, that.wmc) && Objects.equals(rfc, that.rfc) && Objects.equals(lcom, that.lcom) && Objects.equals(nocc, that.nocc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sha, revisionCount, cbo, dit, wmc, rfc, lcom, nocc);
    }
}
