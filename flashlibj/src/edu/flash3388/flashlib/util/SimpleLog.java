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
		getPrintStream().println(getName()+"> ["+time+"] <WARNING> : "+warning);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void printError(String error, double time){
		if(!isLoggingMode(MODE_PRINT)) return;
		getPrintStream().println(getName()+"> ["+time+"] <ERROR> : "+error);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print(String log, String caller){
		if(!isLoggingMode(MODE_PRINT)) return;
		getPrintStream().println(getName()+"> ("+caller+") : "+log);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print(String log, String caller, double time){
		if(!isLoggingMode(MODE_PRINT)) return;
		getPrintStream().println(getName()+"> ["+time+"] ("+caller+") : "+log);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void write(String mess, String caller){
		if(isClosed() || !isLoggingMode(MODE_WRITE)) return;
		writeToStandardLog("("+caller+") : "+mess);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void write(String mess, String caller, double time){
		if(isClosed() || !isLoggingMode(MODE_WRITE)) return;
		writeToStandardLog("["+time+"] ("+caller+") : "+mess);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void writeError(String mess, double time){
		if(isClosed() || !isLoggingMode(MODE_WRITE)) return;
		mess = "["+time+"] : " + mess;
		writeToErrorLog(mess);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void writeError(String mess, String stacktrace, double time){
		if(isClosed() || !isLoggingMode(MODE_WRITE)) return;
		write(mess, "ERROR");
		mess = "["+time+"] <ERROR> : " + mess;
		writeToErrorLog(mess, stacktrace);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void writeWarning(String mess, double time){
		if(isClosed() || !isLoggingMode(MODE_WRITE)) return;
		write(mess, "WARNING");
		mess = "["+time+"] <WARNING> : " + mess;
		writeToErrorLog(mess);
	}
}
