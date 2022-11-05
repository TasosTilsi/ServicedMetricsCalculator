package tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "java_files")
public class JavaFilesEntity {
	
	@OneToMany(mappedBy = "javaFilesEntity")
	@JsonIgnore
	private final Set<ClassesEntity> classesEntities = new java.util.LinkedHashSet<>();
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "java_file_id", nullable = false, updatable = false)
	private Long id;
	@ManyToOne
	@JoinColumn(name = "project_id")
	@JsonIgnore
	private ProjectEntity projectEntity;
	@OneToOne
	@JoinColumn(name = "td_interest")
	@JsonIgnore
	private TDInterestEntity tdInterestEntity;
	@OneToOne
	@JoinColumn(name = "kappa")
	private KappaEntity kappaEntity;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public ProjectEntity getProjectEntity() {
		return projectEntity;
	}
	
	public void setProjectEntity(ProjectEntity projectEntity) {
		this.projectEntity = projectEntity;
	}
	
	public TDInterestEntity getTdInterestEntity() {
		return tdInterestEntity;
	}
	
	public void setTdInterestEntity(TDInterestEntity tdInterestEntity) {
		this.tdInterestEntity = tdInterestEntity;
	}
	
	public Set<ClassesEntity> getClassesEntities() {
		return classesEntities;
	}
	
	public KappaEntity getKappaEntity() {
		return kappaEntity;
	}
	
	public void setKappaEntity(KappaEntity kappaEntity) {
		this.kappaEntity = kappaEntity;
	}
}
