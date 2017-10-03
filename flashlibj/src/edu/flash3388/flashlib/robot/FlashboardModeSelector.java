package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.flashboard.FlashboardModeSelectorControl;

public class FlashboardModeSelector extends FlashboardModeSelectorControl implements ModeSelector{

	public void attachToFlashboard(){
		if(Flashboard.flashboardInit())
			Flashboard.attach(this);
	}
	
	@Override
	public int getMode() {
		if(!remoteAttached() || isDisabled())
			return MODE_DISABLED;
		return getCurrentState();
	}
}
