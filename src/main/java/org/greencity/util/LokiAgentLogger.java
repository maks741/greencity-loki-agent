package org.greencity.util;

import org.greencity.constant.EnvVar;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LokiAgentLogger {

    public static Logger getLogger(Class<?> clazz) {
        Logger log = Logger.getLogger(clazz.getName());
        initLogger(log);
        return log;
    }

    private static void initLogger(Logger log) {
        Level loggingLevel = Level.parse(EnvVar.LOGGING_LEVEL.value());

        Handler handlerObj = new ConsoleHandler();
        handlerObj.setLevel(loggingLevel);
        log.addHandler(handlerObj);
        log.setLevel(loggingLevel);
        log.setUseParentHandlers(false);
    }
}
