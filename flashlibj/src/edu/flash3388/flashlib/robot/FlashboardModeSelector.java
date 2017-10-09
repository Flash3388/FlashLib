package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.flashboard.FlashboardModeSelectorControl;

public class FlashboardModeSelector implements ModeSelector{

	private FlashboardModeSelectorControl modeselector = FlashboardModeSelectorControl.getInstance();
	
	public void attachToFlashboard(){
		if(!modeselector.attached() && Flashboard.flashboardInit())
			Flashboard.attach(modeselector);
	}
	
	@Override
	public int getMode() {
		if(!modeselector.remoteAttached() || modeselector.isDisabled())
			return MODE_DISABLED;
		return modeselector.getCurrentState();
	}
}
