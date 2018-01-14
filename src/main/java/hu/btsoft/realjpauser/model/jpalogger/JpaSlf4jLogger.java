/*
 *  ------------------------------------------------------------------------------------
 *
 *  JPA Real User Test Monitor project
 *
 *  Module:  JruTest (JruTest)
 *  File:    JpaSlf4jLogger.java
 *  Created: 2018.01.14. 8:18:49
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.realjpauser.model.jpalogger;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This is a wrapper class for SLF4J.
 * It is used when messages need to be logged through SLF4J.
 * </p>
 * <p>
 * Use the follwing configuration for using SLF4J with EclipseLink
 * <code>eclipselink.logging.logger</code> and the value
 * <code>org.eclipse.persistence.logging.Slf4jSessionLogger</code>
 * </p>
 * <p>
 * Use the following categories from EclipseLink
 * (eclipselink.logging.timestamp, eclipselink.logging.thread, eclipselink.logging.session, eclipselink.logging.connection, eclipselink.logging.parameters).
 * <p>
 * Logging categories available are:
 * <p>
 * <ul>
 * <li>org.eclipse.persistence.logging.default
 * <li>org.eclipse.persistence.logging.sql
 * <li>org.eclipse.persistence.logging.transaction
 * <li>org.eclipse.persistence.logging.event
 * <li>org.eclipse.persistence.logging.connection
 * <li>org.eclipse.persistence.logging.query
 * <li>org.eclipse.persistence.logging.cache
 * <li>org.eclipse.persistence.logging.propagation
 * <li>org.eclipse.persistence.logging.sequencing
 * <li>org.eclipse.persistence.logging.ejb
 * <li>org.eclipse.persistence.logging.ejb_or_metadata
 * <li>org.eclipse.persistence.logging.weaver
 * <li>org.eclipse.persistence.logging.properties
 * <li>org.eclipse.persistence.logging.server
 * </ul>
 * </p>
 * <p>
 * Mapping of Java Log Level to SLF4J Log Level:
 * </p>
 * <ul>
 * <li>ALL,FINER,FINEST -> TRACE
 * <li>FINE -> DEBUG
 * <li>CONFIG,INFO -> INFO
 * <li>WARNING -> WARN
 * <li>SEVERE -> ERROR
 * </ul>
 * </p>
 * <p>
 * @see https://gist.github.com/msosvi/1325764/raw/50a882f881c2651f6de10cf661813faca22a480d/Slf4jSessionLogger.java
 * @see https://github.com/nurkiewicz/books/blob/master/src/main/java/com/blogspot/nurkiewicz/Slf4jSessionLogger.java
 * @see https://gist.github.com/msosvi/1325764/0dd85653b5be4f1571521f534e7510b709285094
 */
public class JpaSlf4jLogger extends AbstractSessionLog {

    public static final String ECLIPSELINK_NAMESPACE = "org.eclipse.persistence.logging";

    public static final String DEFAULT_CATEGORY = "default";

    public static final String DEFAULT_ECLIPSELINK_NAMESPACE = ECLIPSELINK_NAMESPACE + "." + DEFAULT_CATEGORY;

