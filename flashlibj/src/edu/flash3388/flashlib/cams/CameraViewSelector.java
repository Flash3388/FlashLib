package edu.flash3388.flashlib.cams;

public interface CameraViewSelector {
	void newCam(Camera cam);
	void remCam(Camera cam);
	void select(int index);
	int getCameraIndex();
}
