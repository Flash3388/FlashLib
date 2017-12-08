package edu.flash3388.flashlib.util;

import java.io.FileWriter;
import java.io.IOException;

/**
 * A simple stream log using {@link FileWriter}s to write data to the log files.
 * <p>
 * Data is printed and saved as simple formated strings.
 * </p>
 * <p>
 * Stream log uses a simple file writer to immediately write logs into the created file. It cannot be saved while open and 
 * should be closed when finished to avoid data loss. This type is most useful for desktop applications or other softwares
 * who do not require logs for emergency situations like power outs, where the log data might be lost.
 * </p>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public class SimpleStreamLog extends SimpleLog{

	private FileWriter stdWriter;
	private FileWriter errWriter;
	
	/**
	 * Creates a new simple stream log.
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
	public SimpleStreamLog(String directory, String name, boolean override, int logMode) {
		super(directory, name, override, logMode);
		
		init();
	}
	/**
	 * Creates a new simple stream log.
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
	public SimpleStreamLog(String name, boolean override, int logMode) {
		super(name, override, logMode);
		
		init();
	}
	/**
	 * Creates a new simple stream log.
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
	public SimpleStreamLog(String name, boolean override) {
		super(name, override);
		
		init();
	}
	/**
	 * Creates a new simple stream log.
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
	public SimpleStreamLog(String name) {
		super(name);
		
		init();
	}

	private void init(){
		try {
			stdWriter = new FileWriter(getStandardLogFile(), false);
			errWriter = new FileWriter(getErrorLogFile(), false);
		} catch (IOException e) {
			e.printStackTrace();
			close();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeToStandardLog(String log) {
		try {
			stdWriter.write(log);
			stdWriter.write("\n");
		} catch (IOException e) {
			close();
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeToErrorLog(String log) {
		try {
			stdWriter.write(log);
			stdWriter.write("\n");
		} catch (IOException e) {
			close();
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeToErrorLog(String log, String stacktrace) {
		try {
			errWriter.write(log);
			errWriter.write("\n");
			errWriter.write(stacktrace);
			errWriter.write("\n");
		} catch (IOException e) {
			close();
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void closeInternal() {
		try {
			if(stdWriter != null)
				stdWriter.close();
			stdWriter = null;
			if(errWriter != null)
				errWriter.close();
			errWriter = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternal() {
		try {
			if(stdWriter != null)
				stdWriter.flush();
			if(errWriter != null)
				errWriter.flush();
		} catch (IOException e) {
			close();
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LoggingType getLoggingType() {
		return LoggingType.Stream;
	}
}
