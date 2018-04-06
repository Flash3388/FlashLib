package edu.flash3388.flashlib.util;

import java.io.FileWriter;
import java.io.IOException;

/**
 * A stream log using {@link FileWriter}s to write data to the log files.
 * <p>
 * Data is printed and saved in XML format.
 * </p>
 * <p>
 * Stream log uses a simple file writer to immediately write logs into the created file. It cannot be saved while open and 
 * should be closed when finished to avoid data loss. This type is most useful for desktop applications or other softwares
 * who do not require logs for emergency situations like power outs, where the log data might be lost.
 * </p>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.1
 */
public class XMLStreamLog extends TypeLog{
	
	private FileWriter stdWriter;
	private FileWriter errWriter;
	
	/**
	 * Creates a new XML log.
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
	public XMLStreamLog(String directory, String name, boolean override, int logMode) {
		super(directory, name, override, logMode);
		
		init();
	}
	/**
	 * Creates a new XML log.
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
	public XMLStreamLog(String name, boolean override, int logMode) {
		super(name, override, logMode);
		
		init();
	}
	/**
	 * Creates a new XML log.
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
	public XMLStreamLog(String name, boolean override) {
		super(name, override);
		
		init();
	}
	/**
	 * Creates a new XML log.
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
	public XMLStreamLog(String name) {
		super(name);
		
		init();
	}
	
	private void init(){
		try {
			stdWriter = new FileWriter(getStandardLogFile(), false);
			stdWriter.write("<?xml version=\"1.0\" ?>\n");
			stdWriter.write("<xmlog>\n");
			
			errWriter = new FileWriter(getErrorLogFile(), false);
			errWriter.write("<?xml version=\"1.0\" ?>\n");
			errWriter.write("<xmlog>\n");
		} catch (IOException e) {
			e.printStackTrace();
			close();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeToStandardLog(LogData data) {
		try {
			stdWriter.write(String.format("\t<log caller=\"%s\">%s</log>\n", data.caller, data.log));
		} catch (IOException e) {
			e.printStackTrace();
			close();
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeToStandardLog(TimedLogData data) {
		try {
			stdWriter.write(String.format("\t<log caller=\"%s\" time=\"%.3f\">%s</log>\n", 
					data.caller, data.time, data.log));
		} catch (IOException e) {
			e.printStackTrace();
			close();
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeToErrorLog(ErrorLogData data) {
		try {
			errWriter.write(String.format("\t<%s time=\"%.3f\">\n\t\t<value>%s</value>\n\t</%s>\n", 
					data.caller, data.time, data.log, data.caller));
		} catch (IOException e) {
			e.printStackTrace();
			close();
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeToErrorLog(TracedErrorLogData data) {
		try {
			StringBuffer writeBuffer = new StringBuffer();
			
			writeBuffer.append(String.format("\t<%s time=\"%.3f\">\n\t\t<value>%s</value>\n", 
					data.caller, data.time, data.log));
			writeBuffer.append("\t\t<stacktrace>\n");
			for (int i = data.traceIndex; i < data.stacktrace.length; i++) {
				writeBuffer.append(String.format("\t\t\t<element>%s</element>\n", data.stacktrace[i].toString()));
			}
			writeBuffer.append("\t\t</stacktrace>\n");
			writeBuffer.append(String.format("\t</%s>\n", data.caller));
			
			errWriter.write(writeBuffer.toString());
		} catch (IOException e) {
			e.printStackTrace();
			close();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void closeInternal() {
		try {
			if(stdWriter != null){
				stdWriter.write("</xmlog>");
				stdWriter.close();
			}
			stdWriter = null;
			if(errWriter != null){
				errWriter.write("</xmlog>");
				errWriter.close();
			}
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
