package edu.flash3388.flashlib.util;

/**
 * An abstract logic for logs which use simple string lines when logging. Data is printed and
 * saved as simple formated strings.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public abstract class SimpleLog extends Log{

	/**
	 * Creates a new simple log.
	 * 
	 * <p>
	 * Two log files will be created: Standard log and Error log. The former saves normal data logs through the {@link #log(String)}
	 * method call. The later saves errors and warning from {@link #reportError(String)} and {@link #reportWarning(String)}
	 * </p>
	 * <p>
	 * The log files are created in the given directory and the file name is the log name. If the directory or any of its 
	 * parent directories do not exist, they will be created. If override is true, any existing log files with the same name 
	 * will be deleted, otherwise if a log file with same name is present, an integer will be added to the name specifying it
	 * is a new file of the same name. 
	 * </p>
	 * 
	 * @param directory The directory in which to save the log
	 * @param name The name of the log
	 * @param override true to override existing log files with the same name, false otherwise.
	 * @param logMode The logging output types
	 * 
	 * @see Log#Log(String, String, boolean, int)
	 */
	public SimpleLog(String directory, String name, boolean override, int logMode) {
		super(directory, name, override, logMode);
	}
	/**
	 * Creates a new simple log.
	 * 
	 * <p>
	 * Two log files will be created: Standard log and Error log. The former saves normal data logs through the {@link #log(String)}
	 * method call. The later saves errors and warning from {@link #reportError(String)} and {@link #reportWarning(String)}
	 * </p>
	 * <p>
	 * The log files are created in the given directory and the file name is the log name. If the directory or any of its 
	 * parent directories do not exist, they will be created. If override is true, any existing log files with the same name 
	 * will be deleted, otherwise if a log file with same name is present, an integer will be added to the name specifying it
	 * is a new file of the same name. 
	 * </p>
	 * 
	 * @param name The name of the log
	 * @param override true to override existing log files with the same name, false otherwise.
	 * @param logMode The logging output types
	 * 
	 * @see Log#Log(String, boolean, int)
	 */
	public SimpleLog(String name, boolean override, int logMode) {
		super(name, override, logMode);
	}
	/**
	 * Creates a new simple log.
	 * 
	 * <p>
	 * Two log files will be created: Standard log and Error log. The former saves normal data logs through the {@link #log(String)}
	 * method call. The later saves errors and warning from {@link #reportError(String)} and {@link #reportWarning(String)}
	 * </p>
	 * <p>
	 * The log files are created in the given directory and the file name is the log name. If the directory or any of its 
	 * parent directories do not exist, they will be created. If override is true, any existing log files with the same name 
	 * will be deleted, otherwise if a log file with same name is present, an integer will be added to the name specifying it
	 * is a new file of the same name. 
	 * </p>
	 * 
	 * @param name The name of the log
	 * @param override true to override existing log files with the same name, false otherwise.
	 * 
	 * @see Log#Log(String, boolean)
	 */
	public SimpleLog(String name, boolean override) {
		super(name, override);
	}
	/**
	 * Creates a new simple log.
	 * 
	 * <p>
	 * Two log files will be created: Standard log and Error log. The former saves normal data logs through the {@link #log(String)}
	 * method call. The later saves errors and warning from {@link #reportError(String)} and {@link #reportWarning(String)}
	 * </p>
	 * <p>
	 * The log files are created in the given directory and the file name is the log name. If the directory or any of its 
	 * parent directories do not exist, they will be created. If override is true, any existing log files with the same name 
	 * will be deleted, otherwise if a log file with same name is present, an integer will be added to the name specifying it
	 * is a new file of the same name. 
	 * </p>
	 * 
	 * @param name The name of the log
	 * 
	 * @see Log#Log(String)
	 */
	public SimpleLog(String name) {
		super(name);
	}
	
	/**
	 * Writes data to the standard log file. Implementation of writing is user-dependent.
	 * @param log data to write.
	 */
	protected abstract void writeToStandardLog(String log);
	/**
	 * Writes data to the error log file. Implementation of writing is user-dependent.
	 * @param log data to write.
	 */
	protected abstract void writeToErrorLog(String log);
	/**
	 * Writes data to the error log file. Implementation of writing is user-dependent.
	 * @param log data to write.
	 * @param stacktrace the stacktrace data
	 */
	protected abstract void writeToErrorLog(String log, String stacktrace);
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void printWarning(String warning, double time){
		if(!isLoggingMode(MODE_PRINT)) return;
		getPrintStream().println(String.format("%s> [%f] <WARNING> : %s", getName(), time, warning));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void printError(String error, double time){
		if(!isLoggingMode(MODE_PRINT)) return;
		getPrintStream().println(String.format("%s> [%f] <ERROR> : %s", getName(), time, error));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print(String log, String caller){
		if(!isLoggingMode(MODE_PRINT)) return;
		getPrintStream().println(String.format("%s> (%s) : %s", getName(), caller, log));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print(String log, String caller, double time){
		if(!isLoggingMode(MODE_PRINT)) return;
		getPrintStream().println(String.format("%s> [%f] (%s) : %s", getName(), time, caller, log));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void write(String log, String caller){
		if(isClosed() || !isLoggingMode(MODE_WRITE)) return;
		writeToStandardLog(String.format("(%s) : %s", caller, log));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void write(String log, String caller, double time){
		if(isClosed() || !isLoggingMode(MODE_WRITE)) return;
		writeToStandardLog(String.format("[%f] (%s) : %s", time, caller, log));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void writeError(String log, double time){
		if(isClosed() || !isLoggingMode(MODE_WRITE)) return;
		writeToErrorLog(String.format("[%f] <ERROR> : %s", time, log));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void writeError(String log, double time, StackTraceElement[] stacktrace, int traceIndex){
		if(isClosed() || !isLoggingMode(MODE_WRITE)) return;
		write(log, "ERROR");
		writeToErrorLog(String.format("[%f] <ERROR> : %s", time, log), stackTraceToString(stacktrace, traceIndex));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void writeWarning(String log, double time){
		if(isClosed() || !isLoggingMode(MODE_WRITE)) return;
		write(log, "WARNING");
		writeToErrorLog(String.format("[%f] <WARNING> : %s", time, log));
	}
}
