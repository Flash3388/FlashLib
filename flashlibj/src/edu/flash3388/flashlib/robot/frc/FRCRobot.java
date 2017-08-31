package edu.flash3388.flashlib.robot.frc;

import edu.flash3388.flashlib.robot.HIDInterface;
import edu.flash3388.flashlib.robot.Robot;
import edu.flash3388.flashlib.robot.Scheduler;
import edu.wpi.first.wpilibj.DriverStation;

public class FRCRobot implements Robot{

	private Scheduler schedulerImpl = new Scheduler();
	private HIDInterface hidImpl = new FRCHidInterface();
	private DriverStation ds = DriverStation.getInstance();
	
	@Override
	public Scheduler getScheduler() {
		return schedulerImpl;
	}
	
	@Override
	public HIDInterface getHIDInterface() {
		return hidImpl;
	}

	@Override
	public boolean isDisabled() {
		return ds.isDisabled();
	}

	@Override
	public boolean isOperatorControl() {
		return ds.isOperatorControl();
	}
	
	@Override
	public boolean isFRC() {
		return true;
	}
}
