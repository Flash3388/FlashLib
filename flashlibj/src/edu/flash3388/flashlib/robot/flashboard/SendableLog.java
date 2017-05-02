package edu.flash3388.flashlib.robot.flashboard;

import java.util.Vector;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.LoggingInterface;

public class SendableLog extends Sendable{
	
	private Vector<String> logs = new Vector<String>();
	private Vector<String> sentLogs = new Vector<String>();
	private boolean justConnected = false;
	private int sentIndex = 0;
	
	public SendableLog() {
		super(FlashboardSendableType.LOG);
		FlashUtil.getLog().addLoggingInterface(new LoggingInterface(){
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
		String str = "";
		
		if(sentIndex >= sentLogs.size() || sentLogs.isEmpty()) justConnected = false;
		if(justConnected)
			str = sentLogs.elementAt(sentIndex++);
		else if(!logs.isEmpty()){
			String log = logs.elementAt(0);
			logs.removeElementAt(0);
			sentLogs.addElement(log);
			str = log;
		}
		
		return str.getBytes();
	}
	@Override
	public boolean hasChanged() {
		return !logs.isEmpty() || (justConnected && !sentLogs.isEmpty());
	}
	@Override
	public void onConnection() {
		justConnected = true;
		sentIndex = 0;
	}
	@Override
	public void onConnectionLost() {
		justConnected = false;
	}

}
