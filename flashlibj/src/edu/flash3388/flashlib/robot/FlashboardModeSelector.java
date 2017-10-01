package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.flashboard.DashboardModeSelector;
import edu.flash3388.flashlib.flashboard.Flashboard;

public class FlashboardModeSelector extends DashboardModeSelector implements ModeSelector{

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
