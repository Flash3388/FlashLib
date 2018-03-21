package edu.flash3388.flashlib.flashboard;

import java.util.Vector;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Sends log data to a remote source.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class FlashboardRemoteLogger extends FlashboardControl {
	
	private static class LoggerHandler extends Handler {

		private FlashboardRemoteLogger logger;
		
		LoggerHandler(FlashboardRemoteLogger logger) {
			this.logger = logger;
		}
		
		@Override
		public void publish(LogRecord record) {
			String log = String.format("<%s> [%s] (%s:%s): %s", 
					record.getLevel(), record.getMillis(),
					record.getSourceClassName(), record.getSourceMethodName(),
					record.getMessage());
			
			logger.feed(log);
		}
		
		@Override
		public void flush() {}
		@Override
		public void close() throws SecurityException {}
	}
	
	private Vector<String> logs = new Vector<String>();
	private Vector<String> sentLogs = new Vector<String>();
	private Handler handler = new LoggerHandler(this);
	
	public FlashboardRemoteLogger(Logger logger) {
		super(logger.getName(), FlashboardSendableType.LOG);
		logger.addHandler(handler);
	}
	
	private void feed(String log){
		logs.addElement(log);
	}
	
	@Override
	public void newData(byte[] data) {
	}
	@Override
	public byte[] dataForTransmition() {
		String str = logs.elementAt(0);
		logs.remove(0);
		sentLogs.addElement(str);
		return str.getBytes();
	}
	@Override
	public boolean hasChanged() {
		return !logs.isEmpty();
	}
	@Override
	public void onConnection() {
		int logsize = logs.size();
		for (int i = 0; i < sentLogs.size(); i++){
			if(i < logsize)
				logs.setElementAt(sentLogs.get(i), i);
			else 
				logs.addElement(sentLogs.get(i));
		}
		sentLogs.clear();
	}
	@Override
	public void onConnectionLost() {
	}

}
