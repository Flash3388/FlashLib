package edu.flash3388.flashlib.robot.rio;

import edu.flash3388.flashlib.robot.hid.Triggers.Trigger;
import edu.wpi.first.wpilibj.DriverStation;

public class RioTrigger extends Trigger{

	public RioTrigger(int stick, int number) {
		super(stick, number);
	}

	@Override
	public void refresh(){
		setValue(DriverStation.getInstance().getStickAxis(getJoystick(), getNumber()));
		set(getValue() >= getSensitivity());
	}
}
