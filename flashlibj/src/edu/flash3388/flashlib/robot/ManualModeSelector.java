package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.util.beans.IntegerProperty;

public class ManualModeSelector implements ModeSelector, IntegerProperty{

	private int currentMode = MODE_DISABLED;
	
	public void setMode(int mode){
		this.currentMode = mode;
	}
	@Override
	public int getMode() {
		return currentMode;
	}
	
	
	@Override
	public int get() {
		return getMode();
	}
	@Override
	public void set(int i) {
		setMode(i);
	}
	
	@Override
	public void setValue(Integer o) {
		setMode(o == null? MODE_DISABLED : o.intValue());
	}
	@Override
	public Integer getValue() {
		return getMode();
	}
}
