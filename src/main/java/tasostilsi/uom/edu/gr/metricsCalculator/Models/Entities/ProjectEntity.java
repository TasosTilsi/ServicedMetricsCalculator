package tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.Project;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "project")
public class ProjectEntity {
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;
	@Id
	@Column(name = "url", nullable = false)
	private String gitUrl;
	@Column(name = "owner", nullable = false)
	private String owner;
	@Column(name = "repo", nullable = false)
	private String repo;
	@Column(name = "path", nullable = false)
	private String clonePath;
	
	@OneToMany(mappedBy = "projectEntity")
	@JsonIgnore
	private Set<JavaFilesEntity> javaFilesEntities = new java.util.LinkedHashSet<>();
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getGitUrl() {
		return gitUrl;
	}
	
	public void setGitUrl(String gitUrl) {
		this.gitUrl = gitUrl;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public String getRepo() {
		return repo;
	}
	
	public void setRepo(String repo) {
		this.repo = repo;
	}
	
	public String getClonePath() {
		return clonePath;
	}
	
	public void setClonePath(String clonePath) {
		this.clonePath = clonePath;
	}
	
	public Set<JavaFilesEntity> getJavaFilesEntities() {
		return javaFilesEntities;
	}
	
	public void setJavaFilesEntities(Set<JavaFilesEntity> javaFilesEntities) {
		this.javaFilesEntities = javaFilesEntities;
	}
}
