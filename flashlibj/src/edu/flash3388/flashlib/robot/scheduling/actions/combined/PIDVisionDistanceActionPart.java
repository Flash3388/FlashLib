package edu.flash3388.flashlib.robot.scheduling.actions.combined;

import edu.flash3388.flashlib.robot.control.PIDSource;
import edu.flash3388.flashlib.robot.scheduling.VisionAction;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.DoubleSource;
import edu.flash3388.flashlib.vision.Vision;

public class PIDVisionDistanceActionPart extends PIDDistanceActionPart implements VisionAction {
	
	private int visionTimeout, timeSinceNoAnalysis;
	
	public PIDVisionDistanceActionPart(Vision source, DoubleProperty kp, DoubleProperty ki, DoubleProperty kd, 
			DoubleProperty kf,
			DoubleSource distanceThreshold, double distanceMargin){
		super(new PIDSource.VisionPIDSource(source, false, true), kp, ki, kd, kf, distanceThreshold, distanceMargin);
	}
	public PIDVisionDistanceActionPart(Vision source, DoubleProperty kp, DoubleProperty ki, DoubleProperty kd, 
			DoubleProperty kf, DoubleSource distanceThreshold){
		this(source, kp, ki, kd, kf, distanceThreshold, 15.0);
	}
	public PIDVisionDistanceActionPart(Vision source, double kp, double ki, double kd, double kf,
			DoubleSource distanceThreshold, double distanceMargin){
		super(new PIDSource.VisionPIDSource(source, false, true), kp, ki, kd, kf, distanceThreshold, distanceMargin);
	}
	public PIDVisionDistanceActionPart(Vision source, double kp, double ki, double kd, double kf, DoubleSource distanceThreshold){
		this(source, kp, ki, kd, kf, distanceThreshold, 15.0);
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		timeSinceNoAnalysis = 0;
	}
	@Override
	public void execute() {
		if(!getVision().hasNewAnalysis()){
			set(0.0);
			
			if(timeSinceNoAnalysis < 1)
				timeSinceNoAnalysis = FlashUtil.millisInt();
		}else {
			if(timeSinceNoAnalysis >= 1)
				timeSinceNoAnalysis = 0;
			
			super.execute();
		}
	}
	@Override
	protected boolean isFinished() {
		return super.isFinished() || 
				(timeSinceNoAnalysis >= 1 && FlashUtil.millisInt() >= visionTimeout);
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
	
	public void setVisionTimeout(int ms){
		visionTimeout = ms;
	}
	public int getVisionTimeout(){
		return visionTimeout;
	}
}
