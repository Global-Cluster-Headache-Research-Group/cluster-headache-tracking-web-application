package org.chtracker.dao;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@Configuration
@EnableJdbcRepositories(basePackageClasses = DataConfiguration.class)
@ComponentScan(basePackageClasses = DataConfiguration.class)
@EnableAutoConfiguration
public class DataConfiguration {

}
