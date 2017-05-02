package edu.flash3388.flashlib.robot.rio;

import edu.flash3388.flashlib.robot.hid.DPad;
import edu.wpi.first.wpilibj.DriverStation;

public class RioDpad extends DPad{

	private int stick;
	
	public RioDpad(int stick) {
		super(stick);
		this.stick = stick;
	}

	@Override
	public int get() {
		return DriverStation.getInstance().getStickPOV(stick, 0);
	}
	
	@Override
	public void refresh(){
		int degrees = get();
		Up.set(degrees);
		Down.set(degrees);
		Left.set(degrees);
		Right.set(degrees);
		POV.set(degrees);
	}
}
