package tasostilsi.uom.edu.gr.metricsCalculator.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedJavaFile;

import java.util.List;

@Repository
public interface JavaFilesRepository extends JpaRepository<CalculatedJavaFile, Long> {
	
	List<CalculatedJavaFile> findDistinctRevisionShaByProjectId(Long project_id);
	
	List<CalculatedJavaFile> findDistinctRevisionCountByProjectId(Long project_id);

//	Optional<CalculatedJavaFile> findDistinctRevisionShaByProjectIdOrderByRevisionCountDesc(Long project_id);

//	Optional<CalculatedJavaFile> findDistinctRevisionCountByProjectIdOrderByRevisionCountDesc(Long project_id);
	
}
