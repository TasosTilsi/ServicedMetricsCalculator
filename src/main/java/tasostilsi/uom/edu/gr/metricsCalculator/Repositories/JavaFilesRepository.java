package tasostilsi.uom.edu.gr.metricsCalculator.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedJavaFile;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.Revision;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface JavaFilesRepository extends JpaRepository<CalculatedJavaFile, Long> {
	
	List<CalculatedJavaFile> findDistinctRevisionShaByProjectId(Long project_id);
	
	List<CalculatedJavaFile> findDistinctRevisionCountByProjectId(Long project_id);
	
	@Query("select distinct c.k.revision.sha from CalculatedJavaFile c where c.project.id = :project_id ")
	List<String> getDistinctRevisionShaByProjectId(Long project_id);
	
	@Query("select distinct c.k.revision from CalculatedJavaFile c " +
			"where c.project.id = :project_id " +
			"order by c.k.revision.count DESC")
	Optional<Revision> getRevisionByProjectIdOrderByRevisionCountDesc(Long project_id);
	
	@Query("select jf.path from CalculatedJavaFile jf where jf.path = ?1")
	Optional<String> getJavaFilePathById(String path);
	
	@Query("select c from CalculatedJavaFile c where c.project.id = ?1")
	Optional<Set<CalculatedJavaFile>> getAllByProjectId(Long project_id);
	
	@Transactional
	@Modifying
	@Query(value = "Insert into java_files c set c = ?1",
			nativeQuery = true)
	void insertJavaFileToDB(CalculatedJavaFile jf);
	
	
}
