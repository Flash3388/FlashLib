package edu.flash3388.flashlib.robot.frc;

import edu.flash3388.flashlib.util.LogListener;
import edu.wpi.first.wpilibj.DriverStation;

public class DriverStationLogListener implements LogListener{
	
	@Override
	public void log(String log, String caller) {
	}
	@Override
	public void logTime(String log, String caller, double time) {
	}
	@Override
	public void reportError(String err, double time) {
		DriverStation.reportError(err, false);
	}
	@Override
	public void reportWarning(String war, double time) {
		DriverStation.reportWarning(war, false);
	}
}
