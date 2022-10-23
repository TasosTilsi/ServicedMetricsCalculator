package tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities;


import javax.persistence.*;

@Entity
@Table(name = "revision")
public class RevisionEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private Long id;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
}
