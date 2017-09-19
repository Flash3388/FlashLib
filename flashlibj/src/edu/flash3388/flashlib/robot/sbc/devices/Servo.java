package edu.flash3388.flashlib.robot.sbc.devices;

import edu.flash3388.flashlib.hal.PWM;
import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;

public class Servo extends SafePWM implements FlashSpeedController{

	private double minAngle, maxAngle;
	
	public Servo(PWM port, double minAngle, double maxAngle) {
		super(port);
		
		this.maxAngle = maxAngle;
		this.minAngle = minAngle;
		
		setBounds(2.4, 0, 0, 0, 0.6);
		setFrequency(getFrequency() * 0.25f);
	}
	public Servo(PWM port){
		this(port, 0.0, 180.0);
	}
	public Servo(int port, double minAngle, double maxAngle) {
		this(new PWM(port));
	}
	public Servo(int port){
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
		
		angle = Mathf.constrain(angle, minAngle, maxAngle);
		
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
