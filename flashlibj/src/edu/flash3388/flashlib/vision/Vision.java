package edu.flash3388.flashlib.vision;

public interface Vision {
	boolean hasNewAnalysis();
	Analysis getAnalysis();
	boolean isRunning();
	void start();
	void stop();
	void addProcessing(VisionProcessing proc);
	void selectProcessing(int index);
	int getProcessingCount();
	VisionProcessing getProcessing(int index);
	VisionProcessing getProcessing();
}
