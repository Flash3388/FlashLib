package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.PidController;
import edu.flash3388.flashlib.vision.Vision;

public class PidVisionRotationActionPart extends PidRotationActionPart{
	
	public PidVisionRotationActionPart(Vision vision, double kp, double ki, double kd, boolean horizontal,
			double rotationMargin, int passedTimeout){
		super(new PidController.VisionPidSource(vision, horizontal), kp, ki, kd, 0.0, rotationMargin, passedTimeout);
	}
	public PidVisionRotationActionPart(Vision vision, double kp, double ki, double kd, boolean horizontal){
		this(vision, kp, ki, kd, horizontal, VisionAction.ACCURACY_MARGIN, VisionAction.ACTION_VALIDATION_TIMEOUT);
	}
	public PidVisionRotationActionPart(Vision vision, double kp, double ki, double kd){
		this(vision, kp, ki, kd, true);
	}
}
