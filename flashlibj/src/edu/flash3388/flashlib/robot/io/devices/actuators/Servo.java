package edu.flash3388.flashlib.robot.io.devices.actuators;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.robot.io.PWM;

public class Servo extends SafePWMMotor implements PositionController {

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
        if(value < 0.0) {
            value = Math.abs(value);
        }

        value = Mathf.constrain(value, mMinAngle, mMaxAngle);

        setPosition((value - mMinAngle) / getAngleRange());
	}
	
	@Override
	public double get(){
		return getPosition() * getAngleRange() + mMinAngle;
	}
	
	@Override
	public void stop(){
		disable();
	}
	
	public double getAngleRange(){
		return mMaxAngle - mMinAngle;
	}
}
