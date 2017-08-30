package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.robot.FlashRobotUtil;
import edu.flash3388.flashlib.robot.HIDInterface;
import edu.flash3388.flashlib.robot.RobotFactory;

public class SBCHidInterface implements HIDInterface{

	@Override
	public boolean isHIDConnected(int hid) {
		return ControlStation.getInstance().isStickConnected(hid);
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
		return ControlStation.getInstance().getButtonsCount(hid) >= button;
	}

	@Override
	public double getHIDAxis(int hid, int axis) {
		if(FlashRobotUtil.inEmergencyStop() || RobotFactory.getImplementation().isDisabled())
			return 0;
		return ControlStation.getInstance().getStickAxis(hid, axis);
	}
	@Override
	public boolean getHIDButton(int hid, int button) {
		if(FlashRobotUtil.inEmergencyStop() || RobotFactory.getImplementation().isDisabled())
			return false;
		return ControlStation.getInstance().getStickButton(hid, (byte)button);
	}
	@Override
	public int getHIDPOV(int hid, int pov) {
		if(FlashRobotUtil.inEmergencyStop() || RobotFactory.getImplementation().isDisabled())
			return -1;
		return ControlStation.getInstance().getStickPOV(hid);
	}
}
