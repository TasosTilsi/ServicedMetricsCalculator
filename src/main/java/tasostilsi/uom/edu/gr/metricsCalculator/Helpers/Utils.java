package tasostilsi.uom.edu.gr.metricsCalculator.Helpers;

import data.Globals;
import infrastructure.Project;
import infrastructure.Revision;
import infrastructure.interest.JavaFile;
import infrastructure.newcode.DiffEntry;
import infrastructure.newcode.PrincipalResponseEntity;
import metricsCalculator.calculator.MetricsCalculator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Utils {

    private static Utils instance;

    private Utils() {
    }

    public static Utils getInstance() {
        if (instance == null) {
            instance = new Utils();
        }
        return instance;
    }

    /**
     * Removes those files that are marked as 'DELETED' (new code's call)
     *
     * @param diffEntries the modified java files (new, modified, deleted)
     */
    public static Set<JavaFile> removeDeletedFiles(Revision currentRevision, Set<DiffEntry> diffEntries) {
        Set<JavaFile> deletedFiles = ConcurrentHashMap.newKeySet();
        diffEntries
                .forEach(diffEntry -> {
                    deletedFiles.add(new JavaFile(diffEntry.getOldFilePath(), currentRevision));
                    Globals.getJavaFiles().removeIf(javaFile -> javaFile.getPath().endsWith(diffEntry.getOldFilePath()));
                });
        return deletedFiles;
    }

    /**
     * Finds java file by its path
     *
     * @param filePath the file path
     * @return the java file (JavaFile) whose path matches the given one
     */
    public static JavaFile getAlreadyDefinedFile(String filePath) {
        for (JavaFile jf : Globals.getJavaFiles())
            if (jf.getPath().equals(filePath))
                return jf;
        return null;
    }

    /**
     * Sets the metrics of new and modified files.
     *
     * @param project         the project we are referring to
     * @param currentRevision the revision we are analysing
     * @param entity          the entity with the list containing the diff entries received.
     */
    public static void setMetrics(Project project, Revision currentRevision, PrincipalResponseEntity entity) {
        if (!entity.getDeleteDiffEntries().isEmpty())
            removeDeletedFiles(currentRevision, entity.getDeleteDiffEntries());
        if (!entity.getAddDiffEntries().isEmpty())
            setMetrics(project, currentRevision, entity.getAddDiffEntries().stream().map(DiffEntry::getNewFilePath).collect(Collectors.toSet()));
        if (!entity.getModifyDiffEntries().isEmpty())
            setMetrics(project, currentRevision, entity.getModifyDiffEntries().stream().map(DiffEntry::getNewFilePath).collect(Collectors.toSet()));
        if (!entity.getRenameDiffEntries().isEmpty())
            entity.getRenameDiffEntries()
                    .forEach(diffEntry -> {
                        for (JavaFile javaFile : Globals.getJavaFiles()) {
                            if (javaFile.getPath().equals(diffEntry.getOldFilePath()))
                                javaFile.setPath(diffEntry.getNewFilePath());
                        }
                    });
    }

    /**
     * Get Metrics from Metrics Calculator for every java file (initial calculation)
     *
     * @param project the project we are referring to
     */
    public static void setMetrics(Project project, Revision currentRevision) {
        MetricsCalculator mc = new MetricsCalculator(new metricsCalculator.infrastructure.entities.Project(project.getClonePath()));
        int resultCode = mc.start();
        if (resultCode == -1)
            return;
        String st = mc.printResults();
        String[] s = st.split("\\r?\\n");
        try {
            for (int i = 1; i < s.length; ++i) {
                String[] column = s[i].split("\t");
                String filePath = column[0];
                List<String> classNames;
                try {
                    classNames = Arrays.asList(column[14].split(","));
                } catch (Throwable e) {
                    classNames = new ArrayList<>();
                }

                JavaFile jf;
                if (Globals.getJavaFiles().stream().noneMatch(javaFile -> javaFile.getPath().equals(filePath.replace("\\", "/")))) {
                    jf = new JavaFile(filePath, currentRevision);
                    registerMetrics(column, jf, classNames);
                    Globals.addJavaFile(jf);
                } else {
                    jf = getAlreadyDefinedFile(filePath);
                    if (Objects.nonNull(jf)) {
                        registerMetrics(column, jf, classNames);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Get Metrics from Metrics Calculator for specific java files (new or modified)
     *
     * @param project         the project we are referring to
     * @param currentRevision the revision we are analysing
     * @param jfs             the list of java files
     */
    public static void setMetrics(Project project, Revision currentRevision, Set<String> jfs) {
        if (jfs.isEmpty())
            return;
        MetricsCalculator mc = new MetricsCalculator(new metricsCalculator.infrastructure.entities.Project(project.getClonePath()));
        int resultCode = mc.start(jfs);
        if (resultCode == -1)
            return;
        String st = mc.printResults(jfs);
        String[] s = st.split("\\r?\\n");
        try {
            Set<JavaFile> toCalculate = new HashSet<>();
            for (int i = 1; i < s.length; ++i) {
                String[] column = s[i].split("\t");
                String filePath = column[0];
                List<String> classNames;
                try {
                    classNames = Arrays.asList(column[14].split(","));
                } catch (Throwable e) {
                    classNames = new ArrayList<>();
                }

                JavaFile jf;
                if (Globals.getJavaFiles().stream().noneMatch(javaFile -> javaFile.getPath().equals(filePath.replace("\\", "/")))) {
                    jf = new JavaFile(filePath, currentRevision);
                    registerMetrics(column, jf, classNames);
                    Globals.addJavaFile(jf);
                    toCalculate.add(jf);
                } else {
                    jf = getAlreadyDefinedFile(filePath);
                    if (Objects.nonNull(jf)) {
                        toCalculate.add(jf);
                        registerMetrics(column, jf, classNames);
                    }
                }
            }
            toCalculate.forEach(JavaFile::calculateInterest);
        } catch (Exception ignored) {
        }
    }

    /**
     * Register Metrics to specified java file
     *
     * @param calcEntries entries taken from MetricsCalculator's results
     * @param jf          the java file we are registering metrics to
     */
    public static void registerMetrics(String[] calcEntries, JavaFile jf, List<String> classNames) {
        jf.getQualityMetrics().setClassesNum(Integer.parseInt(calcEntries[1]));
        jf.getQualityMetrics().setWMC(Double.parseDouble(calcEntries[2]));
        jf.getQualityMetrics().setDIT(Integer.parseInt(calcEntries[3]));
        jf.getQualityMetrics().setComplexity(Double.parseDouble(calcEntries[4]));
        jf.getQualityMetrics().setLCOM(Double.parseDouble(calcEntries[5]));
        jf.getQualityMetrics().setMPC(Double.parseDouble(calcEntries[6]));
        jf.getQualityMetrics().setNOM(Double.parseDouble(calcEntries[7]));
        jf.getQualityMetrics().setRFC(Double.parseDouble(calcEntries[8]));
        jf.getQualityMetrics().setDAC(Integer.parseInt(calcEntries[9]));
        jf.getQualityMetrics().setNOCC(Integer.parseInt(calcEntries[10]));
        jf.getQualityMetrics().setCBO(Double.parseDouble(calcEntries[11]));
        jf.getQualityMetrics().setSIZE1(Integer.parseInt(calcEntries[12]));
        jf.getQualityMetrics().setSIZE2(Integer.parseInt(calcEntries[13]));
        for (String className : classNames)
            jf.addClassName(className);
    }
}
