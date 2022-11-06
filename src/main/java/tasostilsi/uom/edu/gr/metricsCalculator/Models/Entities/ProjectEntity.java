package tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
//@Entity
//@Table(name = "project")
public class ProjectEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;
	
	@Column(name = "url", nullable = false, unique = true)
	private String url;
	
	@Column(name = "owner", nullable = false)
	private String owner;
	
	@Column(name = "repo", nullable = false)
	private String repo;
	
	@Column(name = "path", nullable = false)
	private String clonePath;
	
	@OneToMany(mappedBy = "projectEntity")
	@JsonIgnore
	private Set<JavaFilesEntity> javaFilesEntities = new java.util.LinkedHashSet<>();
	
}
