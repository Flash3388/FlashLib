package edu.flash3388.flashlib.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class LogUtil {
	private LogUtil() {}
	
	private static String LOGS_PARENT_DIRECTORY = "";
	
	public static void setLogsParentDirectory(String directory) {
		if (directory.endsWith(File.separator))
			directory = directory.substring(0, directory.length() - 1);
		
		LOGS_PARENT_DIRECTORY = directory;
	}
	
	public static Logger getLogger(String name) throws SecurityException, IOException {
		Logger logger = Logger.getLogger(name);
		
		String directoryPath = getLoggerFileParentPath(name);
		Files.createDirectories(Paths.get(directoryPath));
		
		String filePattern = directoryPath + getLoggerFileNamePattern();
		
		logger.addHandler(new FileHandler(filePattern));
		
		return logger;
	}
	
	private static String getLoggerFileParentPath(String name) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
		
		return String.format("%s/logs/%s/%s/", LOGS_PARENT_DIRECTORY, 
				name, dateFormat.format(new Date()));
	}
	
	private static String getLoggerFileNamePattern() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("hh_mm_ss");
		
		return String.format("log_%s.%%g.log", dateFormat.format(new Date()));
	}
}
