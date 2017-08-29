package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.util.beans.ValueSource;

/**
 * Represents a label for string values on the Flashboard.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class DashboardStringProperty extends Sendable{
	
	private String lastValue = "", value = "";
	private boolean changed = false;
	private ValueSource<String> src;
	
	public DashboardStringProperty(String name, ValueSource<String> data) {
		super(name, FlashboardSendableType.STRING);
		src = data;
	}

	public void set(ValueSource<String> src){
		lastValue = src.getValue();
		this.src = src;
	}
	public ValueSource<String> get(){
		return src;
	}
	
	@Override
	public void newData(byte[] data) {}
	@Override
	public byte[] dataForTransmition() {
		lastValue = value;
		changed = false;
		return lastValue.getBytes();
	}
	@Override
	public boolean hasChanged() {
		if(src == null) return false;
		value = src.getValue();
		return changed || !(value.equals(lastValue));
	}
	@Override
	public void onConnection() {
		changed = true;
	}
	@Override
	public void onConnectionLost() {
	}
}
