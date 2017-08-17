package edu.flash3388.flashlib.robot.sbc;

public class ManualStateSelector implements StateSelector{

	private byte selectedState = STATE_DISABLED;
	
	public void setState(byte state){
		this.selectedState = state;
	}
	@Override
	public byte getState() {
		return selectedState;
	}
}
