package edu.flash3388.flashlib.robot.modes;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.flashboard.FlashboardModeSelectorControl;

public class FlashboardModeSelector implements ModeSelector{

	private FlashboardModeSelectorControl modeselector = FlashboardModeSelectorControl.getInstance();
	
	public void attachToFlashboard(){
		if(!modeselector.isCommunicationAttached() && Flashboard.flashboardInit())
			Flashboard.attach(modeselector);
	}
	
	@Override
	public int getMode() {
		if(!modeselector.isRemoteAttached() || modeselector.isDisabled())
			return MODE_DISABLED;
		return modeselector.getCurrentState();
	}
}
