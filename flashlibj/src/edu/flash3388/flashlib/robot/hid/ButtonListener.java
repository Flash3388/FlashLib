package edu.flash3388.flashlib.robot.hid;

public interface ButtonListener {
	void onPress(ButtonEvent e);
	void onHold(ButtonEvent e);
	void onRelease(ButtonEvent e);
}
