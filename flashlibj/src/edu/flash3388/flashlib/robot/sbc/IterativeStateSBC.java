package edu.flash3388.flashlib.robot.sbc;

public abstract class IterativeStateSBC extends IterativeSBC{
	
	@Override
	protected void stateInit(byte state) {
		switch (state) {
			case StateSelector.STATE_AUTONOMOUS:
				autonomousInit();
				break;
			case StateSelector.STATE_TELEOP:
				teleopInit();
				break;
		}
	}
	@Override
	protected void statePeriodic(byte state) {
		switch (state) {
			case StateSelector.STATE_AUTONOMOUS:
				autonomousPeriodic();
				break;
			case StateSelector.STATE_TELEOP:
				teleopPeriodic();
				break;
		}
	}
	
	protected abstract void teleopInit();
	protected abstract void teleopPeriodic();
	protected abstract void autonomousInit();
	protected abstract void autonomousPeriodic();
}
