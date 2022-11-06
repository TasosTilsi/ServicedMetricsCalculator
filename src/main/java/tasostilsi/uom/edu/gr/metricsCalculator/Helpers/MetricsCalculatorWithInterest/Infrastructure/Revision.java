package tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure;

import lombok.Data;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@Data
@AttributeOverride(name = "sha", column = @Column(name = "revision_sha"))
@AttributeOverride(name = "count", column = @Column(name = "revision_count"))
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
