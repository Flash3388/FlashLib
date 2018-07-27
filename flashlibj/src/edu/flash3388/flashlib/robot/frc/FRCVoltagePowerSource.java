package edu.flash3388.flashlib.robot.frc;

import edu.wpi.first.wpilibj.DriverStation;

public class FRCVoltagePowerSource extends PowerSource{
	
	public FRCVoltagePowerSource(double min, double max) {
		super("Voltage", min, max);
	}

	@Override
	public double get() {
		return  DriverStation.getInstance().getBatteryVoltage();
	}
}
