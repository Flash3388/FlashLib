package edu.flash3388.flashlib.robot.frc;

import edu.flash3388.flashlib.robot.FlashRobotUtil;
import edu.flash3388.flashlib.robot.HIDInterface;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotState;

public class FRCHIDInterface implements HIDInterface{

	private DriverStation ds = DriverStation.getInstance();
	
	@Override
	public boolean isHIDConnected(int hid) {
		return ds.getStickAxisCount(hid) > 0;
	}

	@Override
	public boolean isAxisConnected(int hid, int axis) {
		return ds.getStickAxisCount(hid) > axis;
	}

	@Override
	public boolean isPOVConnected(int hid, int pov) {
		return ds.getStickPOVCount(hid) > pov;
	}

	@Override
	public boolean isButtonConnected(int hid, int button) {
		return ds.getStickButtonCount(hid) >= button;
	}

	@Override
	public double getHIDAxis(int hid, int axis) {
		if(FlashRobotUtil.inEmergencyStop() || RobotState.isDisabled())
			return 0;
		return ds.getStickAxis(hid, axis);
	}
	
	@Override
	public boolean getHIDButton(int hid, int button) {
		if(FlashRobotUtil.inEmergencyStop() || RobotState.isDisabled())
			return false;
		return ds.getStickButton(hid, (byte)button);
	}

	@Override
	public int getHIDPOV(int hid, int pov) {
		if(FlashRobotUtil.inEmergencyStop() || RobotState.isDisabled())
			return -1;
		return ds.getStickPOV(hid, pov);
	}

}
