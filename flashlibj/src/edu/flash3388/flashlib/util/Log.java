package edu.flash3388.flashlib.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.io.FileStream;

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
public final class Log{
	
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
	
	private static final byte BUFFER_SIZE = 50;
	private static final String EXTENSION = ".flog";
	private static final String ERROR_EXTENSION = ".elog";
	private static String parentDirectory = "";
	
	private Vector<LoggingInterface> loggingInterfaces = new Vector<LoggingInterface>(2);
	
	private String name = null;
	
	private PrintStream out = System.out;
	private FileWriter writerLog = null, writerErrorLog = null;
	private String[] logLines = null, errorLines = null;
	private String absPath = null, absPathError = null;
	private boolean closed = true;
	private byte logMode, indexLog, indexErrorLog;
	private LoggingType type;
	
	
	/**
	 * Creates a new log using a given {@link LoggingType} and logging mode. 
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
	 * @param type The {@link LoggingType} of the created log
	 * @param override If true, any existing log files with the same name will be deleted
	 * @param logMode The logging output types
	 */
	public Log(String directory, String name, LoggingType type, boolean override, byte logMode){
		this.name = name;
		this.logMode = logMode;
		this.type = type;
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
		directory += name + "/" + "log_" + dateFormat.format(date) + "/";
		File file = new File(directory);
		if(!file.exists())
			file.mkdirs();
		
		byte counter = 0;
		File logFile = new File(directory + name + EXTENSION);
		while(logFile.exists() && !override)
			logFile = new File(directory + name + (++counter) + EXTENSION);
		
		try {
			if((logMode & MODE_PRINT) != 0)
				out.println(name+"> Log file: "+logFile.getAbsolutePath());
			if(!logFile.exists())
				logFile.createNewFile();
			
			File errorFile = new File(directory + name + (counter > 0? counter : "") + ERROR_EXTENSION);
			if(!errorFile.exists())
				errorFile.createNewFile();
			
			dateFormat = new SimpleDateFormat("hh:mm:ss");
			String timestr = "Time: "+dateFormat.format(date);
			absPath = logFile.getAbsolutePath();
			absPathError = errorFile.getAbsolutePath();
			
			if(type == LoggingType.Buffered){
				logLines = new String[BUFFER_SIZE];
				errorLines = new String[BUFFER_SIZE];
				
				FileStream.writeLine(absPath, timestr);
				FileStream.writeLine(absPathError, timestr);
			}else{
				writerLog = new FileWriter(logFile);
				writerErrorLog = new FileWriter(errorFile);
				
				timestr += "\n";
				writerLog.write(timestr);
				writerErrorLog.write(timestr);
			}
			
			closed = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Creates a new log using a given {@link LoggingType} and logging mode. 
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
	 * <p>
	 * If the given {@link LoggingType} is {@link LoggingType#Buffered Buffered} than a buffer will be created to store
	 * logged data. The buffer will be flush when the {@link #save()} is called, or when the buffer is full.
	 * </p>
	 * 
	 * @param name The name of the log
	 * @param type The {@link LoggingType} of the created log
	 * @param override If true, any existing log files with the same name will be deleted
	 * @param logMode The logging output types
	 */
	public Log(String name, LoggingType type, boolean override, byte logMode){
		this(parentDirectory+"logs/", name, type, override, logMode);
	}
	/**
	 * Creates a new log using a given {@link LoggingType} with a {@link #MODE_FULL full log output mode}. 
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
	 * @param name The name of the log
	 * @param type The {@link LoggingType} of the created log
	 * @param override If true, any existing log files with the same name will be deleted
	 */
	public Log(String name, LoggingType type, boolean override){
		this(parentDirectory+"logs/", name, type, override, MODE_FULL);
	}
	/**
	 * Creates a new {@link LoggingType#Stream} log with a {@link #MODE_FULL full log output mode}. 
	 * <p>
	 * Two log files will be created: Standard log and Error log. The former saves normal data logs through the {@link #log(String)}
	 * method call. The later saves errors and warning from {@link #reportError(String)} and {@link #reportWarning(String)}
	 * </p>
	 * <p>
	 * The log files are created in the given directory and the file name is the log name. If the directory or any of its 
	 * parent directories do not exist, they will be created. If a log file with same name is present, an integer will be added to the name specifying it
	 * is a new file of the same name. 
	 * </p>
	 * <p>
	 * If the given {@link LoggingType} is {@link LoggingType#Buffered Buffered} than a buffer will be created to store
	 * logged data. The buffer will be flush when the {@link #save()} is called, or when the buffer is full.
	 * </p>
	 * 
	 * @param name The name of the log
	 */
	public Log(String name){
		this(name, LoggingType.Stream, false);
	}
	
	private synchronized void flushLogFile(){
		if(indexLog == 0) return;
		FileStream.appendLines(absPath, logLines);
		indexLog = 0;
	}
	private synchronized void flushErrorLogFile(){
		if(indexErrorLog == 0) return;
		FileStream.appendLines(absPathError, errorLines);
		indexErrorLog = 0;
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
	 * Writes data directly to the standard log file. If the {@link LoggingType} is {@link LoggingType#Buffered} than
	 * the data is saved in a log buffer which is automatically flushed when full. If the log is closed, nothing will happen.
	 * 
	 * @param mess A line to log to the standard log file or buffer
	 */
	public synchronized void write(String mess){
		if(isClosed()) return;
		if(type == LoggingType.Buffered){
			if(indexLog >= BUFFER_SIZE)
				flushLogFile();
			logLines[indexLog++] = mess;
		}else{
			try {
				writerLog.write(mess+"\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Writes data directly to the error log file. If the {@link LoggingType} is {@link LoggingType#Buffered} than
	 * the data is saved in a log buffer which is automatically flushed when full. If the log is closed, nothing will happen.
	 * 
	 * @param mess A line to log to the error log file or buffer
	 */
	public synchronized void writeError(String mess){
		if(isClosed()) return;
		mess = (FlashUtil.secs()) + ": " + mess;
		if(type == LoggingType.Buffered){
			if(indexErrorLog >= BUFFER_SIZE)
				flushErrorLogFile();
			errorLines[indexErrorLog++] = mess;
		}else{
			try {
				writerErrorLog.write(mess+"\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Writes data directly to the error log file. If the {@link LoggingType} is {@link LoggingType#Buffered} than
	 * the data is saved in a log buffer which is automatically flushed when full. If the log is closed, nothing will happen.
	 * 
	 * @param mess A line to log to the error log file or buffer
	 * @param stacktrace A stack trace of the error
	 */
	public synchronized void writeError(String mess, String stacktrace){
		if(isClosed()) return;
		mess = (FlashUtil.secs()) + ": " + mess;
		if(type == LoggingType.Buffered){
			if(indexErrorLog + 1 >= BUFFER_SIZE)
				flushErrorLogFile();
			errorLines[indexErrorLog++] = mess;
			errorLines[indexErrorLog++] = stacktrace;
		}else{
			try {
				writerErrorLog.write(mess+"\n");
				writerErrorLog.write(stacktrace);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Closes the log and saves it. Closing the log will disable file writing.
	 */
	public synchronized void close(){
		if(isClosed()) return;
		save();
		closed = true;
	}
	
	/**
	 * Deletes the log file and closes file writing features. Doing so will disable file writing.
	 */
	public synchronized void delete(){
		if(!isClosed())
			close();
		new File(absPath).delete();
		new File(absPathError).delete();
	}
	
	/**
	 * Saves data into the log files. If the {@link LoggingType} is {@link LoggingType#Buffered} than the data buffers
	 * are flushed to the log files, otherwise the {@link FileWriter file writer streams} are flushed using {@link FileWriter#flush()}.
	 * If the log is closed or if the logging mode is set to {@link #MODE_DISABLED} than nothing will happen.
	 */
	public synchronized void save(){
		if(isClosed() || isDisabled()) return;
		
		if(type == LoggingType.Buffered){
			flushLogFile();
			flushErrorLogFile();
		}else{
			try {
				writerLog.flush();
				writerErrorLog.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		closed = false;
		if((logMode & MODE_PRINT) != 0)
			out.println(name + "> " + FlashUtil.secs() + " : Log Saved");
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
	public LoggingType getLoggingType(){
		return type;
	}
	
	/**
	 * Gets whether or not the log is closed. If the log is closed than writing to the log files is not possible.
	 * 
	 * @return True if the log is closed.
	 */
	public boolean isClosed(){
		return closed || (type == LoggingType.Stream && (writerLog == null || writerErrorLog == null));
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
	public void setLoggingMode(byte mode){
		this.logMode = mode;
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
	public byte getLoggingMode(){
		return logMode;
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
		String err = "ERROR\n\t" + 
					FlashUtil.secs() + " : " + error;
		if((logMode & MODE_WRITE) != 0){
			String trace = getErrorStackTrace();
			writeError(error, trace);
			write(err);
		}
		if((logMode & MODE_PRINT) != 0)
			System.err.println(name + "> " + err);
		if((logMode & MODE_INTERFACES) != 0){
			for(Enumeration<LoggingInterface> lEnum = loggingInterfaces.elements(); lEnum.hasMoreElements();)
				lEnum.nextElement().reportError(error);
		}
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
		String war = "WARNING\n\t" + 
				FlashUtil.secs() + " : " + warning;
		if((logMode & MODE_PRINT) != 0)
			System.err.println(name + "> " + war);
		if((logMode & MODE_WRITE) != 0){
			writeError("WARNING - " +warning);
			write(war);
		}
		if((logMode & MODE_INTERFACES) != 0){
			for(Enumeration<LoggingInterface> lEnum = loggingInterfaces.elements(); lEnum.hasMoreElements();)
				lEnum.nextElement().reportWarning(warning);
		}
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
		msg = caller+": "+msg;
		if((logMode & MODE_WRITE) != 0)
			write(msg);
		if((logMode & MODE_PRINT) != 0)
			out.println(name + "> " + msg);
		if((logMode & MODE_INTERFACES) != 0){
			for(Enumeration<LoggingInterface> lEnum = loggingInterfaces.elements(); lEnum.hasMoreElements();)
				lEnum.nextElement().log(msg);
		}
	}
	
	/**
	 * Logs data with information about the current time. Calls {@link #logTime(String, double)} and passes it 
	 * {@link FlashUtil#secs()} as a time stamp.
	 * 
	 * @param msg The log data.
	 */
	public void logTime(String msg){
		logTime(msg, FlashUtil.secs());
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
		msg = time + " : ---------->" + msg;
		if((logMode & MODE_WRITE) != 0)
			write(msg);
		if((logMode & MODE_PRINT) != 0)
			out.println(name + "> " + msg);
		if((logMode & MODE_INTERFACES) != 0){
			for(Enumeration<LoggingInterface> lEnum = loggingInterfaces.elements(); lEnum.hasMoreElements();)
				lEnum.nextElement().log(msg);
		}
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
	 * {@link #Log(String, LoggingType, boolean, byte)}.
	 * 
	 * @param directory The directory in which to save logs.
	 */
	public static void setParentDirectory(String directory){
		parentDirectory = directory;
		if(!parentDirectory.endsWith("/"))
			parentDirectory += "/";
	}
	
	public static void deleteLogFolder(){
		File dir = new File(parentDirectory+"logs/");
		if(dir.exists())
			dir.delete();
	}
}
