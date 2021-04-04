package org.chtracker.service;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = ServiceConfiguration.class)
@EnableAutoConfiguration
public class ServiceConfiguration {
}
