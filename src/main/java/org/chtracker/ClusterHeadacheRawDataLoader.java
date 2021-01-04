package org.chtracker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import org.chtracker.dao.loader.CvsRawDataLoader;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ClusterHeadacheRawDataLoader {

	public static void main(String[] args) throws BeansException, FileNotFoundException, IOException, ParseException {
		ConfigurableApplicationContext context = SpringApplication.run(ClusterHeadacheRawDataLoader.class, args);
		context.getBean(CvsRawDataLoader.class).load();
		context.close();
	}

}
