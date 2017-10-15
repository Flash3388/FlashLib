package edu.flash3388.flashlib.robot;

@FunctionalInterface
public interface ModeSelector {
	
	public static final int MODE_DISABLED = 0;
	
	int getMode();
}
