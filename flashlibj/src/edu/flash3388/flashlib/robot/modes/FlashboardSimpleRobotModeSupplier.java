package edu.flash3388.flashlib.robot.modes;

import edu.flash3388.flashlib.flashboard.FlashboardChooser;
import edu.flash3388.flashlib.flashboard.Flashboard;

public class FlashboardSimpleRobotModeSupplier implements RobotModeSupplier {

	private FlashboardChooser<Integer> chooser;
	
	public FlashboardSimpleRobotModeSupplier() {
		chooser = new FlashboardChooser<Integer>("Mode Selector");
		chooser.addDefault("Disabled", MODE_DISABLED);
	}
	
	public FlashboardSimpleRobotModeSupplier addOption(String name, int mode){
		chooser.addOption(name, mode);
		return this;
	}
	
	public void attachToFlashboard(){
		if(!chooser.isCommunicationAttached() && Flashboard.flashboardInit())
			Flashboard.attach(chooser);
	}
	
	@Override
	public int getMode() {
		if(!chooser.isRemoteAttached())
			return MODE_DISABLED;
		Integer mode = chooser.getSelected();
		return mode != null? mode : MODE_DISABLED;
	}
}
