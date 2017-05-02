package edu.flash3388.flashlib.robot.rio;

import edu.flash3388.flashlib.robot.hid.Button;
import edu.wpi.first.wpilibj.DriverStation;

public class RioButton extends Button{

	public RioButton(int stick, int number) {
		super(stick, number);
	}
	public RioButton(String name, int stick, int number) {
		super(name, stick, number);
	}

	@Override
	public void refresh(){
		set(DriverStation.getInstance().getStickButton(getJoystick(), (byte)getNumber()));
	}
}
