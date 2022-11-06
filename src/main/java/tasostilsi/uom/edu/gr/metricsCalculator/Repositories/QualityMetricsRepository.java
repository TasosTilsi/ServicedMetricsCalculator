package tasostilsi.uom.edu.gr.metricsCalculator.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Metrics.QualityMetrics;

@Repository
public interface QualityMetricsRepository extends JpaRepository<QualityMetrics, Long> {

//	@Query(value = "Select new ")
//	Optional<Revision> findRevisionFromProject(Project project);
}
