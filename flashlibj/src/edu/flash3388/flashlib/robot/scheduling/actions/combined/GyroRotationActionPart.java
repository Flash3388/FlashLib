package edu.flash3388.flashlib.robot.scheduling.actions.combined;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.robot.control.PIDSource;
import edu.flash3388.flashlib.robot.devices.Gyro;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class GyroRotationActionPart extends PIDRotationActionPart{

	private Gyro gyro;
	private DoubleSource rotationThreshold;
	private boolean relative = false;
	private double initialAngle;
	
	public GyroRotationActionPart(Gyro gyro,DoubleProperty kp, DoubleProperty ki, DoubleProperty kd, 
			DoubleProperty kf, DoubleSource rotationThreshold,
			double rotationMargin) {
		super(new PIDSource.DoubleSourcePIDSource(null), kp, ki, kd, kf, ()->0, rotationMargin);
		((PIDSource.DoubleSourcePIDSource)getPIDController().getPIDSource()).setSource(()->getValue());
		this.gyro = gyro;
		this.rotationThreshold = rotationThreshold;
	}
	public GyroRotationActionPart(Gyro gyro, double kp, double ki, double kd, double kf, DoubleSource rotationThreshold,
			double rotationMargin) {
		super(new PIDSource.DoubleSourcePIDSource(null), kp, ki, kd, kf, ()->0, rotationMargin);
		((PIDSource.DoubleSourcePIDSource)getPIDController().getPIDSource()).setSource(()->getValue());
		this.gyro = gyro;
		this.rotationThreshold = rotationThreshold;
	}

	private double getValue(){
		double val = Mathf.translateAngle(rotationThreshold.get());
		double curr = Mathf.translateAngle(gyro.getAngle() - initialAngle);
		
		double max = Math.max(curr, val), min = Math.min(curr, val);
		
		if(Math.abs(max - min) < Math.abs(360 - max + min))
			return max == curr? -Math.abs(max - min) : Math.abs(max - min);
		return max != curr? -Math.abs(360 - max + min) : Math.abs(360 - max + min);
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		
		initialAngle = relative? gyro.getAngle() : 0;
	}
	
	public void setRelativeAngle(boolean relative){
		this.relative = relative;
	}
}
