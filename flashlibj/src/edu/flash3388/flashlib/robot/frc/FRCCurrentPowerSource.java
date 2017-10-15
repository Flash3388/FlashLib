package edu.flash3388.flashlib.robot.frc;

import edu.flash3388.flashlib.robot.PowerLogger.PowerSource;

public class FRCCurrentPowerSource extends PowerSource{

	private int channel;
	
	public FRCCurrentPowerSource(String name, int channel, double min, double max) {
		super(name, min, max);
		this.channel = channel;
	}

	@Override
	public double get() {
		return FlashFRCUtil.getPDP().getCurrent(channel);
	}
}
