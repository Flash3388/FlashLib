package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.flashboard.DashboardModeSelector;

public class FlashboardModeSelector extends DashboardModeSelector implements ModeSelector{

	@Override
	public int getMode() {
		if(!remoteAttached() || isDisabled())
			return MODE_DISABLED;
		return getCurrentState();
	}
}
