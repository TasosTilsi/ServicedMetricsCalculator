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

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;

@Configuration
public class AppConfig {
	
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	
	@Bean
	public OpenEntityManagerInViewFilter openEntityManagerInViewFilter() {
		Hibernate.initialize(null);
		return new OpenEntityManagerInViewFilter();
	}
	
	@Bean
	public FilterRegistrationBean<OpenEntityManagerInViewFilter> registration() {
		FilterRegistrationBean<OpenEntityManagerInViewFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(openEntityManagerInViewFilter());
		registration.addUrlPatterns("/*");
		registration.setName("OpenEntityManagerInViewFilter");
		registration.setOrder(Ordered.LOWEST_PRECEDENCE);
		return registration;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager;
	}
}
