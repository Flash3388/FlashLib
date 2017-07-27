package edu.flash3388.flashlib.util;

import java.io.FileWriter;
import java.io.IOException;

/**
 * A simple stream log using {@link FileWriter}s to write data to the log files.
 * <p>
 * Stream log uses a simple file writer to immediately write logs into the created file. It cannot be saved while open and 
 * should be closed when finished to avoid data loss. This type is most useful for desktop applications or other softwares
 * who do not require logs for emergency situations like power outs, where the log data might be lost.
 * </p>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public class SimpleLog extends Log{

	private FileWriter stdWriter;
	private FileWriter errWriter;
	private boolean showTime;
	
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
	 * @param showTime true to always timestamp logs, false otherwise.
	 * 
	 * @see Log#Log(String, String, boolean, int)
	 */
	public SimpleLog(String directory, String name, boolean override, int logMode, boolean showTime) {
		super(directory, name, override, logMode);
		
		init(showTime);
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
	 * @param showTime true to always timestamp logs, false otherwise.
	 * 
	 * @see Log#Log(String, boolean, int)
	 */
	public SimpleLog(String name, boolean override, int logMode, boolean showTime) {
		super(name, override, logMode);
		
		init(showTime);
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
	 * @param showTime true to always timestamp logs, false otherwise.
	 * 
	 * @see Log#Log(String, boolean)
	 */
	public SimpleLog(String name, boolean override, boolean showTime) {
		super(name, override);
		
		init(showTime);
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
	 * @param showTime true to always timestamp logs, false otherwise.
	 * 
	 * @see Log#Log(String)
	 */
	public SimpleLog(String name, boolean showTime) {
		super(name);
		
		init(showTime);
	}
	/**
	 * Creates a new simple stream log. With a timestamp output for every standard log output.
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
		this(name, true);
	}

	private void init(boolean showTime){
		this.showTime = showTime;
		
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
		if(showTime)
			log = FlashUtil.millis() + ": " + log;
		try {
			stdWriter.write(log + "\n");
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
			errWriter.write(log);
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
	
	/**
	 * Sets whether or not to add a timestamp to all standard log outputs. If true, 
	 * timestamp in milliseconds will be added for every standard log writes.
	 * @param enable true to enable, false otherwise.
	 */
	public void enableTimeShow(boolean enable){
		showTime = enable;
	}
}
