package tasostilsi.uom.edu.gr.metricsCalculator.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedJavaFile;

import java.util.List;
import java.util.Optional;

@Repository
public interface JavaFilesRepository extends JpaRepository<CalculatedJavaFile, Long> {
	
	List<String> findDistinctRevisionShaByProjectId(Long project_id);
	
//	Optional<String> findLastRevisionShaByProjectIdOrderByRevisionCountDesc(Long project_id);
	
//	Optional<Integer> findLastRevisionCountByProjectIdOrderByRevisionCountDesc(Long project_id);
	
}
