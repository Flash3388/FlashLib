package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.cams.Camera;
import edu.flash3388.flashlib.cams.CameraViewSelector;

/**
 * Represents a {@link CameraViewSelector} on the Flashboard. Uses a {@link DashboardChooser}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class DashboardCamViewSelector implements CameraViewSelector{

	private DashboardChooser<Integer> chooser;
	private int cameras = 0;
	
	public DashboardCamViewSelector(){
		chooser = new DashboardChooser<Integer>("Cam View Selector");
	}
	public DashboardChooser<Integer> getChooser(){
		return chooser;
	}
	
	public void attachToFlashboard(){
		if(!chooser.attached() && Flashboard.flashboardInit())
			Flashboard.attach(chooser);
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
