package edu.flash3388.flashlib.util;

/**
 * 
 * Abstract logic for logs organizing data into class objects. Such logs offer extreme organization
 * and should be used with XML when saving.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public abstract class TypeLog extends Log{

	protected static class LogData{
		public String log;
		public String caller;
		
		public LogData(String log, String caller){
			this.log = log;
			this.caller = caller;
		}
	}
	protected static class TimedLogData extends LogData{
		public double time;
		
		public TimedLogData(String log, String caller, double time){
			super(log, caller);
			this.time = time;
		}
	}
	protected static class ErrorLogData extends TimedLogData{
		
		public ErrorLogData(String log, double time, boolean error){
			super(log, error? "ERROR" : "WARNING", time);
		}
	}
	protected static class TracedErrorLogData extends ErrorLogData{
		public String stacktrace;
		
		public TracedErrorLogData(String log, double time, String stacktrace){
			super(log, time, true);
			this.stacktrace = stacktrace;
		}
	}
	
	/**
	 * Creates a new type log.
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
	public TypeLog(String directory, String name, boolean override, int logMode) {
		super(directory, name, override, logMode);
	}
	/**
	 * Creates a new type log.
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
	public TypeLog(String name, boolean override, int logMode) {
		super(name, override, logMode);
	}
	/**
	 * Creates a new type log.
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
	public TypeLog(String name, boolean override) {
		super(name, override);
	}
	/**
	 * Creates a new type log.
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
	public TypeLog(String name) {
		super(name);
	}
	
	/**
	 * Writes data to the standard log file. Implementation of writing is user-dependent.
	 * @param data data to write.
	 */
	protected abstract void writeToStandardLog(LogData data);
	/**
	 * Writes data to the standard log file. Implementation of writing is user-dependent.
	 * @param data data to write.
	 */
	protected abstract void writeToStandardLog(TimedLogData data);
	/**
	 * Writes data to the error log file. Implementation of writing is user-dependent.
	 * @param data data to write.
	 */
	protected abstract void writeToErrorLog(ErrorLogData data);
	/**
	 * Writes data to the error log file. Implementation of writing is user-dependent.
	 * @param data data to write.
	 */
	protected abstract void writeToErrorLog(TracedErrorLogData data);
	
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
		writeToStandardLog(new LogData(mess, caller));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void write(String mess, String caller, double time){
		if(isClosed() || !isLoggingMode(MODE_WRITE)) return;
		writeToStandardLog(new TimedLogData(mess, caller, time));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void writeError(String mess, double time){
		if(isClosed() || !isLoggingMode(MODE_WRITE)) return;
		writeToErrorLog(new ErrorLogData(mess, time, true));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void writeError(String mess, String stacktrace, double time){
		if(isClosed() || !isLoggingMode(MODE_WRITE)) return;
		writeToErrorLog(new TracedErrorLogData(mess, time, stacktrace));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void writeWarning(String mess, double time){
		if(isClosed() || !isLoggingMode(MODE_WRITE)) return;
		writeToErrorLog(new ErrorLogData(mess, time, false));
	}
}
