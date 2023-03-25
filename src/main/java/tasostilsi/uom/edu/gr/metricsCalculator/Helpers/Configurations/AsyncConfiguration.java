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

package tasostilsi.uom.edu.gr.metricsCalculator.Helpers.Configurations;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfiguration {
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(AsyncConfiguration.class);
	
	@Bean(name = "taskExecutor")
	public Executor taskExecutor() {
		LOGGER.debug("Creating Async Task Executor");
		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize((int) Math.ceil(Runtime.getRuntime().availableProcessors() / 2.0));
		executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
		executor.setQueueCapacity(15);
		executor.setThreadNamePrefix("Thread-");
		executor.initialize();
		TransactionSynchronizationManager.setActualTransactionActive(true);
		return executor;
	}
	
}
