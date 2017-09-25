package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.PIDSource;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.DoubleSource;
import edu.flash3388.flashlib.vision.Vision;

public class PIDVisionRotateActionPart extends PIDRotationActionPart implements VisionAction{

	public PIDVisionRotateActionPart(Vision source, DoubleProperty kp, DoubleProperty ki, DoubleProperty kd, 
			DoubleProperty kf, boolean horizontal,
			DoubleSource rotationThreshold, double rotationMargin){
		super(new PIDSource.VisionPIDSource(source, horizontal, false), kp, ki, kd, kf, rotationThreshold, rotationMargin);
	}
	public PIDVisionRotateActionPart(Vision source, DoubleProperty kp, DoubleProperty ki, DoubleProperty kd, 
			DoubleProperty kf, boolean horizontal,
			DoubleSource rotationThreshold){
		this(source, kp, ki, kd, kf, horizontal, rotationThreshold, 15.0);
	}
	public PIDVisionRotateActionPart(Vision source, DoubleProperty kp, DoubleProperty ki, DoubleProperty kd, 
			DoubleProperty kf,
			DoubleSource rotationThreshold){
		this(source, kp, ki, kd, kf, true, rotationThreshold);
	}
	public PIDVisionRotateActionPart(Vision source, double kp, double ki, double kd, double kf, boolean horizontal,
			DoubleSource rotationThreshold, double rotationMargin){
		super(new PIDSource.VisionPIDSource(source, horizontal, false), kp, ki, kd, kf, rotationThreshold, rotationMargin);
	}
	public PIDVisionRotateActionPart(Vision source, double kp, double ki, double kd, double kf, boolean horizontal,
			DoubleSource rotationThreshold){
		this(source, kp, ki, kd, kf, horizontal, rotationThreshold, 15.0);
	}
	public PIDVisionRotateActionPart(Vision source, double kp, double ki, double kd, double kf,
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
		PIDSource source = getPIDController().getPIDSource();
		if(source instanceof PIDSource.VisionPIDSource)
			((PIDSource.VisionPIDSource)source).setVision(vision);
	}
	@Override
	public Vision getVision() {
		PIDSource source = getPIDController().getPIDSource();
		if(source instanceof PIDSource.VisionPIDSource)
			return ((PIDSource.VisionPIDSource)source).getVision();
		return null;
	}
}
