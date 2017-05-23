package edu.flash3388.flashlib.robot.sbc;

public abstract class IterativeFrcBot extends IterativeSbc{

	public static final byte STATE_AUTONOMOUS = 0x01;
	public static final byte STATE_TELEOP = 0x02;
	
	@Override
	protected void stateInit(byte state) {
		switch (state) {
			case STATE_AUTONOMOUS:
				autonomousInit();
				break;
			case STATE_TELEOP:
				teleopInit();
				break;
		}
	}
	@Override
	protected void statePeriodic(byte state) {
		switch (state) {
			case STATE_AUTONOMOUS:
				autonomousPeriodic();
				break;
			case STATE_TELEOP:
				teleopPeriodic();
				break;
		}
	}

	protected abstract void robotInit();
	protected abstract void robotShutdown();

	protected abstract void disabledInit();
	protected abstract void disabledPeriodic();
	protected abstract void teleopInit();
	protected abstract void teleopPeriodic();
	protected abstract void autonomousInit();
	protected abstract void autonomousPeriodic();
}
