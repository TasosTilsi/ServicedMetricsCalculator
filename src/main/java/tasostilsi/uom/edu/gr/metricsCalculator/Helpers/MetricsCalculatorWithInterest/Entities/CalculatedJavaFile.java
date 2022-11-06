package tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.Revision;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Metrics.Kappa;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Metrics.QualityMetrics;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Metrics.TDInterest;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
@Entity
@Table(
		name = "java_files",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "java_files_project_id_unique",
						columnNames = "project_id"
				)
		}
)
public class CalculatedJavaFile {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "project_id")
	@JsonIgnore
	private Project project;
	
	@Embedded
	private TDInterest interest;
	
	@OneToMany(mappedBy = "javaFile")
	@JsonIgnore
	private Set<CalculatedClass> classes;
	
	@Transient
	private Set<String> classesNames;
	
	@Transient
	private QualityMetrics qualityMetrics;
	
	@Embedded
	private Kappa k;
	
	@Column(name = "path", nullable = false)
	private String path;
	
	public CalculatedJavaFile(String path, Set<CalculatedClass> classes) {
		this.path = path;
		this.qualityMetrics = new QualityMetrics();
		this.classes = classes;
//		this.classes.forEach(c -> {
//			classesNames.add(c.getQualifiedName());
//		});
		this.interest = new TDInterest(this);
	}
	
	public CalculatedJavaFile(String path, QualityMetrics qualityMetrics) {
		this.path = path;
		this.qualityMetrics = qualityMetrics;
		this.interest = new TDInterest(this);
	}
	
	public CalculatedJavaFile(String path) {
		this.path = path;
		this.qualityMetrics = new QualityMetrics();
		this.classes = ConcurrentHashMap.newKeySet();
		this.interest = new TDInterest(this);
	}
	
	public CalculatedJavaFile(String path, Revision revision) {
		this.path = path;
		this.classes = new HashSet<>();
		this.qualityMetrics = new QualityMetrics(revision);
		this.interest = new TDInterest(this);
		this.setK(new Kappa(revision, this));
	}
	
	public CalculatedJavaFile(String path, QualityMetrics qualityMetrics, Double interestInEuros, Double interestInHours, Double interestInAvgLOC, Double avgInterestPerLOC, Double sumInterestPerLOC, Double kappa, Set<CalculatedClass> classes, Revision revision) {
		this.path = path;
		this.qualityMetrics = qualityMetrics;
		this.interest = new TDInterest(this, interestInEuros, interestInHours, interestInAvgLOC, avgInterestPerLOC, sumInterestPerLOC);
		this.setK(new Kappa(revision, kappa, this));
		this.setClasses(classes);
	}
	
	public void aggregateMetrics() {
		for (CalculatedClass aClass : getClasses()) {
			getQualityMetrics().add(aClass.getQualityMetrics());
		}
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public QualityMetrics getQualityMetrics() {
		return qualityMetrics;
	}
	
	public void setQualityMetrics(QualityMetrics qualityMetrics) {
		this.qualityMetrics = qualityMetrics;
	}
	
	public Set<CalculatedClass> getClasses() {
		return classes;
	}
	
	public void setClasses(Set<CalculatedClass> classes) {
		this.classes = classes;
	}
	
	public String getClassNames() {
		StringBuilder classesAsStringBuilder = new StringBuilder();
		for (CalculatedClass aClass : this.getClasses()) {
			classesAsStringBuilder.append(aClass.getQualifiedName()).append(",");
		}
		String classesAsString = classesAsStringBuilder.toString();
		return classesAsString.isEmpty() ? "" : classesAsString.substring(0, classesAsString.length() - 1);
	}
	
	public void addClassName(String className) {
		classesNames.add(className);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CalculatedJavaFile javaFile = (CalculatedJavaFile) o;
		return Objects.equals(path, javaFile.path) && Objects.equals(qualityMetrics, javaFile.qualityMetrics);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(path, qualityMetrics);
	}
	
	@Override
	public String toString() {
		return this.getPath();
	}
	
	public void calculateInterest() {
		this.getK().update(this.getQualityMetrics().getOldSIZE1());
		this.getInterest().calculate();
	}
	
	public TDInterest getInterest() {
		return this.interest;
	}
	
	public Kappa getK() {
		return this.k;
	}
	
	public void setK(Kappa k) {
		this.k = k;
	}
}
