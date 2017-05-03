package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.robot.hid.Button;

public class SbcButton extends Button{
	
	public SbcButton(int stick, int number) {
		super(stick, number);
	}
	public SbcButton(String name, int stick, int number) {
		super(name, stick, number);
	}

	@Override
	public void refresh(){
		set(SbcBot.getControlStation().getStickButton(getJoystick(), (byte)getNumber()));
	}
}
