package edu.flash3388.flashlib.flashboard;

import java.util.Vector;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.util.Log;
import edu.flash3388.flashlib.util.LoggingInterface;

/**
 * Sends log data to a remote source.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class SendableLog extends Sendable{
	
	private Vector<String> logs = new Vector<String>();
	private Vector<String> sentLogs = new Vector<String>();
	
	public SendableLog(Log log) {
		super(log.getName(), FlashboardSendableType.LOG);
		byte mode = log.getLoggingMode();
		if((mode & Log.MODE_INTERFACES) == 0)
			log.setLoggingMode((byte) (mode | Log.MODE_INTERFACES));
		log.addLoggingInterface(new LoggingInterface(){
			@Override
			public void log(String log) {
				feed(log);
			}
			@Override
			public void reportError(String err) {}
			@Override
			public void reportWarning(String war) {}
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
