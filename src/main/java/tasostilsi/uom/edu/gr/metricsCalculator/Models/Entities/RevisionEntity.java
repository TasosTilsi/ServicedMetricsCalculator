package tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities;


import javax.persistence.*;

@Entity
@Table(name = "revision")
public class RevisionEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;
	
	@Column(name = "sha", nullable = false)
	private String sha;
	
	@Column(name = "revisionCount", nullable = false)
	private Integer revisionCount;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
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
}
