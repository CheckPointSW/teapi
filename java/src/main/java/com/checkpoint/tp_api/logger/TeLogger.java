package com.checkpoint.tp_api.logger;

import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Created by edanha on 4/13/2017.
 */
public class TeLogger {
    //private static FileAppender fileAppender = null;
    //
    private static ConsoleAppender consoleAppender = new ConsoleAppender();

    public static Logger init(Class clazz, Map<String, Object> argMap) throws Exception {
        consoleAppender.setLayout(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"));
        Logger LOGGER = Logger.getLogger(clazz);
        LOGGER.setAdditivity(false);
        BasicConfigurator.configure();
        LOGGER.addAppender(consoleAppender);
        if ((boolean) argMap.get("d")) {
            LOGGER.setLevel(Level.DEBUG);
        } else {
            LOGGER.setLevel(Level.INFO);
        }
        return LOGGER;
    }
}
