package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.communications.SendableException;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class FlashboardDirectionIndicator extends FlashboardControl{

	private DoubleSource source;
	private double value = 0.0, lastValue = 0.0;
	private boolean forceUpdate = false;
	
	public FlashboardDirectionIndicator(String name, DoubleSource source) {
		super(name, FlashboardSendableType.DIR_INDICATOR);
		this.source = source;
	}

	@Override
	public void newData(byte[] data) throws SendableException {
	}

	@Override
	public byte[] dataForTransmission() throws SendableException {
		lastValue = value;
		forceUpdate = false;
		return FlashUtil.toByteArray(value);
	}
	@Override
	public boolean hasChanged() {
		value = source.get();
		return forceUpdate || Math.abs(value - lastValue) > 0.01;
	}

	@Override
	public void onConnection() {
		forceUpdate = true;
	}
	@Override
	public void onConnectionLost() {
	}
}
