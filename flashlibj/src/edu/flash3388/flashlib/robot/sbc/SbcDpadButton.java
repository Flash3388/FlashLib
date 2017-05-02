package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.robot.hid.POVButton;

public class SbcDpadButton extends POVButton{

	public SbcDpadButton(String name, int stick, Type t) {
		super(name, stick, t);
	}
	public SbcDpadButton(int stick, Type t) {
		this("", stick, t);
	}

	@Override
	public void refresh(){
		set(SbcBot.getControlStation().getStickPOV(getJoystick()));
	}
}
