package edu.flash3388.flashlib.flashboard;

import java.util.Vector;

import edu.flash3388.flashlib.util.Log;
import edu.flash3388.flashlib.util.LogListener;

/**
 * Sends log data to a remote source.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class FlashboardRemoteLog extends FlashboardControl{
	
	private Vector<String> logs = new Vector<String>();
	private Vector<String> sentLogs = new Vector<String>();
	
	public FlashboardRemoteLog(Log log) {
		super(log.getName(), FlashboardSendableType.LOG);
		if(!log.isLoggingMode(Log.MODE_INTERFACES))
			log.setLoggingMode(log.getLoggingMode() | Log.MODE_INTERFACES);
		log.addListener(new LogListener(){
			@Override
			public void log(String log, String caller) {
				feed("("+caller+") : "+log);
			}
			@Override
			public void logTime(String log, String caller, double time) {
				feed("("+caller+") : "+"["+time+"] "+log);
			}
			@Override
			public void reportError(String err, double time) {
				logTime(err, "ERROR", time);
			}
			@Override
			public void reportWarning(String war, double time) {
				logTime(war, "WARNING", time);
			}
		});
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
			else logs.addElement(sentLogs.get(i));
		}
		sentLogs.clear();
	}
	@Override
	public void onConnectionLost() {
	}

}
