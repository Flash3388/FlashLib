package edu.flash3388.flashlib.robot.rio;

import edu.flash3388.flashlib.robot.hid.POVButton;
import edu.wpi.first.wpilibj.DriverStation;

public class RioDpadButton extends POVButton{

	public RioDpadButton(String name, int stick, Type t) {
		super(name, stick, t);
	}
	public RioDpadButton(int stick, Type t) {
		this("", stick, t);
	}

	@Override
	public void refresh(){
		set(DriverStation.getInstance().getStickPOV(getJoystick(), 0));
	}
}
