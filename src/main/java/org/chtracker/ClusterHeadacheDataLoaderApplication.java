package org.chtracker;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.chtracker.dao.DataConfiguration;
import org.chtracker.dao.loader.PaviaAndersonMyHeadacheLogDataLoader;
import org.chtracker.dao.loader.YilativsCvsDataLoader;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;

public class ClusterHeadacheDataLoaderApplication {

    public static void main(String[] args) throws BeansException, IOException, InvalidFormatException {
        SpringApplication application = new SpringApplication(BaseConfiguration.class, DataConfiguration.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        ConfigurableApplicationContext context = application.run(args);
        try {
            context.getBean(YilativsCvsDataLoader.class).load();
            context.getBean(PaviaAndersonMyHeadacheLogDataLoader.class).load();
        } catch (Exception e) {
            LoggerFactory.getLogger(ClusterHeadacheDataLoaderApplication.class).error(e.getMessage(), e);
        }
        context.close();
    }

}
