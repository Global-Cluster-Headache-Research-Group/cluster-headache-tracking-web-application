package org.chtracker.dao;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = DataConfiguration.class)
@ComponentScan(basePackageClasses = DataConfiguration.class)
@EnableAutoConfiguration
public class DataConfiguration {
	public static final String REPORT_SCHEMA_NAME = "report";
	public static final String PROFILE_SCHEMA_NAME = "profile";
	public static final String METADA_SCHEMA_NAME = "metadata";
}
