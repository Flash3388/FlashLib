package examples.util;

import edu.flash3388.flashlib.util.Log;

/*
 * An example for using the flashlib log.
 */
public class ExampleLog {

	public static void main(String[] args){
		/*
		 * All logs will be saved under a "logs" directory in "/home/tomtzook".
		 */
		Log.setParentDirectory("/home/tomtzook/");
		
		/*
		 * Creating a new log named "test" which immediately writes data into the log file. The log
		 * overrides any existing files with the same name.
		 */
		Log log = Log.createStreamLog("test log");
		
		/*
		 * Sets the logging mode to Write and Print. Meaning data will be printed to the out stream
		 * and written to the log files 
		 */
		log.setLoggingMode(Log.MODE_PRINT | Log.MODE_WRITE);
		
		log.log("HELLO!! 1"); // Use this for standard log data
		log.logTime("IMPORTANT HELLO!! 1"); // Use this to record the time of the data log
		log.reportError("ERROR!! 1"); // Use to log errors
		log.reportWarning("WARNING!! 1"); // Use to log warnings
		
		/*
		 *  Sets the logging mode to Write. Meaning data will be written to the log files only.
		 */
		log.setLoggingMode(Log.MODE_WRITE);
		
		log.log("HELLO!! 2"); // Use this for standard log data
		log.logTime("IMPORTANT HELLO!! 2"); // Use this to record the time of the data log
		log.reportError("ERROR!! 2"); // Use to log errors
		log.reportWarning("WARNING!! 2"); // Use to log warnings
		
		/*
		 *  Sets the logging mode to Write. Meaning data will be printed to the out stream only.
		 */
		log.setLoggingMode(Log.MODE_PRINT);
		
		log.log("HELLO!! 3"); // Use this for standard log data
		log.logTime("IMPORTANT HELLO!! 3"); // Use this to record the time of the data log
		log.reportError("ERROR!! 3"); // Use to log errors
		log.reportWarning("WARNING!! 3"); // Use to log warnings
		
		/*
		 * Saves and closes the log. After that, the log is no longer available for use
		 */
		log.close();
	}
}
