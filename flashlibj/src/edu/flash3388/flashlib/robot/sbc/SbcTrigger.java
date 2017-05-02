package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.robot.hid.Triggers.Trigger;

public class SbcTrigger extends Trigger{

	public SbcTrigger(int stick, int number) {
		super(stick, number);
	}

	@Override
	public void refresh(){
		setValue(SbcBot.getControlStation().getStickAxis(getJoystick(), getNumber()));
		set(getValue() >= getSensitivity());
	}

}
