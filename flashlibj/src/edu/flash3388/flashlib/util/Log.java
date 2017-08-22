package edu.flash3388.flashlib.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.util.beans.DoubleSource;

/**
 * <p>
 * Convenience class for logging states and errors into files, allowing for real-time and post-run code debugging.
 * There are two types of log: Stream or Buffered. Each offers different advantages and is useful for a different type of
 * software.
 * </p>
 * <p>
 * Stream log uses a simple file writer to immediately write logs into the created file. It cannot be saved while open and 
 * should be closed when finished to avoid data loss. This type is most useful for desktop applications or other softwares
 * who do not require logs for emergency situations like power outs, where the log data might be lost.
 * <br>
 * Buffer log stores logging data in a buffer and flushes the data into a file manually, on a time base or when the buffer 
 * is full. The buffer allows us to avoid data loss in a case of power loss thanks to the file being closed when the buffer
 * is not flushing the data. Recommended for robot software tracking.
 * </p>
 * <p>
 * There are several types of logging which can be used simultaneously:
 * <ul>
 * 		<li>File writing: {@link #MODE_WRITE}</li>
 * 		<li>PrintStream writing: {@link #MODE_PRINT}</li>
 * 		<li>External logging interfaces: {@link #MODE_INTERFACES} 
 * ({@link LoggingInterface LoggingInterface})</li>
 * </ul>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class Log{
	
	/**
	 * <p>
	 * Enumeration representing logging types. There are two types of log: Stream or Buffered. Each offers different advantages and is useful for a different type of
	 * software.
	 * </p>
	 * <p>
	 * Stream log uses a simple file writer to immediately write logs into the created file. It cannot be saved while open and 
	 * should be closed when finished to avoid data loss. This type is most useful for desktop applications or other softwares
	 * who do not require logs for emergency situations like power outs, where the log data might be lost.
	 * <br>
	 * Buffer log stores logging data in a buffer and flushes the data into a file manually, on a time base or when the buffer 
	 * is full. The buffer allows us to avoid data loss in a case of power loss thanks to the file being closed when the buffer
	 * is not flushing the data. Recommended for robot software tracking.
	 * </p>
	 * @author Tom Tzook
	 */
	public static enum LoggingType{
		Stream, Buffered
	}
	
	
	/**
	 * Disabled writing mode.
	 * When this mode is set, a call to one of the logging methods will do nothing.
	 */
	public static final byte MODE_DISABLED = 0x00;
	/**
	 * File writing mode.
	 * When this mode is set, a call to one of the logging methods will write data to the logging file or logging 
	 * buffer, depending on the {@link LoggingType}.
	 */
	public static final byte MODE_WRITE = 0x01 << 1;
	/**
	 * Print writing mode.
	 * When this mode is set, a call to one of the logging methods will print data using the
	 * {@link java.io.PrintStream#println()} method in the saved {@link java.io.PrintStream} which can be set using
	 * {@link #setPrintStream(PrintStream)}. The default output is to {@link java.lang.System#out}.
	 */
	public static final byte MODE_PRINT = 0x01 << 2;
	/**
	 * Interfaces writing mode.
	 * When this mode is set, a call to one of the logging methods will pass data to attached {@link LoggingInterface logging interfaces}.
	 */
	public static final byte MODE_INTERFACES = 0x01 << 3;
	/**
	 * Full writing mode.
	 * When this mode is set, a call to one of the logging methods will perform all possible logging functions.
	 */
	public static final byte MODE_FULL = MODE_WRITE | MODE_PRINT | MODE_INTERFACES;
	
	private static final String EXTENSION = ".flog";
	private static final String ERROR_EXTENSION = ".elog";
	private static String parentDirectory = "";
	
	private Vector<LoggingInterface> loggingInterfaces = new Vector<LoggingInterface>(2);
	
	private String name = null;
	
	private PrintStream out = System.out;
	private File logFile, errorLogFile;
	private boolean closed = true;
	private byte logMode;
	
	private DoubleSource timeSourcems;
	private boolean showTime = false;
	
	
	/**
	 * Creates a new log. 
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
	 * <p>
	 * If the given {@link LoggingType} is {@link LoggingType#Buffered Buffered} than a buffer will be created to store
	 * logged data. The buffer will be flush when the {@link #save()} is called, or when the buffer is full.
	 * </p>
	 * 
	 * @param directory The directory in which to save the log
	 * @param name The name of the log
	 * @param override true to override existing log files with the same name, false otherwise.
	 * @param logMode The logging output types
	 */
	public Log(String directory, String name, boolean override, int logMode){
		this.name = name;
		this.logMode = (byte) logMode;
		this.timeSourcems = ()->FlashUtil.millis();
		
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
		directory += name + "/" + "log_" + dateFormat.format(date) + "/";
		File file = new File(directory);
		if(!file.exists())
			file.mkdirs();
		
		dateFormat.applyPattern("_hh_mm");
		
		name = directory + name + dateFormat.format(date);
		byte counter = 0;
		logFile = new File(name + EXTENSION);
		while(logFile.exists() && !override)
			logFile = new File(name + (++counter) + EXTENSION);
		
		try {
			if(isLoggingMode(MODE_PRINT))
				out.println(this.name+"> Log file: "+logFile.getAbsolutePath());
			if(!logFile.exists())
				logFile.createNewFile();
			
			errorLogFile = new File(name + (counter > 0? counter : "") + ERROR_EXTENSION);
			if(!errorLogFile.exists())
				errorLogFile.createNewFile();
			
			closed = false;
		}catch(IOException e){
			e.printStackTrace();
			setLoggingMode(MODE_INTERFACES | MODE_PRINT);
			closed = true;
		}
	}
	/**
	 * Creates a new log.
	 * <p>
	 * Two log files will be created: Standard log and Error log. The former saves normal data logs through the {@link #log(String)}
	 * method call. The later saves errors and warning from {@link #reportError(String)} and {@link #reportWarning(String)}
	 * </p>
	 * <p>
	 * The log files are created in the currently set parentDirectory, which can be set using {@link #setParentDirectory(String)}, 
	 * and the file name is the log name. If the directory or any of its 
	 * parent directories do not exist, they will be created. If override is true, any existing log files with the same name 
	 * will be deleted, otherwise if a log file with same name is present, an integer will be added to the name specifying it
	 * is a new file of the same name. 
	 * </p>
	 * 
	 * @param name The name of the log
	 * @param override If true, any existing log files with the same name will be deleted
	 * @param logMode The logging output types
	 */
	public Log(String name, boolean override, int logMode){
		this(parentDirectory+"logs/", name, override, logMode);
	}
	/**
	 * Creates a new log with a {@link #MODE_FULL full log output mode}. 
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
	 * @param override If true, any existing log files with the same name will be deleted
	 */
	public Log(String name, boolean override){
		this(parentDirectory+"logs/", name, override, MODE_FULL);
	}
	/**
	 * Creates a new log with a {@link #MODE_FULL full log output mode} without overriding any previous logs. 
	 * <p>
	 * Two log files will be created: Standard log and Error log. The former saves normal data logs through the {@link #log(String)}
	 * method call. The later saves errors and warning from {@link #reportError(String)} and {@link #reportWarning(String)}
	 * </p>
	 * <p>
	 * The log files are created in the given directory and the file name is the log name. If the directory or any of its 
	 * parent directories do not exist, they will be created. If a log file with same name is present, an integer will be added to the name specifying it
	 * is a new file of the same name. 
	 * </p>
	 * 
	 * @param name The name of the log
	 */
	public Log(String name){
		this(name, false);
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
	 * Closes the internal writing objects of the log. Called from {@link #close()}. 
	 * Implementation of closing is user-dependent.
	 */
	protected abstract void closeInternal();
	/**
	 * Saves the internal writing objects of the log. Called from {@link #save()}. 
	 * Implementation of saving is user-dependent.
	 */
	protected abstract void saveInternal();
	
	/**
	 * Gets the time data for logging
	 * 
	 * @param secs if true, returns the time in seconds, millis otherwise
	 * @return the time
	 */
	protected double getTime(boolean secs){
		return secs? timeSourcems.get() * 0.001 : timeSourcems.get();
	}
	
	/**
	 * Sets the data source which returns the current time in milliseconds, used when print or logging data.
	 * @param source the time source
	 */
	public void setLoggingTimeSource(DoubleSource source){
		if(source == null)
			throw new NullPointerException("Source is null");
		this.timeSourcems = source;
	}
	/**
	 * Sets whether or not to add a timestamp to standard log output. If true, a timestamp
	 * will be added in seconds.
	 * @param showTime true to show time, false otherwise
	 */
	public void enableShowTime(boolean showTime){
		this.showTime = showTime;
	}
	
	/**
	 * Sets the {@link java.io.PrintStream} to which log data is printed if the output mode include {@link #MODE_PRINT}.
	 * The default stream is {@link java.lang.System#out}.
	 * 
	 * @param out The {@link java.io.PrintStream} to which log data is printed.
	 */
	public void setPrintStream(PrintStream out){
		this.out = out;
	}
	/**
	 * Prints a warning message to the used {@link PrintStream}.
	 * @param warning a warning
	 */
	public void printWarning(String warning){
		if(!isLoggingMode(MODE_PRINT)) return;
		out.println(name+"> ["+Mathf.roundDecimal(getTime(true))+"] <WARNING> : "+warning);
	}
	/**
	 * Prints an error message to the used {@link PrintStream}.
	 * @param error an error
	 */
	public void printError(String error){
		if(!isLoggingMode(MODE_PRINT)) return;
		out.println(name+"> ["+Mathf.roundDecimal(getTime(true))+"] <ERROR> : "+error);
	}
	/**
	 * Prints a message to the used {@link PrintStream}.
	 * @param log a log data
	 */
	public void print(String log){
		if(!isLoggingMode(MODE_PRINT)) return;
		out.println(name+"> "+log);
	}
	
	/**
	 * Writes data directly to the standard log file. If the {@link LoggingType} is {@link LoggingType#Buffered} than
	 * the data is saved in a log buffer which is automatically flushed when full. If the log is closed, nothing will happen.
	 * 
	 * @param mess A line to log to the standard log file or buffer
	 */
	public synchronized void write(String mess){
		if(isClosed() || !isLoggingMode(MODE_WRITE)) return;
		writeToStandardLog(mess);
	}
	/**
	 * Writes data directly to the error log file. If the {@link LoggingType} is {@link LoggingType#Buffered} than
	 * the data is saved in a log buffer which is automatically flushed when full. If the log is closed, nothing will happen.
	 * 
	 * @param mess A line to log to the error log file or buffer
	 */
	public synchronized void writeError(String mess){
		if(isClosed() || !isLoggingMode(MODE_WRITE)) return;
		mess = "[" +Mathf.roundDecimal(getTime(false))+ "] : " + mess;
		writeToErrorLog(mess);
	}
	/**
	 * Writes data directly to the error log file. If the {@link LoggingType} is {@link LoggingType#Buffered} than
	 * the data is saved in a log buffer which is automatically flushed when full. If the log is closed, nothing will happen.
	 * 
	 * @param mess A line to log to the error log file or buffer
	 * @param stacktrace A stack trace of the error
	 */
	public synchronized void writeError(String mess, String stacktrace){
		if(isClosed() || !isLoggingMode(MODE_WRITE)) return;
		write("<ERROR> : "+mess);
		mess = "[" +Mathf.roundDecimal(getTime(false))+ "] <ERROR> : " + mess;
		writeToErrorLog(mess, stacktrace);
	}
	/**
	 * Writes data directly to the error log file. If the {@link LoggingType} is {@link LoggingType#Buffered} than
	 * the data is saved in a log buffer which is automatically flushed when full. If the log is closed, nothing will happen.
	 * 
	 * @param mess A line to log to the error log file or buffer
	 */
	public synchronized void writeWarning(String mess){
		if(isClosed() || !isLoggingMode(MODE_WRITE)) return;
		write("<WARNING> : "+mess);
		mess = "[" +Mathf.roundDecimal(getTime(false))+ "] <WARNING> : " + mess;
		writeToErrorLog(mess);
	}
	
	/**
	 * Closes the log and saves it. Closing the log will disable file writing.
	 */
	public synchronized void close(){
		if(isClosed()) return;
		save();
		closeInternal();
		closed = true;
	}
	
	/**
	 * Deletes the log file and closes file writing features. Doing so will disable file writing.
	 */
	public synchronized void delete(){
		if(!isClosed())
			close();
		logFile.delete();
		errorLogFile.delete();
	}
	
	/**
	 * Saves data into the log files. If the {@link LoggingType} is {@link LoggingType#Buffered} than the data buffers
	 * are flushed to the log files, otherwise the {@link FileWriter file writer streams} are flushed using {@link FileWriter#flush()}.
	 * If the log is closed or if the logging mode is set to {@link #MODE_DISABLED} than nothing will happen.
	 */
	public synchronized void save(){
		saveInternal();
		print("Log Saved");
	}
	
	/**
	 * Gets the name of the log.
	 * 
	 * @return The name of this log.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Gets the {@link LoggingType} set for use in this log.
	 * 
	 * @return The {@link LoggingType} used for this log.
	 */
	public abstract LoggingType getLoggingType();
	
	/**
	 * Gets the {@link File} object of the error log file.
	 * @return the error log file.
	 */
	public File getErrorLogFile(){
		return errorLogFile;
	}
	
	/**
	 * Gets the {@link File} object of the standard log file.
	 * @return the standard log file.
	 */
	public File getStandardLogFile(){
		return logFile;
	}
	
	/**
	 * Gets whether or not the log is closed. If the log is closed than writing to the log files is not possible.
	 * 
	 * @return True if the log is closed.
	 */
	public boolean isClosed(){
		return closed;
	}
	
	/**
	 * Sets the logging mode to {@link #MODE_DISABLED} effectively disabling functionality of this log.
	 */
	public void disable(){
		setLoggingMode(MODE_DISABLED);
	}
	
	/**
	 * Gets whether or not the logging mode is set to {@link #MODE_DISABLED}.
	 * 
	 * @return True if the current logging mode is {@link #MODE_DISABLED}.
	 */
	public boolean isDisabled(){
		return logMode == MODE_DISABLED;
	}
	
	/**
	 * Sets the logging mode of this log. Logging mode determines the outputs used by this log when a logging is occurred.
	 * There are several types of logging which can be used simultaneously:
	 * <ul>
 	 * 		<li>File writing: {@link #MODE_WRITE}</li>
 	 * 		<li>PrintStream writing: {@link #MODE_PRINT}</li>
 	 * 		<li>External logging interfaces: {@link #MODE_INTERFACES}</li>
 	 * </ul>
 	 * 
 	 * <p>
 	 * To use several logging modes at the same time, use bitwise OR:<br><br>
 	 * {@code
 	 * 		setLoggingMode(MODE_PRINT | MODE_WRITE);
 	 * }<br><br>
 	 * This will set the logging mode to use {@link #MODE_PRINT} and {@link #MODE_WRITE}.
 	 * </p>
 	 * 
	 * @param mode The logging mode to set for use in this log
	 */
	public void setLoggingMode(int mode){
		this.logMode = (byte) mode;
	}
	
	/**
	 * Gets the logging mode used by this log. Logging mode determines the outputs used by this log when a logging is occurred.
	 * There are several types of logging which can be used simultaneously:
	 * <ul>
 	 * 		<li>File writing: {@link #MODE_WRITE}</li>
 	 * 		<li>PrintStream writing: {@link #MODE_PRINT}</li>
 	 * 		<li>External logging interfaces: {@link #MODE_INTERFACES}</li>
 	 * </ul> 
	 * <p>
	 * To determine what logging modes are set, use bitwise AND and compare the returned byte with the desired mode:
	 * <br><br>
	 * {@code
	 * 		(getLoggingMode() & MODE_WRITE) != 0
	 * }
	 * <br> <br>
	 * The result of this operation is true when the logging mode include {@link #MODE_WRITE}
	 * </p>
	 * 
	 * @return The logging mode use by this log
	 */
	public int getLoggingMode(){
		return logMode;
	}
	
	/**
	 * Gets whether or not the given mode is the current logging mode set in the log.
	 * There are several types of logging which can be used simultaneously:
	 * <ul>
 	 * 		<li>File writing: {@link #MODE_WRITE}</li>
 	 * 		<li>PrintStream writing: {@link #MODE_PRINT}</li>
 	 * 		<li>External logging interfaces: {@link #MODE_INTERFACES}</li>
 	 * </ul> 
	 * 
	 * @param mode logging mode to check
	 * @return true if the log is in the given mode.
	 */
	public boolean isLoggingMode(int mode){
		return (logMode & mode) != 0;
	}
	
	/**
	 * Adds a {@link LoggingInterface} to this log. {@link LoggingInterface}s allow for log data to pass to external sources.
	 * If the current logging mode does not include {@link #MODE_INTERFACES} data will not be transfered to the attached interfaces.
	 * 
	 * @param in {@link LoggingInterface} to add.
	 */ 
	public void addLoggingInterface(LoggingInterface in){
		loggingInterfaces.addElement(in);
	}
	
	/**
	 * Logs the given data to all attached log interfaces by calling {@link LoggingInterface#log(String)}.
	 * @param log data to log
	 */
	public void interfaceLog(String log){
		if(!isLoggingMode(MODE_INTERFACES)) return;
		for(Enumeration<LoggingInterface> lEnum = loggingInterfaces.elements(); lEnum.hasMoreElements();)
			lEnum.nextElement().log(log);
	}
	/**
	 * Logs the given data to all attached log interfaces by calling {@link LoggingInterface#reportError(String)}.
	 * @param error error to log
	 */
	public void interfaceReportError(String error){
		if(!isLoggingMode(MODE_INTERFACES)) return;
		for(Enumeration<LoggingInterface> lEnum = loggingInterfaces.elements(); lEnum.hasMoreElements();)
			lEnum.nextElement().reportError(error);
	}
	/**
	 * Logs the given data to all attached log interfaces by calling {@link LoggingInterface#reportWarning(String)}.
	 * @param warning warning to log
	 */
	public void interfaceReportWarning(String warning){
		if(!isLoggingMode(MODE_INTERFACES)) return;
		for(Enumeration<LoggingInterface> lEnum = loggingInterfaces.elements(); lEnum.hasMoreElements();)
			lEnum.nextElement().reportWarning(warning);
	}
	
	/**
	 * Reports an error. The data output depends on the logging mode currently set:
	 * <ul>
	 * 		<li> If the logging mode includes {@link #MODE_WRITE} than data is written to both the standard and error files or buffers. A stack trace of the error is added as well. </li>
	 * 		<li> If the logging mode includes {@link #MODE_PRINT} than data is printed to the set {@link PrintStream}</li>
	 * 		<li> If the logging mode includes {@link #MODE_INTERFACES} than data is passed to all the attached {@link LoggingInterface} to {@link LoggingInterface#reportError(String)} </li>
	 * </ul>
	 * @param error String containing data about the error.
	 */
	public void reportError(String error){
		if(isDisabled()) return;
		
		writeError(error, getErrorStackTrace());
		printError(error);
		interfaceReportError(error);
	}
	
	/**
	 * Reports a warning. The data output depends on the logging mode currently set:
	 * <ul>
	 * 		<li> If the logging mode includes {@link #MODE_WRITE} than data is written to both the standard and error files or buffers.</li>
	 * 		<li> If the logging mode includes {@link #MODE_PRINT} than data is printed to the set {@link PrintStream}</li>
	 * 		<li> If the logging mode includes {@link #MODE_INTERFACES} than data is passed to all the attached {@link LoggingInterface} to {@link LoggingInterface#reportWarning(String)} </li>
	 * </ul>
	 * @param warning String containing data about the warning.
	 */
	public void reportWarning(String warning){
		if(isDisabled()) return;
		
		printWarning(warning);
		writeWarning(warning);
		interfaceReportWarning(warning);
	}
	
	/**
	 * Logs data with information about the sender of the log data. This method calls {@link #log(String, Class)} and
	 * passes it the class which called it using {@link Thread#getStackTrace()}.
	 * 
	 * @param msg The log data.
	 */
	public void log(String msg){
		log(msg, getCallerClass());
	}
	/**
	 * Logs data with information about the sender of the log data. This method calls {@link #log(String, String)} and
	 * passes it the value of {@link Class#getName()} as the caller.
	 * 
	 * @param msg The log data.
	 * @param caller The calling class.
	 */
	public void log(String msg, Class<?> caller){
		log(msg, caller.getName());
	}
	
	/**
	 * Logs data with information about the sender of the log data. The data output depends on the logging mode currently set:
	 * <ul>
	 * 		<li> If the logging mode includes {@link #MODE_WRITE} than data is written to the standard file or buffer.</li>
	 * 		<li> If the logging mode includes {@link #MODE_PRINT} than data is printed to the set {@link PrintStream}</li>
	 * 		<li> If the logging mode includes {@link #MODE_INTERFACES} than data is passed to all the attached {@link LoggingInterface} to {@link LoggingInterface#log(String)} </li>
	 * </ul>
	 * @param msg The log data.
	 * @param caller The name of the caller to the log.
	 */
	public void log(String msg, String caller){
		if(isDisabled()) return;
		
		msg = "("+caller+") : "+msg;
		if(showTime)
			msg = "["+Mathf.roundDecimal(getTime(true))+"] " +msg;
			
		
		write(msg);
		print(msg);
		interfaceLog(msg);
	}
	
	/**
	 * Logs data with information about the current time. Calls {@link #logTime(String, double)} and passes it 
	 * the timestamp from the time interface used by this log.
	 * 
	 * @param msg The log data.
	 */
	public void logTime(String msg){
		logTime(msg, getTime(true));
	}
	
	/**
	 * Logs data with information about the current time. The data output depends on the logging mode currently set:
	 * <ul>
	 * 		<li> If the logging mode includes {@link #MODE_WRITE} than data is written to the standard file or buffer.</li>
	 * 		<li> If the logging mode includes {@link #MODE_PRINT} than data is printed to the set {@link PrintStream}</li>
	 * 		<li> If the logging mode includes {@link #MODE_INTERFACES} than data is passed to all the attached {@link LoggingInterface} to {@link LoggingInterface#log(String)} </li>
	 * </ul>
	 * @param msg The log data.
	 * @param time The current time stamp.
	 */
	public void logTime(String msg, double time){
		if(isDisabled()) return;
		
		msg = "[" + Mathf.roundDecimal(time) + "] : ----------> " + msg;
		
		write(msg);
		print(msg);
		interfaceLog(msg);
	}
	
	private static String getCallerClass(){
		StackTraceElement[] traces = Thread.currentThread().getStackTrace();
		if(traces.length > 3)
			return traces[3].getClassName();
		return "";
	}
	private static String getErrorStackTrace(){
		StackTraceElement[] traces = Thread.currentThread().getStackTrace();
		String trace = "";
		for(byte i = 3; i < traces.length; i++)
			trace += "\t"+traces[i].toString()+"\n";
		return trace;
	}
	
	/**
	 * Sets the directory in which to save logs files whose directories were not defined. Such logs are created using
	 * {@link #Log(String, boolean, int)}. The default directory is the local directory.
	 * 
	 * @param directory The directory in which to save logs.
	 */
	public static void setParentDirectory(String directory){
		parentDirectory = directory;
		if(!parentDirectory.endsWith("/"))
			parentDirectory += "/";
	}
	/**
	 * Deletes the default log folder. The folder is the "logs" folder in the parent directory
	 * which is set by {@link #setParentDirectory(String)}.
	 */
	public static void deleteLogFolder(){
		File dir = new File(parentDirectory+"logs/");
		if(dir.exists())
			dir.delete();
	}
	
	/**
	 * Creates and returns a new buffered log. The created class may differ depending on platforms. 
	 * @param name the name of the log.
	 * @return a new buffered log. 
	 */
	public static Log createBufferedLog(String name){
		return new SimpleBufferedLog(name);
	}
	/**
	 * Creates and returns a new stream log. The created class may differ depending on platforms. 
	 * @param name the name of the log.
	 * @return a new stream log. 
	 */
	public static Log createStreamLog(String name){
		return new SimpleStreamLog(name);
	}
	/**
	 * Creates a new log by a given type and name.
	 * @param name the log name
	 * @param type the log type
	 * @return a new log
	 */
	public static Log createLogByType(String name, LoggingType type){
		switch(type){
			case Buffered: return createBufferedLog(name);
			case Stream: return createStreamLog(name);
		}
		throw new IllegalArgumentException("Unknown logging type");
	}
}
