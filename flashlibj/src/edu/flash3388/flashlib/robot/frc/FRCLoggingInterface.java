package edu.flash3388.flashlib.robot.frc;

import edu.flash3388.flashlib.util.LoggingInterface;
import edu.wpi.first.wpilibj.DriverStation;

public class FRCLoggingInterface implements LoggingInterface{
	
	@Override
	public void log(String log) {
	}
	@Override
	public void reportError(String err) {
		DriverStation.reportError(err, false);
	}
	@Override
	public void reportWarning(String war) {
		DriverStation.reportWarning(war, false);
	}
}
