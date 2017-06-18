package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.robot.devices.DoubleDataSource;
import edu.flash3388.flashlib.util.FlashUtil;

/**
 * Represents a label for double values on the Flashboard.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class DoubleProperty extends Sendable{
	
	private static final double CHAGNE_DIFFERENCE = 0.1;
	
	private double lastValue = 0.0, value = 0.0;
	private boolean changed = false;
	private DoubleDataSource src;
	private byte[] bytes = new byte[8];
	
	public DoubleProperty(String name, DoubleDataSource data) {
		super(name, FlashboardSendableType.DOUBLE);
		src = data;
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
		if(src == null) return false;
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
