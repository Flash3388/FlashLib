package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.systems.ModableMotor;
import edu.flash3388.flashlib.vision.Vision;

public interface VisionAction {
	public static final long LOSS_TIMEOUT = 200;
	public static final int ACCURACY_MARGIN = 10;
	public static final long ACTION_VALIDATION_TIMEOUT = 50;
	public static final double DEFAULT_MIN_SPEED = 0.15;
	public static final double DEFAULT_MAX_SPEED = 0.8;
	
	void start();
	void cancel();
	boolean isRunning();
	
	void setMotorModeSource(ModableMotor modable);
	boolean targetInView();
	Vision getVision();
	void setVision(Vision vision);
	double getSpeed();
	void setSpeed(double sp);
	void setMinSpeed(double min);
	double getMinSpeed();
	void setMaxSpeed(double max);
	double getMaxSpeed();
	long getLossTimeout();
	void setLossTimeout(long millis);
}
