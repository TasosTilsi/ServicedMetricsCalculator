package tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "java_files")
public class JavaFilesEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "java_file_id", nullable = false)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "project_id")
	@JsonIgnore
	private ProjectEntity projectEntity;
	
	@OneToOne
	@JoinColumn(name = "td_interest")
	@JsonIgnore
	private TDInterestEntity tdInterestEntity;
	
	@OneToMany(mappedBy = "javaFilesEntity")
	@JsonIgnore
	private final Set<ClassesEntity> classesEntities = new java.util.LinkedHashSet<>();
	
	@OneToOne
	@JoinColumn(name = "td_interest")
	private KappaEntity kappaEntity;
}
