package tasostilsi.uom.edu.gr.metricsCalculator;

import ch.qos.logback.classic.Logger;
import infrastructure.Project;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Objects;

@SpringBootApplication
@RestController
public class MetricsCalculatorApplication {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(MetricsCalculatorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MetricsCalculatorApplication.class, args);
    }

    @GetMapping
    public String hello() {
        LOGGER.info("you called hello (/) get method");
        return "Hello and welcome to Serviced Metrics Calculator";
    }

    /**
     * Clones a repo to a specified path.
     *
     * @param project the project we are referring to
     * @return a git object
     */
    private static Git cloneRepository(Project project, String accessToken) {
        try {
            if (Objects.isNull(accessToken))
                return Git.cloneRepository()
                        .setURI(project.getUrl())
                        .setDirectory(new File(project.getClonePath()))
                        .call();
            else {
                return Git.cloneRepository()
                        .setURI(project.getUrl())
                        .setDirectory(new File(project.getClonePath()))
                        .setCredentialsProvider(new UsernamePasswordCredentialsProvider(accessToken, ""))
                        .call();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Deletes source code (if exists) before the analysis
     * procedure.
     *
     * @param file the directory that the repository will be cloned
     */
    public static void deleteSourceCode(File file) throws NullPointerException {
        if (file.isDirectory()) {
            /* If directory is empty, then delete it */
            if (Objects.requireNonNull(file.list()).length == 0)
                file.delete();
            else {
                /* List all the directory contents */
                String[] files = file.list();

                for (String temp : files) {
                    /* Construct the file structure */
                    File fileDelete = new File(file, temp);
                    /* Recursive delete */
                    deleteSourceCode(fileDelete);
                }

                /* Check the directory again, if empty then delete it */
                if (Objects.requireNonNull(file.list()).length == 0)
                    file.delete();
            }
        } else {
            /* If file, then delete it */
            file.delete();
        }
    }
}