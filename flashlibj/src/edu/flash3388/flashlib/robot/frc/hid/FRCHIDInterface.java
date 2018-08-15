package edu.flash3388.flashlib.robot.frc.hid;

import edu.flash3388.flashlib.robot.hid.HIDInterface;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotState;

public class FRCHIDInterface implements HIDInterface{

	private DriverStation ds = DriverStation.getInstance();
	
	@Override
	public boolean isHidConnected(int hid) {
		return ds.getStickAxisCount(hid) > 0;
	}

	@Override
	public boolean isAxisConnected(int hid, int axis) {
		return ds.getStickAxisCount(hid) > axis;
	}

	@Override
	public boolean isPovConnected(int hid, int pov) {
		return ds.getStickPOVCount(hid) > pov;
	}

	@Override
	public boolean isButtonConnected(int hid, int button) {
		return ds.getStickButtonCount(hid) >= button;
	}

	@Override
	public double getHidAxis(int hid, int axis) {
		if(RobotState.isDisabled())
			return 0;
		return ds.getStickAxis(hid, axis);
	}
	
	@Override
	public boolean getHidButton(int hid, int button) {
		if(RobotState.isDisabled())
			return false;
		return ds.getStickButton(hid, (byte)button);
	}

	@Override
	public int getHidPov(int hid, int pov) {
		if(RobotState.isDisabled())
			return -1;
		return ds.getStickPOV(hid, pov);
	}

}