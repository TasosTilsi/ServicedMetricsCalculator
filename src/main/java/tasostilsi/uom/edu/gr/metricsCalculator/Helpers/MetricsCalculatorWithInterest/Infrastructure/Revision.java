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

package tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure;

import lombok.Data;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@Data
@AttributeOverride(name = "sha", column = @Column(name = "revision_sha"))
@AttributeOverride(name = "count", column = @Column(name = "revision_count"))
@Transactional(isolation = Isolation.SERIALIZABLE)
public class Revision {
	
	private String sha;
	
	private Integer count;
	
	public Revision() {
		this.sha = null;
		this.count = null;
	}
	
	public Revision(String sha, Integer count) {
		this.sha = sha;
		this.count = count;
	}
	
	public String getSha() {
		return sha;
	}
	
	public void setSha(String sha) {
		this.sha = sha;
	}
	
	public Integer getCount() {
		return count;
	}
	
	public void setCount(Integer count) {
		this.count = count;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Revision revision = (Revision) o;
		return Objects.equals(sha, revision.sha) && Objects.equals(count, revision.count);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(sha, count);
	}
}
