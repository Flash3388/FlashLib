package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.util.beans.BooleanProperty;

public class FlashboardCheckbox extends FlashboardControl{

	private BooleanProperty prop;
	private boolean updateValue = false;
	private boolean value = false, lastValue = false;
	private byte[] send = new byte[1];
	
	public FlashboardCheckbox(String name, BooleanProperty prop) {
		super(name, FlashboardSendableType.CHECKBOX);
		this.prop = prop;
	}

	@Override
	public void newData(byte[] data) {
		value = data[0] == 1;
		lastValue = value;
		prop.set(value);
	}

	@Override
	public byte[] dataForTransmition() {
		send[0] = (byte) (value? 1 : 0);
		lastValue = value;
		updateValue = false;
		return send;
	}
	@Override
	public boolean hasChanged() {
		value = prop.get();
		return updateValue || lastValue != value;
	}

	@Override
	public void onConnection() {
		updateValue = true;
	}
	@Override
	public void onConnectionLost() {
	}
}
