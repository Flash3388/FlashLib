package edu.flash3388.flashlib.cams;

public class ManualViewSelector implements CameraViewSelector{

	private int cameraIndex = 0;
	
	@Override
	public void select(int index){
		if(index < 0)
			throw new IllegalArgumentException("Camera index cannot be negative");
		cameraIndex = index;
	}
	@Override
	public int getCameraIndex() {
		return cameraIndex;
	}
	@Override
	public void newCam(Camera cam) {}
	@Override
	public void remCam(Camera cam) {}
}