    /**
     * SLF4J log levels.
     */
    enum LogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR, OFF
    }

    private static final Map<Integer, LogLevel> MAP_LEVELS = new HashMap<Integer, LogLevel>();

    static {
        MAP_LEVELS.put(SessionLog.ALL, LogLevel.TRACE);
        MAP_LEVELS.put(SessionLog.FINEST, LogLevel.TRACE);
        MAP_LEVELS.put(SessionLog.FINER, LogLevel.TRACE);
        MAP_LEVELS.put(SessionLog.FINE, LogLevel.DEBUG);
        MAP_LEVELS.put(SessionLog.CONFIG, LogLevel.INFO);
        MAP_LEVELS.put(SessionLog.INFO, LogLevel.INFO);
        MAP_LEVELS.put(SessionLog.WARNING, LogLevel.WARN);
        MAP_LEVELS.put(SessionLog.SEVERE, LogLevel.ERROR);
    }

    private final Map<String, Logger> categoryLoggers = new HashMap<>();

    /**
     * Constructor
     */
    public JpaSlf4jLogger() {
        super();
        createCategoryLoggers();
    }

    /**
     * PUBLIC:
     * <p>
     * Log a SessionLogEntry
     *
     * @param sessionLogEntry SessionLogEntry that holds all the information for an EclipseLink logging event
     */
    @Override
    public void log(SessionLogEntry sessionLogEntry) {
        if (!shouldLog(sessionLogEntry.getLevel(), sessionLogEntry.getNameSpace())) {
            return;
        }

        Logger logger = getLogger(sessionLogEntry.getNameSpace());
        LogLevel logLevel = getLogLevel(sessionLogEntry.getLevel());

        StringBuilder message = new StringBuilder();

        message.append(getSupplementDetailString(sessionLogEntry));
        message.append(formatMessage(sessionLogEntry));

        switch (logLevel) {
            case TRACE:
                logger.trace("{}", message);
                break;
            case DEBUG:
                logger.debug("{}", message);
                break;
            case INFO:
                logger.info("{}", message);
                break;
            case WARN:
                logger.warn("{}", message);
                break;
            case ERROR:
                logger.error("{}", message);
                break;
        }
    }

    /**
     * PUBLIC:
     * <p>
     * Check if a message of the given level would actually be logged for the category name space.
     * !isOff() is checked to screen out the possibility when both
     * log level and log request level are set to OFF.
     *
     * @return true if the given message level will be logged
     *
     * @param level    the log request level
     * @param category the string representation of an EclipseLink category, e.g. "sql", "transaction" ...*
     */
    @Override
    public boolean shouldLog(int level, String category) {
        Logger logger = getLogger(category);
        LogLevel logLevel = getLogLevel(level);

        switch (logLevel) {
            case TRACE:
                return logger.isTraceEnabled();
            case DEBUG:
                return logger.isDebugEnabled();
            case INFO:
                return logger.isInfoEnabled();
            case WARN:
                return logger.isWarnEnabled();
            case ERROR:
                return logger.isErrorEnabled();
            default:
                return true;
        }
    }

    /**
     * PUBLIC:
     * <p>
     * Check if a message of the given level would actually be logged.
     * It is used when session is not available.
     *
     * @return true if the given message level will be logged
     *
     * @param level the log request level
     */
    @Override
    public boolean shouldLog(int level) {
        return shouldLog(level, DEFAULT_CATEGORY);
    }

    /**
     * PUBLIC:
     * Return true if SQL logging should log visible bind parameters. If the
     * shouldDisplayData is not set, check the session log level and return
     * true for a level greater than CONFIG.
     *
     * @return
     */
    @Override
    public boolean shouldDisplayData() {
        if (this.shouldDisplayData != null) {
            return shouldDisplayData;
        } else {
            return false;
        }
    }

    /**
     * Initialize loggers eagerly
     */
    private void createCategoryLoggers() {
        for (String category : SessionLog.loggerCatagories) {
            addLogger(category, ECLIPSELINK_NAMESPACE + "." + category);
        }
        // Logger default para cuando no hay categoría.
        addLogger(DEFAULT_CATEGORY, DEFAULT_ECLIPSELINK_NAMESPACE);
    }

    /**
     * INTERNAL: Add Logger to the categoryLoggers.
     */
    private void addLogger(String loggerCategory, String loggerNameSpace) {
        categoryLoggers.put(loggerCategory, LoggerFactory.getLogger(loggerNameSpace));
    }

    /**
     * INTERNAL: Return the Logger for the given category
     */
    private Logger getLogger(String category) {
        if (!hasText(category) || !this.categoryLoggers.containsKey(category)) {
            category = DEFAULT_CATEGORY;
        }

        return categoryLoggers.get(category);
    }

    /**
     * Return the corresponding Slf4j Level for a given EclipseLink level.
     */
    private LogLevel getLogLevel(Integer level) {
        LogLevel logLevel = MAP_LEVELS.get(level);

        if (logLevel == null) {
            logLevel = LogLevel.OFF;
        }

        return logLevel;
    }

    private static boolean hasText(String str) {
        return str != null;
    }

}
