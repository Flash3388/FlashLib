package edu.flash3388.flashlib.cams;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.flashboard.FlashboardChooser;

/**
 * Represents a {@link CameraViewSelector} on the Flashboard. Uses a {@link FlashboardChooser}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class FlashboardCamViewSelector implements CameraViewSelector{

	private FlashboardChooser<Integer> chooser;
	private int cameras = 0;
	
	public FlashboardCamViewSelector(){
		chooser = new FlashboardChooser<Integer>("Cam View Selector");
	}
	public FlashboardChooser<Integer> getChooser(){
		return chooser;
	}
	
	public void attachToFlashboard(){
		if(!chooser.isCommunicationAttached() && Flashboard.flashboardInit())
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
