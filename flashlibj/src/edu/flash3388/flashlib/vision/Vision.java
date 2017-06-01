package edu.flash3388.flashlib.vision;

public interface Vision {
	boolean hasNewAnalysis();
	Analysis getAnalysis();
	boolean isRunning();
	void start();
	void stop();
	void setProcessing(VisionProcessing proc);
	VisionProcessing getProcessing();
	
	void setCameraOffsetAngle(double angle);
	double getCameraOffsetAngle();
	void setTargetHeight(double h);
	double getTargetHeight();
	void setTargetWidth(double w);
	double getTargetWidth();
}
