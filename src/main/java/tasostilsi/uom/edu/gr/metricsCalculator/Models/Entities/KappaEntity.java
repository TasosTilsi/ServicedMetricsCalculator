package tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities;

import javax.persistence.*;

@Entity
@Table(name = "kappa")
public class KappaEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;
	
	@OneToOne
	@JoinColumn(name = "java_file_id")
	private JavaFilesEntity javaFile;
	
	@Column(name = "value", nullable = false)
	private Double value;
	
	@OneToOne
	@JoinColumn(name = "revision_id")
	private RevisionEntity revisionEntity;
	
	public JavaFilesEntity getJavaFile() {
		return javaFile;
	}
	
	public void setJavaFile(JavaFilesEntity javaFile) {
		this.javaFile = javaFile;
	}
	
	public RevisionEntity getRevisionEntity() {
		return revisionEntity;
	}
	
	public void setRevisionEntity(RevisionEntity revisionEntity) {
		this.revisionEntity = revisionEntity;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Double getValue() {
		return value;
	}
	
	public void setValue(Double value) {
		this.value = value;
	}
	
	
}
