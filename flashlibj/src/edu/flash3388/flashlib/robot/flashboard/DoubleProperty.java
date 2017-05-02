package edu.flash3388.flashlib.robot.flashboard;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.robot.devices.DoubleDataSource;
import edu.flash3388.flashlib.util.FlashUtil;

public class DoubleProperty extends Sendable{
	
	private static final double CHAGNE_DIFFERENCE = 0.1;
	double lastValue = 0.0, value = 0.0;
	boolean changed = false;
	DoubleDataSource src;
	byte[] bytes = new byte[8];
	
	public DoubleProperty(String name, DoubleDataSource data) {
		super(name, FlashboardSendableType.DOUBLE);
	}
	public DoubleProperty(String name){
		this(name, new DoubleDataSource.VarDataSource());
	}

	public void set(DoubleDataSource src){
		lastValue = src.get();
		this.src = src;
	}
	public DoubleDataSource get(){
		return src;
	}
	
	@Override
	public void newData(byte[] data) {}
	@Override
	public byte[] dataForTransmition() {
		lastValue = value;
		FlashUtil.fillByteArray(lastValue, bytes);
		changed = false;
		return bytes;
	}
	@Override
	public boolean hasChanged() {
		value = src.get();
		return changed || Math.abs(value - lastValue) > CHAGNE_DIFFERENCE;
	}
	@Override
	public void onConnection() {
		changed = true;
	}
	@Override
	public void onConnectionLost() {
	}
}
