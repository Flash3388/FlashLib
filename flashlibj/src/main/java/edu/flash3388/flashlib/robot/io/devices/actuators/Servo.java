package edu.flash3388.flashlib.robot.io.devices.actuators;

import edu.flash3388.flashlib.robot.io.Pwm;

public class Servo extends PwmPositionController {

	private final double mMinAngle;
	private final double mMaxAngle;
	
	public Servo(Pwm port, double minAngle, double maxAngle) {
		super(
		        port,
                new PwmBounds(2.4, 0, 0, 0, 0.6, false),
                port.getFrequency() * 0.25);

		mMaxAngle = maxAngle;
		mMinAngle = minAngle;
	}

	public Servo(Pwm port){
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
