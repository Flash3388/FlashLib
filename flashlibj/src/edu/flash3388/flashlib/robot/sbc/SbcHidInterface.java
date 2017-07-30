package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.robot.FlashRoboUtil;
import edu.flash3388.flashlib.robot.HIDInterface;
import edu.flash3388.flashlib.robot.RobotState;

public class SbcHidInterface implements HIDInterface{

	@Override
	public boolean isHIDConnected(int hid) {
		return SbcBot.getControlStation().isStickConnected(hid);
	}
	@Override
	public boolean isAxisConnected(int hid, int axis) {
		return true;
	}
	@Override
	public boolean isPOVConnected(int hid, int pov) {
		return isHIDConnected(hid) && pov == 0;
	}
	@Override
	public boolean isButtonConnected(int hid, int button) {
		return SbcBot.getControlStation().getButtonsCount(hid) >= button;
	}

	@Override
	public double getHIDAxis(int hid, int axis) {
		if(FlashRoboUtil.inEmergencyStop() || RobotState.isRobotDisabled())
			return 0;
		return SbcBot.getControlStation().getStickAxis(hid, axis);
	}
	@Override
	public boolean getHIDButton(int hid, int button) {
		if(FlashRoboUtil.inEmergencyStop() || RobotState.isRobotDisabled())
			return false;
		return SbcBot.getControlStation().getStickButton(hid, (byte)button);
	}
	@Override
	public int getHIDPOV(int hid, int pov) {
		if(FlashRoboUtil.inEmergencyStop() || RobotState.isRobotDisabled())
			return -1;
		return SbcBot.getControlStation().getStickPOV(hid);
	}
}
