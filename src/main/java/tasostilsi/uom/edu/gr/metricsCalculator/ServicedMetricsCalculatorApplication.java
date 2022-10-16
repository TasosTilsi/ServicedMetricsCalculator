package tasostilsi.uom.edu.gr.metricsCalculator;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;

@SpringBootApplication
@RestController
public class ServicedMetricsCalculatorApplication {

    @Autowired
    Environment environment;

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ServicedMetricsCalculatorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ServicedMetricsCalculatorApplication.class, args);
    }

    @GetMapping
    public String welcomePage() {
        LOGGER.info("you called hello (/) get method");

        String host = InetAddress.getLoopbackAddress().getHostAddress();
        String port = environment.getProperty("local.server.port");
        String url = "http://" + host + ":" + port + "/api/docs/swagger-ui/index.html";

        LOGGER.info(url);

        String hello = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "  <head>\n" +
                "    <meta http-equiv=\"refresh\" content=\"1; url='" + url + "'\" />\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <p>Please follow <a href=" + url + ">this link</a> if the auto redirector won't work properly.</p>\n" +
                "  </body>\n" +
                "</html>";
        return hello;
    }

}
