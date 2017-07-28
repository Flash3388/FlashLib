package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.util.beans.BooleanSource;

/**
 * Represents a label for boolean values on the Flashboard.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class DashboardBooleanProperty extends Sendable{
	
	private boolean lastValue = false, value = false, changed = false;
	private BooleanSource src;
	private byte[] bytes = new byte[1];
	
	public DashboardBooleanProperty(String name, BooleanSource data) {
		super(name, FlashboardSendableType.BOOLEAN);
		src = data;
	}

	public void set(BooleanSource src){
		lastValue = src.get();
		this.src = src;
	}
	public BooleanSource get(){
		return src;
	}
	
	@Override
	public void newData(byte[] data) {}
	@Override
	public byte[] dataForTransmition() {
		lastValue = value;
		bytes[0] = (byte) (lastValue? 1 : 0);
		changed = false;
		return bytes;
	}
	@Override
	public boolean hasChanged() {
		return src != null && (lastValue != (value = src.get()) || changed);
	}
	@Override
	public void onConnection() {
		changed = true;
	}
	@Override
	public void onConnectionLost() {
	}
}
