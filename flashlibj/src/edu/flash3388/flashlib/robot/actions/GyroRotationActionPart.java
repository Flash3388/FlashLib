package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.robot.PidSource;
import edu.flash3388.flashlib.robot.devices.Gyro;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class GyroRotationActionPart extends PidRotationActionPart{

	private Gyro gyro;
	private DoubleSource rotationThreshold;
	private boolean relative = false;
	private double initialAngle;
	
	public GyroRotationActionPart(Gyro gyro, double kp, double ki, double kd, DoubleSource rotationThreshold,
			double rotationMargin) {
		super(new PidSource.DoubleDataPidSource(null), kp, ki, kd, ()->0, rotationMargin);
		((PidSource.DoubleDataPidSource)getPidController().getSource()).setSource(()->getValue());
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
