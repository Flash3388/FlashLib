package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.math.Mathd;
import io.silverspoon.bulldog.core.pin.Pin;

public class Servo extends SbcPwm{

	private double minAngle, maxAngle;
	
	public Servo(Pin port, double minAngle, double maxAngle) {
		super(port);
		
		this.maxAngle = maxAngle;
		this.minAngle = minAngle;
		
		setBounds(2.4, 0, 0, 0, 0.6);
		setFrequency(getFrequency() / 4);
	}
	public Servo(Pin port){
		this(port, 0, 180);
	}

	public void set(double value){
		setPosition(value);
	}
	public double get(){
		return getPosition();
	}
	
	public void setAngle(double angle){
		angle = Mathd.limit(angle, 0, 180);
		
		setPosition((angle - minAngle) / getAngleRange());
	}
	public double getAngle(){
		return getPosition() * getAngleRange() + minAngle;
	}
	
	public double getAngleRange(){
		return maxAngle - minAngle;
	}
}
