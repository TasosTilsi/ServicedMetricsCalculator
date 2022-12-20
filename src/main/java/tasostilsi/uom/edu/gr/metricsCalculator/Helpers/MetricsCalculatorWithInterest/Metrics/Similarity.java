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

import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedJavaFile;

public class Similarity implements Comparable<Similarity> {
	
	private final CalculatedJavaFile jf1;
	private final CalculatedJavaFile jf2;
	private final Double similarityMetric;
	
	public Similarity(CalculatedJavaFile jf1, CalculatedJavaFile jf2, Double similarityMetric) {
		this.jf1 = jf1;
		this.jf2 = jf2;
		this.similarityMetric = similarityMetric;
	}
	
	public CalculatedJavaFile getJf1() {
		return jf1;
	}
	
	public CalculatedJavaFile getJf2() {
		return jf2;
	}
	
	public Double getSimilarityMetric() {
		return similarityMetric;
	}
	
	@Override
	public int compareTo(Similarity s) {
		return this.getSimilarityMetric().compareTo(s.getSimilarityMetric());
	}
}
