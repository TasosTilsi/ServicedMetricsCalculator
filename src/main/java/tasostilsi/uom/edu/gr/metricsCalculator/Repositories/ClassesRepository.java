package tasostilsi.uom.edu.gr.metricsCalculator.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedClass;

@Repository
public interface ClassesRepository extends JpaRepository<CalculatedClass, Long> {
	
	@Transactional
	@Modifying
	@Query(value = "Insert into classes c set c = ?1",
			nativeQuery = true)
	void insertClassesToDB(CalculatedClass calculatedClass);
}
