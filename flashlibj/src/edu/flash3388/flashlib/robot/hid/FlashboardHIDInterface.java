package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.flashboard.FlashboardHIDControl;
import edu.flash3388.flashlib.robot.FlashRobotUtil;

public class FlashboardHIDInterface implements HIDInterface, Runnable {

	private FlashboardHIDControl hidcontrol = FlashboardHIDControl.getInstance();

	public void attachToFlashboard(){
		if(!hidcontrol.isCommunicationAttached() && Flashboard.flashboardInit())
			Flashboard.attach(hidcontrol);
	}
	
	@Override
	public boolean isHidConnected(int hid) {
		return hidcontrol.getAxisCount(hid) > 0 && hidcontrol.getButtonCount(hid) > 0;
	}

	@Override
	public boolean isAxisConnected(int hid, int axis) {
		return hidcontrol.getAxisCount(hid) > axis;
	}

	@Override
	public boolean isPovConnected(int hid, int pov) {
		return hidcontrol.getPOVCount(hid) > pov;
	}

	@Override
	public boolean isButtonConnected(int hid, int button) {
		return hidcontrol.getButtonCount(hid) >= button;
	}

	@Override
	public double getHidAxis(int hid, int axis) {
		if(FlashRobotUtil.inEmergencyStop())
			return 0.0;
		return hidcontrol.getAxis(hid, axis);
	}

	@Override
	public boolean getHidButton(int hid, int button) {
		if(FlashRobotUtil.inEmergencyStop())
			return false;
		return hidcontrol.getButton(hid, button);
	}

	@Override
	public int getHidPov(int hid, int pov) {
		if(FlashRobotUtil.inEmergencyStop())
			return -1;
		return hidcontrol.getPOV(hid, pov);
	}

	@Override
	public void run() {
		hidcontrol.run();
	}
}
