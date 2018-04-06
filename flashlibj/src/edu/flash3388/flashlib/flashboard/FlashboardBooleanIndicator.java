package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.communications.SendableException;
import edu.flash3388.flashlib.util.beans.BooleanSource;

public class FlashboardBooleanIndicator extends FlashboardControl{

	private BooleanSource source;
	private boolean value = false, lastValue = false;
	private boolean forceUpdate = false;
	private byte[] send = new byte[1];

	public FlashboardBooleanIndicator(String name, BooleanSource source) {
		super(name, FlashboardSendableType.BOOL_INDICATOR);
		this.source = source;
	}

	@Override
	public void newData(byte[] data) throws SendableException {
	}
	@Override
	public byte[] dataForTransmission() throws SendableException {
		lastValue = value;
		forceUpdate = false;
		send[0] = (byte) (value? 1: 0);
		return send;
	}
	@Override
	public boolean hasChanged() {
		value = source.get();
		return forceUpdate || lastValue != value;
	}
	@Override
	public void onConnection() {
		forceUpdate = true;
	}
	@Override
	public void onConnectionLost() {
	}
}
