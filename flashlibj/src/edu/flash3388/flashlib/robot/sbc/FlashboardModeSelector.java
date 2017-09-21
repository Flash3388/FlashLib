package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.flashboard.DashboardChooser;
import edu.flash3388.flashlib.flashboard.Flashboard;

public class FlashboardModeSelector implements ModeSelector{

	private DashboardChooser<Integer> chooser;
	
	public FlashboardModeSelector() {
		chooser = new DashboardChooser<Integer>("Mode Selector");
		chooser.addDefault("Disabled", MODE_DISABLED);
	}
	
	public FlashboardModeSelector addOption(String name, int mode){
		chooser.addOption(name, mode);
		return this;
	}
	
	public void attachToFlashboard(){
		if(!chooser.attached())
			Flashboard.attach(chooser);
	}
	
	@Override
	public int getMode() {
		return chooser.getSelected();
	}
}
