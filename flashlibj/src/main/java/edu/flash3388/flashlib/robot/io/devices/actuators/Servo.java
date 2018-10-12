package edu.flash3388.flashlib.robot.io.devices.actuators;

import edu.flash3388.flashlib.robot.io.PWM;

public class Servo extends PWMPositionController {

	private final double mMinAngle;
	private final double mMaxAngle;
	
	public Servo(PWM port, double minAngle, double maxAngle) {
		super(
		        port,
                new PWMBounds(2.4, 0, 0, 0, 0.6, false),
                port.getFrequency() * 0.25);

		mMaxAngle = maxAngle;
		mMinAngle = minAngle;
	}

	public Servo(PWM port){
		this(port, 0.0, 180.0);
	}

	public void setAngle(double angle){
        set((angle - mMinAngle) / getAngleRange());
	}

	public double getAngle(){
		return get() * getAngleRange() + mMinAngle;
	}
	
	public double getAngleRange(){
		return mMaxAngle - mMinAngle;
	}
}
