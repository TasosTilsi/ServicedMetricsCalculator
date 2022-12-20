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
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ServicedMetricsCalculatorApplication.class);
	@Autowired
	Environment environment;
	
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
		
		return "<!DOCTYPE html>\n" +
				"<html>\n" +
				"  <head>\n" +
				"    <meta http-equiv=\"refresh\" content=\"1; url='" + url + "'\" />\n" +
				"  </head>\n" +
				"  <body>\n" +
				"    <p>Please follow <a href=" + url + ">this link</a> if the auto redirector won't work properly.</p>\n" +
				"  </body>\n" +
				"</html>";
	}
	
}
