package edu.flash3388.flashlib.robot.hid;

public interface HID {
	double getRawAxis(int axis);
	
	boolean getRawButton(int button);
	Button getButton(int button);
	int getButtonCount();
	
	Stick getStick(int index);
	Stick getStick();
	
	DPad getPOV();
}
