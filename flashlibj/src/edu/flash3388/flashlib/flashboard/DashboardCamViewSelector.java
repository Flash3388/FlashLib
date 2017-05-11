package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.cams.Camera;
import edu.flash3388.flashlib.cams.CameraViewSelector;

public class DashboardCamViewSelector implements CameraViewSelector{

	private DashboardChooser<Integer> chooser;
	private int cameras = 0;
	
	public DashboardCamViewSelector(){
		chooser = new DashboardChooser<Integer>("Cam View Selector");
		chooser.select(0);
		
		if(Flashboard.flashboardInit())
			Flashboard.attach(chooser);
	}
	public DashboardChooser<Integer> getChooser(){
		return chooser;
	}
	@Override
	public int getCameraIndex() {
		return chooser.getSelectedIndex();
	}
	@Override
	public void newCam(Camera cam) {
		chooser.addOption("Cam "+cameras, cameras);
		cameras++;
	}
	@Override
	public void remCam(Camera cam) {
		chooser.removeLast();
		cameras--;
	}
	@Override
	public void select(int index) {
		if(chooser.getSelectedIndex() != index)
			chooser.select(index);
	}
}
