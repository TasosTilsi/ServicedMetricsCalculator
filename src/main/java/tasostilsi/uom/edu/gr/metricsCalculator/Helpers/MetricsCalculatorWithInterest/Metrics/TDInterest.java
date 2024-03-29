/*
 * ******************************************************************************
 *  * Copyright (C) 2022-2023 University of Macedonia
 *  *
 *  * This program and the accompanying materials are made
 *  * available under the terms of the Eclipse Public License 2.0
 *  * which is available at https://www.eclipse.org/legal/epl-2.0/
 *  *
 *  * SPDX-License-Identifier: EPL-2.0
 *  *****************************************************************************
 */

package tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Metrics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedJavaFile;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure.GlobalsManager;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.*;

@Data
@NoArgsConstructor
@Embeddable
@Transactional(isolation= Isolation.SERIALIZABLE)
public class TDInterest {
	
	@Transient
	private static final Double HOURLY_WAGE = 37.50;
	
	@Transient
	@JsonIgnore
	private CalculatedJavaFile javaFile;
	
	@Column(name = "interestInEuros")
	private BigDecimal interestInEuros;
	
	@Column(name = "interestInHours")
	private BigDecimal interestInHours;
	
	@Column(name = "interestInAvgLOC")
	private BigDecimal interestInAvgLOC;
	
	@Column(name = "avgInterestPerLOC")
	private BigDecimal avgInterestPerLOC;
	
	@Column(name = "sumInterestPerLOC")
	private BigDecimal sumInterestPerLOC;
	
	public TDInterest(CalculatedJavaFile javaFile) {
		this.interestInEuros = BigDecimal.valueOf(0.0);
		this.interestInHours = BigDecimal.valueOf(0.0);
		this.interestInAvgLOC = BigDecimal.valueOf(0.0);
		this.avgInterestPerLOC = BigDecimal.valueOf(0.0);
		this.sumInterestPerLOC = BigDecimal.valueOf(0.0);
		this.javaFile = javaFile;
	}
	
	public TDInterest(CalculatedJavaFile javaFile, BigDecimal interestInEuros, BigDecimal interestInHours, BigDecimal interestInAvgLOC, BigDecimal avgInterestPerLOC, BigDecimal sumInterestPerLOC) {
		this.javaFile = javaFile;
		this.interestInEuros = interestInEuros;
		this.interestInHours = interestInHours;
		this.interestInAvgLOC = interestInAvgLOC;
		this.avgInterestPerLOC = avgInterestPerLOC;
		this.sumInterestPerLOC = sumInterestPerLOC;
	}
	
	/**
	 * Calculates the interest for the file we are
	 * referring to, finding the optimal metrics
	 * and the top 5 neighbors.
	 */
	public void calculate() {
		/* Calculate similarity */
		AbstractQueue<Similarity> similarityOfFiles = calculateSimilarities();
		
		/* No need to proceed to interest calculation */
		if (similarityOfFiles.isEmpty())
			return;
		
		/* Find Top 5 Neighbors */
		Set<CalculatedJavaFile> topFiveNeighbors = findTopFiveNeighbors(similarityOfFiles);
		
		if (Objects.isNull(topFiveNeighbors))
			return;
		
		/* Get optimal metrics & normalize (add one smoothing) */
		QualityMetrics optimalMetrics = getOptimalMetrics(topFiveNeighbors);
		optimalMetrics.normalize();

			/* Calculate the interest per LOC
               Get difference optimal to actual */
		this.setSumInterestPerLOC(this.calculateInterestPerLoc(javaFile, optimalMetrics));
		
		this.setAvgInterestPerLOC(this.getSumInterestPerLOC().divide(BigDecimal.valueOf(10)));
		
		this.setInterestInAvgLOC(this.getAvgInterestPerLOC().multiply(BigDecimal.valueOf(javaFile.getK().getValue())));
		
		this.setInterestInHours(this.getInterestInAvgLOC().divide(BigDecimal.valueOf(25)));
		
		this.setInterestInEuros(this.getInterestInHours().multiply(BigDecimal.valueOf(HOURLY_WAGE)));

//            System.out.println("File: " + CalculatedJavaFile.this.path + " | Interest: " + this.getInterestInEuros());
//            System.out.println("Kappa: " + CalculatedJavaFile.this.getK().getValue());
//            System.out.println("Revisions: " + Globals.getRevisions());
	}
	
