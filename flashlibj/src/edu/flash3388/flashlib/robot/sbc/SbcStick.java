package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.robot.hid.Stick;

public class SbcStick extends Stick{
	private int stick, axisX, axisY;
	
	public SbcStick(int stick, int axisX, int axisY){
		this.axisX = axisX;
		this.stick = stick;
		this.axisY = axisY;
	}
	
	@Override
	public double getX() {
		return SbcBot.getControlStation().getStickAxis(stick, axisX);
	}
	@Override
	public double getY() {
		return SbcBot.getControlStation().getStickAxis(stick, axisY);
	}
}
