package org.chtracker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.chtracker.dao.loader.PaviaAndersonMyHeadacheLogDataLoader;
import org.chtracker.dao.loader.YilativsCvsDataLoader;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ClusterHeadacheRawDataLoader {

	public static void main(String[] args) throws BeansException, FileNotFoundException, IOException, ParseException, InvalidFormatException {
		ConfigurableApplicationContext context = SpringApplication.run(ClusterHeadacheRawDataLoader.class, args);
		context.getBean(YilativsCvsDataLoader.class).load();
		context.getBean(PaviaAndersonMyHeadacheLogDataLoader.class).load();
		context.close();
	}

}
