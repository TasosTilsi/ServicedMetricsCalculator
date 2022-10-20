package tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure;

import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities.CalculatedJavaFile;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class Globals {
	
	private static final Set<CalculatedJavaFile> javaFiles;
	
	static {
		javaFiles = ConcurrentHashMap.newKeySet();
	}
	
	public static void addJavaFile(CalculatedJavaFile jf) {
		if (!getJavaFiles().add(jf)) {
			getJavaFiles().remove(jf);
			getJavaFiles().add(jf);
		}
	}
	
	public static Set<CalculatedJavaFile> getJavaFiles() {
		return javaFiles;
	}
}
