package tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "classes")
public class ClassesEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "classes_id", nullable = false)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "java_file_id")
	@JsonIgnore
	private JavaFilesEntity javaFilesEntity;
}
