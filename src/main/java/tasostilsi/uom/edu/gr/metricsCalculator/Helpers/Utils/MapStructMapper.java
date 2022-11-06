//package tasostilsi.uom.edu.gr.metricsCalculator.Helpers.Utils;
//
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedClass;
//import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedJavaFile;
//import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.Project;
//import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.Revision;
//import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Metrics.Kappa;
//import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Metrics.QualityMetrics;
//import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Metrics.TDInterest;
//import tasostilsi.uom.edu.gr.metricsCalculator.Models.Entities.*;
//
//import java.util.List;
//
//@Mapper(componentModel = "spring")
//public interface MapStructMapper {
//
//	ProjectEntity projectToProjectEntity(Project project);
//
//	JavaFilesEntity CalculatedJavaFilesToJavaFilesEntity(CalculatedJavaFile javaFile);
//
//	ClassesEntity CalculatedClassesToClassesEntity(CalculatedClass calculatedClass);
//
//	List<JavaFilesEntity> CalculatedJavaFilesListToJavaFilesEntityList(List<CalculatedJavaFile> javaFileList);
//
//	List<ClassesEntity> CalculatedClassesListToJavaFilesEntityList(List<CalculatedClass> calculatedClassList);
//
//	KappaEntity kappaToKappaEntity(Kappa k);
//
//	TDInterestEntity tdInterestToTdInterestEntity(TDInterest interest);
//
//	RevisionEntity revisionToRevisionEntity(Revision revision);
//
//	QualityMetricsEntity qualityMetricsToQualityMetricsEntity(QualityMetrics qualityMetrics);
//
//	List<QualityMetricsEntity> qualityMetricsListToQualityMetricsEntityList(List<QualityMetrics> qualityMetricsList);
//
//}
