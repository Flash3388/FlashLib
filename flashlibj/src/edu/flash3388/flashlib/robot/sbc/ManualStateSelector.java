package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.robot.sbc.SbcBot.SbcState;

public class ManualStateSelector implements StateSelector{

	private SbcState selectedState = SbcState.Disabled;
	
	public void setState(SbcState state){
		this.selectedState = state;
	}
	@Override
	public SbcState getState() {
		return selectedState;
	}
}
