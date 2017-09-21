package edu.flash3388.flashlib.robot.devices;

public interface SafeMotor {
	public static final int DEFAULT_EXPIRATION = 100;
	
	public void disable();
	
	void setExpiration(int timeout);
    int getExpiration();

	boolean isAlive();

	void setSafetyEnabled(boolean enabled);
	boolean isSafetyEnabled();
}
