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
import java.math.BigInteger;
import java.util.Objects;

public class FileReusabilityMetrics {
    private String sha;
    private Long revisionCount;
    private String filePath;
    private BigDecimal cbo;
    private Integer dit;
    private BigDecimal wmc;
    private BigDecimal rfc;
    private BigDecimal lcom;
    private Integer nocc;

    public FileReusabilityMetrics() { }

    public FileReusabilityMetrics(String sha, Long revisionCount, String filePath, BigDecimal cbo, Integer dit, BigDecimal wmc, BigDecimal rfc, BigDecimal lcom, Integer nocc) {
        this.sha = sha;
        this.revisionCount = revisionCount;
        this.filePath = filePath;
        this.cbo = cbo;
        this.dit = dit;
        this.wmc = wmc;
        this.rfc = rfc;
        this.lcom = lcom;
        this.nocc = nocc;
    }
    
    public FileReusabilityMetrics(String sha, Long revisionCount, String filePath, Double cbo, Integer dit, Double wmc, Double rfc, Double lcom, Integer nocc) {
        this.sha = sha;
        this.revisionCount = revisionCount;
        this.filePath = filePath;
        this.cbo = BigDecimal.valueOf(cbo);
        this.dit = dit;
        this.wmc = BigDecimal.valueOf(wmc);
        this.rfc = BigDecimal.valueOf(rfc);
        this.lcom = BigDecimal.valueOf(lcom);
        this.nocc = nocc;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
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

    public BigDecimal getCbo() {
        return cbo;
    }

    public void setCbo(BigDecimal cbo) {
        this.cbo = cbo;
    }

    public Integer getDit() {
        return dit;
    }

    public void setDit(Integer dit) {
        this.dit = dit;
    }

    public BigDecimal getWmc() {
        return wmc;
    }

    public void setWmc(BigDecimal wmc) {
        this.wmc = wmc;
    }

    public BigDecimal getRfc() {
        return rfc;
    }

    public void setRfc(BigDecimal rfc) {
        this.rfc = rfc;
    }

    public BigDecimal getLcom() {
        return lcom;
    }

    public void setLcom(BigDecimal lcom) {
        this.lcom = lcom;
    }

    public Integer getNocc() {
        return nocc;
    }

    public void setNocc(Integer nocc) {
        this.nocc = nocc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileReusabilityMetrics that = (FileReusabilityMetrics) o;
        return Objects.equals(sha, that.sha) && Objects.equals(revisionCount, that.revisionCount) && Objects.equals(filePath, that.filePath) && Objects.equals(cbo, that.cbo) && Objects.equals(dit, that.dit) && Objects.equals(wmc, that.wmc) && Objects.equals(rfc, that.rfc) && Objects.equals(lcom, that.lcom) && Objects.equals(nocc, that.nocc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sha, revisionCount, filePath, cbo, dit, wmc, rfc, lcom, nocc);
    }
}
