package edu.flash3388.flashlib.util;

/**
 * An interface for log data piping. Can be added to a log using the {@link Log#addListener(LogListener)}
 * method. If the logging mode of the log contains {@link Log#MODE_INTERFACES} than data will be passed to here as well.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface LogListener {
	/**
	 * A pipe for standard logging data from {@link Log#log(String, String)} method.
	 * @param log a data log
	 * @param caller the logger
	 */
	void log(String log, String caller);
	/**
	 * A pipe for standard logging data from {@link Log#logTime(String, String, double)} method.
	 * @param log a data log
	 * @param caller the logger
	 * @param time the timestamp
	 */
	void logTime(String log, String caller, double time);
	/**
	 * A pipe for errors reported from {@link Log#reportError(String)}.
	 * @param err error data
	 * @param time the timestamp
	 */
	void reportError(String err, double time);
	/**
	 * A pipe for warning reported from {@link Log#reportWarning(String)}.
	 * @param war warning data
	 * @param time the timestamp
	 */
	void reportWarning(String war, double time);
}
