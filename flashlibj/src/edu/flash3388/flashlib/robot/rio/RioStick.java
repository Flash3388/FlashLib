package edu.flash3388.flashlib.robot.rio;

import edu.flash3388.flashlib.robot.hid.Stick;
import edu.wpi.first.wpilibj.DriverStation;

public class RioStick extends Stick{

	private int stick, axisX, axisY;
	
	public RioStick(int stick, int axisX, int axisY){
		this.axisX = axisX;
		this.stick = stick;
		this.axisY = axisY;
	}
	
	@Override
	public double getX() {
		return DriverStation.getInstance().getStickAxis(stick, axisX);
	}
	@Override
	public double getY() {
		return DriverStation.getInstance().getStickAxis(stick, axisY);
	}
}
