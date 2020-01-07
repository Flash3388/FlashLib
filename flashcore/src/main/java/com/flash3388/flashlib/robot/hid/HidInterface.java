package com.flash3388.flashlib.robot.hid;

public interface HidInterface {
	
	boolean isHidConnected(int hid);

	boolean isAxisConnected(int hid, int axis);

	boolean isPovConnected(int hid, int pov);

	boolean isButtonConnected(int hid, int button);
	
	double getHidAxis(int hid, int axis);

	boolean getHidButton(int hid, int button);

	int getHidPov(int hid, int pov);
}
