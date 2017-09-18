package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.PidSource;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.DoubleSource;
import edu.flash3388.flashlib.vision.Vision;

public class PidVisionRotateActionPart extends PidRotationActionPart implements VisionAction{

	public PidVisionRotateActionPart(Vision source, DoubleProperty kp, DoubleProperty ki, DoubleProperty kd, 
			DoubleProperty kf, boolean horizontal,
			DoubleSource rotationThreshold, double rotationMargin){
		super(new PidSource.VisionPidSource(source, horizontal, false), kp, ki, kd, kf, rotationThreshold, rotationMargin);
	}
	public PidVisionRotateActionPart(Vision source, DoubleProperty kp, DoubleProperty ki, DoubleProperty kd, 
			DoubleProperty kf, boolean horizontal,
			DoubleSource rotationThreshold){
		this(source, kp, ki, kd, kf, horizontal, rotationThreshold, 15.0);
	}
	public PidVisionRotateActionPart(Vision source, DoubleProperty kp, DoubleProperty ki, DoubleProperty kd, 
			DoubleProperty kf,
			DoubleSource rotationThreshold){
		this(source, kp, ki, kd, kf, true, rotationThreshold);
	}
	public PidVisionRotateActionPart(Vision source, double kp, double ki, double kd, double kf, boolean horizontal,
			DoubleSource rotationThreshold, double rotationMargin){
		super(new PidSource.VisionPidSource(source, horizontal, false), kp, ki, kd, kf, rotationThreshold, rotationMargin);
	}
	public PidVisionRotateActionPart(Vision source, double kp, double ki, double kd, double kf, boolean horizontal,
			DoubleSource rotationThreshold){
		this(source, kp, ki, kd, kf, horizontal, rotationThreshold, 15.0);
	}
	public PidVisionRotateActionPart(Vision source, double kp, double ki, double kd, double kf,
			DoubleSource rotationThreshold){
		this(source, kp, ki, kd, kf, true, rotationThreshold);
	}
	
	@Override
	public void execute() {
		if(!getVision().hasNewAnalysis()){
			set(0.0);
		}else super.execute();
	}
	
	@Override
	public void setVision(Vision vision) {
		PidSource source = getPIDController().getPIDSource();
		if(source instanceof PidSource.VisionPidSource)
			((PidSource.VisionPidSource)source).setVision(vision);
	}
	@Override
	public Vision getVision() {
		PidSource source = getPIDController().getPIDSource();
		if(source instanceof PidSource.VisionPidSource)
			return ((PidSource.VisionPidSource)source).getVision();
		return null;
	}
}
