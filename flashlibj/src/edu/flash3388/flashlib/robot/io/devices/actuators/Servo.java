package edu.flash3388.flashlib.robot.io.devices.actuators;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.robot.io.PWM;
import edu.flash3388.flashlib.robot.io.devices.PWMBounds;
import edu.flash3388.flashlib.robot.io.devices.SafePWM;

public class Servo extends SafePWM implements FlashSpeedController{

	private double mMinAngle;
	private double mMaxAngle;
	
	public Servo(PWM port, double minAngle, double maxAngle) {
		super(port, new PWMBounds(2.4, 0, 0, 0, 0.6, false));

		mMaxAngle = maxAngle;
		mMinAngle = minAngle;

		setFrequency(getFrequency() * 0.25f);
	}

	public Servo(PWM port){
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
		if(angle < 0.0) {
			angle = Math.abs(angle);
		}
		
		angle = Mathf.constrain(angle, mMinAngle, mMaxAngle);
		
		setPosition((angle - mMinAngle) / getAngleRange());
	}

	public double getAngle(){
		return getPosition() * getAngleRange() + mMinAngle;
	}
	
	public double getAngleRange(){
		return mMaxAngle - mMinAngle;
	}
	
	@Override
	public boolean isInverted() {return false;}

	@Override
	public void setInverted(boolean inverted) {}
}
