package edu.flash3388.flashlib.robot;

/**
 * Implements an empty {@link Robot}. Does nothing, will indicate the robot is always in disabled mode.
 * 
 * @author TomTzook
 * @since FlashLib 1.0.2
 */
public class EmptyRobot implements Robot{
	@Override
	public boolean isDisabled() {
		return true;
	}
	@Override
	public boolean isOperatorControl() {
		return false;
	}
	@Override
	public boolean isFRC() {
		return false;
	}
}