	/**
	 * Calculates the interest per loc for the file we
	 * are referring to, based on the optimal metrics
	 * found from the top 5 neighbors.
	 *
	 * @param jf             the file we are referring to
	 * @param optimalMetrics the optimal metrics object
	 * @return the interest per line of code (double)
	 */
	private BigDecimal calculateInterestPerLoc(CalculatedJavaFile jf, QualityMetrics optimalMetrics) {
		double sumInterestPerLOC = 0.0;
		sumInterestPerLOC += Math.abs(jf.getQualityMetrics().getDIT() - optimalMetrics.getDIT()) * 1.0 / optimalMetrics.getDIT();
		sumInterestPerLOC += Math.abs(jf.getQualityMetrics().getNOCC() - optimalMetrics.getNOCC()) * 1.0 / optimalMetrics.getNOCC();
		sumInterestPerLOC += Math.abs(jf.getQualityMetrics().getRFC() - optimalMetrics.getRFC()) / optimalMetrics.getRFC();
		sumInterestPerLOC += Math.abs(jf.getQualityMetrics().getLCOM() - optimalMetrics.getLCOM()) / optimalMetrics.getLCOM();
		sumInterestPerLOC += Math.abs(jf.getQualityMetrics().getWMC() - optimalMetrics.getWMC()) / optimalMetrics.getWMC();
		sumInterestPerLOC += Math.abs(jf.getQualityMetrics().getNOM() - optimalMetrics.getNOM()) / optimalMetrics.getNOM();
		sumInterestPerLOC += Math.abs(jf.getQualityMetrics().getMPC() - optimalMetrics.getMPC()) / optimalMetrics.getMPC();
		sumInterestPerLOC += Math.abs(jf.getQualityMetrics().getDAC() - optimalMetrics.getDAC()) * 1.0 / optimalMetrics.getDAC();
		sumInterestPerLOC += Math.abs(jf.getQualityMetrics().getSIZE1() - optimalMetrics.getSIZE1()) * 1.0 / optimalMetrics.getSIZE1();
		sumInterestPerLOC += Math.abs(jf.getQualityMetrics().getSIZE2() - optimalMetrics.getSIZE2()) * 1.0 / optimalMetrics.getSIZE2();
		return BigDecimal.valueOf(sumInterestPerLOC);
	}
	
	/**
	 * Calculates the similarity between a file
	 * and all the other files that are available
	 *
	 * @return the similarity of files (priority queue)
	 */
	private AbstractQueue<Similarity> calculateSimilarities() {
		AbstractQueue<Similarity> similarityOfFiles = new PriorityQueue<>(Collections.reverseOrder());
		GlobalsManager.getProjectGlobals(javaFile.getProject().getUrl()).getJavaFiles()
				.stream()
				.filter(jf -> !Objects.equals(javaFile, jf))
				.forEach(jf -> similarityOfFiles.add(new Similarity(javaFile, jf, calculateSimilarityIndex(jf))));
		return similarityOfFiles;
	}
	
	/**
	 * Finds the top five neighbors of the file
	 * we are referring to, based on the similarity
	 * index.
	 *
	 * @param similarityOfFiles all the similarity indexes
	 * @return the top five neighbors (hash set)
	 */
	private Set<CalculatedJavaFile> findTopFiveNeighbors(AbstractQueue<Similarity> similarityOfFiles) {
		Set<CalculatedJavaFile> topFiveNeighbors = new HashSet<>();
		if (similarityOfFiles.size() < 5)
			return null;
		/* Keep top 5 */
		for (int i = 0; i < 5; ++i) {
			Similarity similarity = similarityOfFiles.poll();
			topFiveNeighbors.add(Objects.requireNonNull(similarity).getJf2());
//                    System.out.printf("********* Commit %s: No%d neighbor for file %s is: %s (Similarity = %g) *********\n", CalculatedJavaFile.this.currentRevision.getSha(), i + 1, CalculatedJavaFile.this.getPath(), similarity.getJf2().getPath(), similarity.getSimilarity());
		}
		return topFiveNeighbors;
	}
	
	/**
	 * Calculates the similarity between two java files
	 * (jf1, jf2) based on specific quality metrics
	 *
	 * @param jf2 the second java file
	 * @return the similarity of these two files (double)
	 */
	private Double calculateSimilarityIndex(CalculatedJavaFile jf2) {
		
		double numOfClassesSimilarityPercentage = 0.0;
		double complexitySimilarityPercentage = 0.0;
		double methodSimilarityPercentage = 0.0;
		double linesOfCodeSimilarityPercentage = 0.0;
		
		if (javaFile.getQualityMetrics().getClassesNum() != 0 || jf2.getQualityMetrics().getClassesNum() != 0)
			numOfClassesSimilarityPercentage = 100 - (double) (Math.abs(javaFile.getQualityMetrics().getClassesNum() - jf2.getQualityMetrics().getClassesNum()) / Math.max(javaFile.getQualityMetrics().getClassesNum(), jf2.getQualityMetrics().getClassesNum()) * 100);
		if (javaFile.getQualityMetrics().getComplexity() != 0 || jf2.getQualityMetrics().getComplexity() != 0)
			complexitySimilarityPercentage = 100 - (Math.abs(javaFile.getQualityMetrics().getComplexity() - jf2.getQualityMetrics().getComplexity()) / Math.max(javaFile.getQualityMetrics().getComplexity(), jf2.getQualityMetrics().getComplexity()) * 100);
		if (javaFile.getQualityMetrics().getWMC() != 0 || jf2.getQualityMetrics().getWMC() != 0)
			methodSimilarityPercentage = 100 - (Math.abs(javaFile.getQualityMetrics().getWMC() - jf2.getQualityMetrics().getWMC()) / Math.max(javaFile.getQualityMetrics().getWMC(), jf2.getQualityMetrics().getWMC()) * 100);
		if (javaFile.getQualityMetrics().getSIZE1() != 0 || jf2.getQualityMetrics().getSIZE1() != 0)
			linesOfCodeSimilarityPercentage = 100 - (double) (Math.abs(javaFile.getQualityMetrics().getSIZE1() - jf2.getQualityMetrics().getSIZE1()) / Math.max(javaFile.getQualityMetrics().getSIZE1(), jf2.getQualityMetrics().getSIZE1()) * 100);
		
		return (numOfClassesSimilarityPercentage
				+ complexitySimilarityPercentage
				+ methodSimilarityPercentage
				+ linesOfCodeSimilarityPercentage)
				/ 4;
	}
	
