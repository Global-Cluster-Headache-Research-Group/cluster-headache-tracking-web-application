package org.chtracker.application;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = ApplicationConfiguration.class)
@EnableAutoConfiguration
public class ApplicationConfiguration {
}
