package org.chtracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;

@EnableAutoConfiguration
@Configuration
public class BaseConfiguration {

	@Bean
	@Scope("prototype")
	public Logger logger(InjectionPoint injectionPoint) {
		MethodParameter methodParameter = injectionPoint.getMethodParameter();
		Assert.notNull(methodParameter, "method parameter is expected to be non null");
		return LoggerFactory.getLogger(methodParameter.getContainingClass());
	}

}
