package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.PidSource;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.DoubleSource;
import edu.flash3388.flashlib.vision.Vision;

public class PidVisionDistanceActionPart extends PidDistanceActionPart implements VisionAction{
	
	public PidVisionDistanceActionPart(Vision source, double kp, double ki, double kd,
			DoubleSource rotationThreshold, double rotationMargin){
		super(new PidSource.VisionPidSource(source, false, true), kp, ki, kd, rotationThreshold, rotationMargin);
	}
	public PidVisionDistanceActionPart(Vision source, double kp, double ki, double kd){
		this(source, kp, ki, kd, ()->100.0, 15.0);
	}
	
	@Override
	public void execute() {
		super.execute();
		if(!getVision().hasNewAnalysis()){
			dataSource.set(0.0);
			FlashUtil.getLog().log("Vision analysis timedout");
		}
	}
	
	@Override
	public void setVision(Vision vision) {
		PidSource source = getPidController().getSource();
		if(source instanceof PidSource.VisionPidSource)
			((PidSource.VisionPidSource)source).setVision(vision);
	}
	@Override
	public Vision getVision() {
		PidSource source = getPidController().getSource();
		if(source instanceof PidSource.VisionPidSource)
			return ((PidSource.VisionPidSource)source).getVision();
		return null;
	}
}
