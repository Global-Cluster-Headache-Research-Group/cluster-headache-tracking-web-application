package org.chtracker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.chtracker.dao.DataConfiguration;
import org.chtracker.dao.loader.PaviaAndersonMyHeadacheLogDataLoader;
import org.chtracker.dao.loader.YilativsCvsDataLoader;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;

public class ClusterHeadacheRawDataLoader {

	public static void main(String[] args) throws BeansException, FileNotFoundException, IOException, ParseException, InvalidFormatException {
		SpringApplication application = new SpringApplication(DataConfiguration.class,LoggingConfiguration.class);
		application.setWebApplicationType(WebApplicationType.NONE);
		ConfigurableApplicationContext context = application.run (args);
		context.getBean(YilativsCvsDataLoader.class).load();
		context.getBean(PaviaAndersonMyHeadacheLogDataLoader.class).load();
		context.close();
	}

}