	/**
	 * Returns the optimal metrics found from the set
	 * of the top five neighbors.
	 *
	 * @param topFiveNeighbors the set of neighbors
	 * @return a QualityMetrics object containing the 'holy grail' metrics
	 */
	private QualityMetrics getOptimalMetrics(Set<CalculatedJavaFile> topFiveNeighbors) {
		QualityMetrics optimalMetrics = new QualityMetrics();
		optimalMetrics.setDIT(topFiveNeighbors
				.stream().parallel()
				.map(n -> n.getQualityMetrics().getDIT())
				.min(Integer::compare)
				.orElse(0));
		optimalMetrics.setNOCC(topFiveNeighbors
				.stream().parallel()
				.map(n -> n.getQualityMetrics().getNOCC())
				.min(Integer::compare)
				.orElse(0));
		optimalMetrics.setRFC(topFiveNeighbors
				.stream().parallel()
				.map(n -> n.getQualityMetrics().getRFC())
				.min(Double::compare)
				.orElse(0.0));
		optimalMetrics.setLCOM(topFiveNeighbors
				.stream().parallel()
				.map(n -> n.getQualityMetrics().getLCOM())
				.min(Double::compare)
				.orElse(0.0));
		optimalMetrics.setWMC(topFiveNeighbors
				.stream().parallel()
				.map(n -> n.getQualityMetrics().getWMC())
				.min(Double::compare)
				.orElse(0.0));
		optimalMetrics.setNOM(topFiveNeighbors
				.stream().parallel()
				.map(n -> n.getQualityMetrics().getNOM())
				.min(Double::compare)
				.orElse(0.0));
		optimalMetrics.setMPC(topFiveNeighbors
				.stream().parallel()
				.map(n -> n.getQualityMetrics().getMPC())
				.min(Double::compare)
				.orElse(0.0));
		optimalMetrics.setDAC(topFiveNeighbors
				.stream().parallel()
				.map(n -> n.getQualityMetrics().getDAC())
				.min(Integer::compare)
				.orElse(0));
		optimalMetrics.setSIZE1(topFiveNeighbors
				.stream().parallel()
				.map(n -> n.getQualityMetrics().getSIZE1())
				.min(Integer::compare)
				.orElse(0));
		optimalMetrics.setSIZE2(topFiveNeighbors
				.stream().parallel()
				.map(n -> n.getQualityMetrics().getSIZE2())
				.min(Integer::compare)
				.orElse(0));
		return optimalMetrics;
	}
	
	public BigDecimal getInterestInHours() {
		return this.interestInHours;
	}
	
	public void setInterestInHours(BigDecimal interestInHours) {
		this.interestInHours = interestInHours;
	}
	
	public BigDecimal getInterestInAvgLOC() {
		return this.interestInAvgLOC;
	}
	
	public void setInterestInAvgLOC(BigDecimal interestInAvgLOC) {
		this.interestInAvgLOC = interestInAvgLOC;
	}
	
	public BigDecimal getAvgInterestPerLOC() {
		return this.avgInterestPerLOC;
	}
	
	public void setAvgInterestPerLOC(BigDecimal avgInterestPerLOC) {
		this.avgInterestPerLOC = avgInterestPerLOC;
	}
	
	public BigDecimal getSumInterestPerLOC() {
		return this.sumInterestPerLOC;
	}
	
	public void setSumInterestPerLOC(BigDecimal sumInterestPerLOC) {
		this.sumInterestPerLOC = sumInterestPerLOC;
	}
	
	public BigDecimal getInterestInEuros() {
		return this.interestInEuros;
	}
	
	public void setInterestInEuros(BigDecimal interestInEuros) {
		this.interestInEuros = interestInEuros;
	}
}
