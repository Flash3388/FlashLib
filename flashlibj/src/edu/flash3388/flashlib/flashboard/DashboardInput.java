package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.robot.devices.DoubleDataSource;
import edu.flash3388.flashlib.robot.devices.StringDataSource;
import edu.flash3388.flashlib.util.FlashUtil;

public class DashboardInput extends Sendable{
	
	private StringDataSource.VarDataSource value;
	
	public DashboardInput(String name) {
		super(name, FlashboardSendableType.INPUT);
		value = new StringDataSource.VarDataSource();
	}

	public boolean empty(){
		return value == null || value.get().equals("");
	}
	public int intValue(){
		return intValue(0);
	}
	public int intValue(int defaultVal){
		if(empty()) return defaultVal;
		return FlashUtil.toInt(value.get());
	}
	
	public double doubleValue(){
		return doubleValue(0);
	}
	public double doubleValue(double defaultVal){
		if(empty()) return defaultVal;
		return FlashUtil.toDouble(value.get());
	}
	public DoubleDataSource getDoubleSource(){
		return new DoubleDataSource() {
			@Override
			public double get() {
				return doubleValue();
			}
		};
	}
	
	public String stringValue(){
		return stringValue("");
	}
	public String stringValue(String defaultVal){
		if(empty()) return defaultVal;
		return value.get();
	}
	public StringDataSource getStringSource(){
		return new StringDataSource() {
			@Override
			public String get() {
				return stringValue();
			}
		};
	}
	
	@Override
	public void newData(byte[] data) {
		String newV = new String(data);
		synchronized (value) {
			value.set(newV);
		}
	}
	@Override
	public byte[] dataForTransmition() {
		return null;
	}
	@Override
	public boolean hasChanged() {
		return false;
	}
	@Override
	public void onConnection() {}
	@Override
	public void onConnectionLost() {}
}
