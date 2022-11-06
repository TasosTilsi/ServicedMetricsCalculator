package tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
//@Entity
//@Table(name = "java_files")
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
	
}
