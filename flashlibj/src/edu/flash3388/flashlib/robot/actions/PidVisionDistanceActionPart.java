package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.PidSource;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.DoubleSource;
import edu.flash3388.flashlib.vision.Vision;

public class PidVisionDistanceActionPart extends PidDistanceActionPart implements VisionAction{
	
	public PidVisionDistanceActionPart(Vision source, DoubleProperty kp, DoubleProperty ki, DoubleProperty kd, 
			DoubleProperty kf,
			DoubleSource distanceThreshold, double distanceMargin){
		super(new PidSource.VisionPidSource(source, false, true), kp, ki, kd, kf, distanceThreshold, distanceMargin);
	}
	public PidVisionDistanceActionPart(Vision source, DoubleProperty kp, DoubleProperty ki, DoubleProperty kd, 
			DoubleProperty kf, DoubleSource distanceThreshold){
		this(source, kp, ki, kd, kf, distanceThreshold, 15.0);
	}
	public PidVisionDistanceActionPart(Vision source, double kp, double ki, double kd, double kf,
			DoubleSource distanceThreshold, double distanceMargin){
		super(new PidSource.VisionPidSource(source, false, true), kp, ki, kd, kf, distanceThreshold, distanceMargin);
	}
	public PidVisionDistanceActionPart(Vision source, double kp, double ki, double kd, double kf, DoubleSource distanceThreshold){
		this(source, kp, ki, kd, kf, distanceThreshold, 15.0);
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
