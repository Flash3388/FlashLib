package edu.flash3388.flashlib.util;

/**
 * An interface for log data piping. Can be added to a log using the {@link Log#addLoggingInterface(LoggingInterface)}
 * method. If the logging mode of the log contains {@link Log#MODE_INTERFACES} than data will be passed to here as well.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface LoggingInterface {
	/**
	 * A pipe for standard logging data from {@link Log#log(String, String)} method.
	 * @param log a data log
	 */
	void log(String log);
	/**
	 * A pipe for errors reported from {@link Log#reportError(String)}.
	 * @param err error data
	 */
	void reportError(String err);
	/**
	 * A pipe for warning reported from {@link Log#reportWarning(String)}.
	 * @param war warning data
	 */
	void reportWarning(String war);
}
