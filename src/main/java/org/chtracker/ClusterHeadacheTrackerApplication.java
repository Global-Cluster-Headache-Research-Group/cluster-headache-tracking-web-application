package org.chtracker;

import org.chtracker.dao.DataConfiguration;
import org.chtracker.service.ServiceConfiguration;
import org.chtracker.web.WebMvcConfiguration;
import org.springframework.boot.SpringApplication;

public class ClusterHeadacheTrackerApplication {

	public static void main(String[] args) {
		Class<?>[] configurations = { BaseConfiguration.class, DataConfiguration.class, WebMvcConfiguration.class, ServiceConfiguration.class};
		SpringApplication.run(configurations, args);
	}

}
