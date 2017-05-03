package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.robot.hid.DPad;

public class SbcDpad extends DPad{

	private int stick;
	
	public SbcDpad(int stick) {
		super(stick);
		this.stick = stick;
	}

	@Override
	public int get() {
		return SbcBot.getControlStation().getStickPOV(stick);
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
