package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.PidSource;
import edu.flash3388.flashlib.robot.devices.DoubleDataSource;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.vision.Vision;

public class PidVisionRotateActionPart extends PidRotationActionPart implements VisionAction{
	
	public PidVisionRotateActionPart(Vision source, double kp, double ki, double kd, boolean horizontal,
			DoubleDataSource rotationThreshold, double rotationMargin){
		super(new PidSource.VisionPidSource(source, horizontal, false), kp, ki, kd, rotationThreshold, rotationMargin);
	}
	public PidVisionRotateActionPart(Vision source, double kp, double ki, double kd, boolean horizontal){
		this(source, kp, ki, kd, horizontal, ()->100.0, 15.0);
	}
	public PidVisionRotateActionPart(Vision source, double kp, double ki, double kd){
		this(source, kp, ki, kd, true);
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
