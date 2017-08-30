package edu.flash3388.flashlib.util;

import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * A simple buffered log. Uses buffers to gather data which is then saved into the log files manually or when
 * the buffer is full.
 * <p>
 * Data is printed and saved as simple formated strings.
 * </p>
 * <p>
 * Buffer log stores logging data in a buffer and flushes the data into a file manually, on a time base or when the buffer 
 * is full. The buffer allows us to avoid data loss in a case of power loss thanks to the file being closed when the buffer
 * is not flushing the data. Recommended for robot software tracking.
 * </p>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public class SimpleBufferedLog extends SimpleLog{

	private String[] stdBuffer;
	private String[] errBuffer;
	
	private int stdIdx, errIdx;
	
	/**
	 * Creates a new simple buffered log.
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
	 * @param bufferSize max amount of lines in the buffer
	 * 
	 * @see Log#Log(String, String, boolean, int)
	 */
	public SimpleBufferedLog(String directory, String name, boolean override, int logMode, int bufferSize) {
		super(directory, name, override, logMode);
		
		init(bufferSize);
	}
	/**
	 * Creates a new simple buffered log.
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
	 * @param bufferSize max amount of lines in the buffer
	 * 
	 * @see Log#Log(String, boolean, int)
	 */
	public SimpleBufferedLog(String name, boolean override, int logMode, int bufferSize) {
		super(name, override, logMode);
		
		init(bufferSize);
	}
	/**
	 * Creates a new simple buffered log.
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
	 * @param bufferSize max amount of lines in the buffer
	 * 
	 * @see Log#Log(String, boolean)
	 */
	public SimpleBufferedLog(String name, boolean override, int bufferSize) {
		super(name, override);
		
		init(bufferSize);
	}
	/**
	 * Creates a new simple buffered log.
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
	 * @param bufferSize max amount of lines in the buffer
	 * 
	 * @see Log#Log(String)
	 */
	public SimpleBufferedLog(String name, int bufferSize) {
		super(name);
		
		init(bufferSize);
	}
	/**
	 * Creates a new simple buffered log with a buffer size of 100.
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
	public SimpleBufferedLog(String name) {
		super(name);
		
		init(100);
	}

	private void init(int bufferSize){
		stdBuffer = new String[bufferSize];
		errBuffer = new String[bufferSize];
		
		stdIdx = errIdx = 0;
	}
	private synchronized void flushLogFile(){
		if(stdIdx == 0) return;
		try {
			FileWriter writer = new FileWriter(getStandardLogFile(), true);
			for (int i = 0; i < stdIdx; i++)
				writer.write(stdBuffer[i]);
			writer.close();
		} catch (IOException e) {
			close();
		}finally{
			stdIdx = 0;
		}
	}
	private synchronized void flushErrorLogFile(){
		if(errIdx == 0) return;
		try {
			FileWriter writer = new FileWriter(getErrorLogFile(), true);
			for (int i = 0; i < errIdx; i++)
				writer.write(errBuffer[i]);
			writer.close();
		} catch (IOException e) {
			close();
		}finally{
			errIdx = 0;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the logging buffer is full, data is saved and the buffer is reset.
	 * </p>
	 */
	@Override
	protected void writeToStandardLog(String log) {
		if(stdIdx >= stdBuffer.length)
			flushLogFile();
		stdBuffer[stdIdx++] = log;
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the logging buffer is full, data is saved and the buffer is reset.
	 * </p>
	 */
	@Override
	protected void writeToErrorLog(String log) {
		if(errIdx >= errBuffer.length)
			flushLogFile();
		errBuffer[errIdx++] = log;
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the logging buffer is full, data is saved and the buffer is reset.
	 * </p>
	 */
	@Override
	protected void writeToErrorLog(String log, String stacktrace) {
		if(errIdx >= errBuffer.length)
			flushLogFile();
		errBuffer[errIdx++] = log;
		errBuffer[errIdx++] = stacktrace;
	}
	/**
	 * Does nothing
	 */
	@Override
	protected void closeInternal() {
		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternal() {
		flushLogFile();
		flushErrorLogFile();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LoggingType getLoggingType() {
		return LoggingType.Buffered;
	}
	
	/**
	 * Sets the logging buffer size. If data already exists in the buffer, it is saved and then the buffer is
	 * recreated in the given size.
	 * @param bufferSize max amount of lines in the buffer
	 */
	public void setBufferSize(int bufferSize){
		flushLogFile();
		flushErrorLogFile();
		init(bufferSize);
	}
}
