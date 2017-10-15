package edu.flash3388.flashlib.robot.frc;

import edu.flash3388.flashlib.robot.PowerLogger.PowerSource;

public class FRCTotalCurrentPowerSource extends PowerSource{

	public FRCTotalCurrentPowerSource(double min, double max) {
		super("PDP Total Current", min, max);
	}

	@Override
	public double get() {
		return FlashFRCUtil.getPDP().getTotalCurrent();
	}
}
