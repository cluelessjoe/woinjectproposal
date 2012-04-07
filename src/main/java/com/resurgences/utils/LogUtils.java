package com.resurgences.utils;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class LogUtils {

    private static final Level DEFAULT_LOG_LEVEL = Level.ALL;

    public static void initLog() {
        initLog(DEFAULT_LOG_LEVEL, new ConsoleAppender(new PatternLayout("%-5p [%t] (%F:%L) - %m\n"),
                ConsoleAppender.SYSTEM_OUT));
    }

    private static void initLog(Level logLevel, Appender appender) {
        Logger.getRootLogger().removeAllAppenders();
        Logger.getRootLogger().addAppender(appender);
        Logger.getRootLogger().setLevel(logLevel);
        Logger.getRootLogger().setAdditivity(false);

    }
}
