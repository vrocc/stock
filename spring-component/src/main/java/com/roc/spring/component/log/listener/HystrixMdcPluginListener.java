package com.roc.spring.component.log.listener;

import com.roc.spring.component.log.logback.HystrixConcurrencyStrategyWithMdc;
import com.netflix.hystrix.strategy.HystrixPlugins;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class HystrixMdcPluginListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(HystrixMdcPluginListener.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        logger.info("#####################################################");
        logger.info("#      project configuration initialization");
        try {
            HystrixPlugins.getInstance().registerConcurrencyStrategy(HystrixConcurrencyStrategyWithMdc.getInstance());
            logger.info("#      Configuration HystrixConcurrencyStrategyWithMdc register success");
        } catch (Exception e) {
            logger.info("#      Configuration Failed ...");
            System.exit(-1);
        }
        logger.info("#####################################################");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("ServletContext Destroyed");
    }
}