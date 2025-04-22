package ca.bc.gov.brmb.common.utils;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;

/**
 * There is no way to programmatically change SLF4J root log levels at runtime,
 * therefore we have to get the underlying logging framework and change that.
 * This is a limitation of SLF4J.
 */
public class LoggingConfigurer {
	
	private static final Logger logger = LogManager.getLogger(LoggingConfigurer.class);
	
	public static final String LOG4J_PREFERENCE_SET = "org.apache.logging.log4j";
	public static final String LAYOUT_CONVERSION_PATTERN_KEY = LOG4J_PREFERENCE_SET+".PatternLayout.ConversionPattern";
	public static final String ROOT_LEVEL_KEY = LOG4J_PREFERENCE_SET+".root.level";
	public static final String LOGGER_KEY = LOG4J_PREFERENCE_SET+".logger.";

	private Properties properties;
	
	public LoggingConfigurer() {
		logger.debug("<LoggingConfigurer");
		logger.debug(">LoggingConfigurer");
	}

	public void init() {
		logger.info("<init");

    logger.warn("ALERT: LoggingConfigurer has been deprecated with BRMB Common v1.3.0");

		Logger rootLogger = LogManager.getRootLogger();
		logger.debug(rootLogger.getLevel() + " " + rootLogger.getName());
		
		/* String layoutConversionPattern = properties.getProperty(LAYOUT_CONVERSION_PATTERN_KEY);
		
		PatternLayout patternLayout = null;
		if (layoutConversionPattern != null && layoutConversionPattern.trim().length() > 0) {
			patternLayout = new PatternLayout(layoutConversionPattern);
		} */

		/* Enumeration<?> appenders = rootLogger.get getAllAppenders();
		while(appenders.hasMoreElements()) {
			Appender appender = (Appender)appenders.nextElement();
			logger.info(appender.getName()+" "+appender.getClass());
			if(patternLayout!=null) {
				logger.info("Setting conversion pattern to "+layoutConversionPattern);
				appender.setLayout(patternLayout);
			}
			if(appender instanceof RollingFileAppender) {
				RollingFileAppender rollingFileAppender = (RollingFileAppender) appender;
				rollingFileAppender.rollOver();
			}
		} */
		
		/* Enumeration<? extends Logger> loggers = LogManager.getContext().getLoggerRegistry(); // LogManager.getCurrentLoggers();
		while(loggers.hasMoreElements()) {
			Logger log = (Logger)loggers.nextElement();
			log.setLevel(null);
		} */
		
		/* for(Object key:properties.keySet()) {
			if(key instanceof String) {
				String propertyName = (String) key;
				if(propertyName.startsWith(LOGGER_KEY)) {
					String name = propertyName.substring(LOGGER_KEY.length());
					String value = properties.getProperty(propertyName);
					Level level = Level.toLevel(value);
					logger.info("Setting '"+name+"' level to "+level);
					Logger log = LogManager.getLogger(name);
					log.setLevel(level);
				}
				
			}
		} */
		
		/* String rootLevel = properties.getProperty(ROOT_LEVEL_KEY);
		
		if(rootLevel!=null&&rootLevel.trim().length()>0) {
			Level level = Level.toLevel(rootLevel);
			logger.info("Setting root level to "+level);
			rootLogger.setLevel(level);
		} */

		logger.info(">init");
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
}