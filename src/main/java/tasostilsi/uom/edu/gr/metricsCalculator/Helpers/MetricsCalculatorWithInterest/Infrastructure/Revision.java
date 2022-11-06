package tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure;

import lombok.Data;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@Data
@AttributeOverrides({
		@AttributeOverride(name = "sha", column = @Column(name = "revision_sha")),
		@AttributeOverride(name = "revisionCount", column = @Column(name = "revision_count"))
})
public class Revision {

//	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
//	@Column(name = "id", nullable = false, updatable = false)
//	private Long id;
	
	@Column(name = "sha")
	private String sha;
	
	@Column(name = "revisionCount")
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
