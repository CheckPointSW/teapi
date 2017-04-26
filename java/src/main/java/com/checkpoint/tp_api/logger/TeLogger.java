package com.checkpoint.tp_api.logger;

import org.apache.log4j.*;

import java.util.Map;

/**
 * Created by edanha on 4/13/2017.
 */
public class TeLogger {
    private static FileAppender fileAppender = null;
    private static ConsoleAppender consoleAppender = new ConsoleAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"));

    public static Logger init(Class clazz, Map<String, Object> argMap) throws Exception {
        Logger LOGGER = Logger.getLogger(clazz);
        LOGGER.setAdditivity(false);
        BasicConfigurator.configure();
        if (argMap.containsKey("debugFileName") && argMap.get("debugFileName") != null) {
            if (fileAppender == null) {
                fileAppender = new FileAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"),
                        argMap.get("debugFileName") + "\\out.log");
            }
            LOGGER.addAppender(fileAppender);
        }
        LOGGER.addAppender(consoleAppender);
        Logger.getLogger("org.apache.http").setLevel(Level.OFF);
        if ((boolean) argMap.get("d")) {
            LOGGER.setLevel(Level.DEBUG);
        } else {
            LOGGER.setLevel(Level.INFO);
        }
        return LOGGER;
    }
}
