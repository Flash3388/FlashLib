package edu.flash3388.flashlib.robot.frc.modes;

import edu.flash3388.flashlib.robot.modes.ModeSelector;
import edu.wpi.first.wpilibj.DriverStation;

public class FRCModeSelector implements ModeSelector{
	
	public static final int MODE_TELEOP = 1;
	public static final int MODE_AUTONOMOUS = 2;
	public static final int MODE_TEST = 3;
	
	private DriverStation ds = DriverStation.getInstance();
	
	@Override
	public int getMode() {
		if(ds.isDisabled())
			return MODE_DISABLED;
		if(ds.isOperatorControl())
			return MODE_TELEOP;
		if(ds.isAutonomous())
			return MODE_AUTONOMOUS;
		if(ds.isTest())
			return MODE_TEST;
		
		return MODE_DISABLED;
	}
}
