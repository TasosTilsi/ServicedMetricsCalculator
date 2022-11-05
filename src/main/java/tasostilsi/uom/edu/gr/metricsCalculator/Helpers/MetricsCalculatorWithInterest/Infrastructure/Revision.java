package tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure;

import tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.RevisionEntity;

import java.util.Objects;

public class Revision extends RevisionEntity {
	private String sha;
	private Integer revisionCount;
	
	public Revision() {
		this.sha = null;
		this.revisionCount = null;
	}
	
	public Revision(String sha, Integer revisionCount) {
		this.sha = sha;
		this.revisionCount = revisionCount;
	}
	
	public String getSha() {
		return sha;
	}
	
	public void setSha(String sha) {
		this.sha = sha;
	}
	
	public Integer getRevisionCount() {
		return revisionCount;
	}
	
	public void setRevisionCount(Integer revisionCount) {
		this.revisionCount = revisionCount;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Revision revision = (Revision) o;
		return Objects.equals(sha, revision.sha) && Objects.equals(revisionCount, revision.revisionCount);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(sha, revisionCount);
	}
}
