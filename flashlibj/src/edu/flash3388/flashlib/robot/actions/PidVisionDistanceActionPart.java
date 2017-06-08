package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.PidController;
import edu.flash3388.flashlib.robot.PidSource;
import edu.flash3388.flashlib.robot.devices.DoubleDataSource;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.vision.Vision;

public class PidVisionDistanceActionPart extends PidDistanceActionPart implements VisionAction{
	
	public PidVisionDistanceActionPart(Vision source, double kp, double ki, double kd,
			DoubleDataSource rotationThreshold, double rotationMargin){
		super(new PidController.VisionPidSource(source, false, true), kp, ki, kd, rotationThreshold, rotationMargin);
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
		if(source instanceof PidController.VisionPidSource)
			((PidController.VisionPidSource)source).setVision(vision);
	}
	@Override
	public Vision getVision() {
		PidSource source = getPidController().getSource();
		if(source instanceof PidController.VisionPidSource)
			return ((PidController.VisionPidSource)source).getVision();
		return null;
	}
}
