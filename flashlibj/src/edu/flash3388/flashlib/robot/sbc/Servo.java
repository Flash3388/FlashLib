package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;
import io.silverspoon.bulldog.core.pin.Pin;

public class Servo extends SbcPwm implements FlashSpeedController{

	private double minAngle, maxAngle;
	
	public Servo(Pin port, double minAngle, double maxAngle) {
		super(port);
		
		this.maxAngle = maxAngle;
		this.minAngle = minAngle;
		
		setBounds(2.4, 0, 0, 0, 0.6);
		setFrequency(getFrequency() * 0.25);
	}
	public Servo(Pin port){
		this(port, 0.0, 180.0);
	}

	@Override
	public void set(double value){
		setPosition(value);
	}
	@Override
	public void set(double speed, int direction) {
		set(speed);
	}
	@Override
	public void set(double speed, boolean direction) {
		set(speed);
	}
	
	@Override
	public double get(){
		return getPosition();
	}
	
	@Override
	public void stop(){
		set(0);
	}
	
	public void setAngle(double angle){
		if(angle < 0.0)
			angle = Math.abs(angle);
		
		angle = Mathf.limit(angle, 0.0, 180.0);
		
		setPosition((angle - minAngle) / getAngleRange());
	}
	public double getAngle(){
		return getPosition() * getAngleRange() + minAngle;
	}
	
	public double getAngleRange(){
		return maxAngle - minAngle;
	}
	
	@Override
	public boolean isInverted() {return false;}
	@Override
	public void setInverted(boolean inverted) {}
}
