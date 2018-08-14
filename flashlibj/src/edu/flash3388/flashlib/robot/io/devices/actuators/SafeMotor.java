package edu.flash3388.flashlib.robot.io.devices.actuators;

/**
 * Interface for motors and actuators using FlashLib's safety feature, 
 * allowing for disabling them if not used. This class is used together with the
 * {@link MotorSafetyHelper} class.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public interface SafeMotor {
	/**
	 * Default expiration timeout for motor usage.
	 */
	static final int DEFAULT_EXPIRATION = 100;
	
	/**
	 * Disables the actuator, stopping its operation to insure safety.
	 */
	public void disable();
	
	/**
	 * Sets the expiration timeout for motor usage. When this timeout was reached 
	 * since the last time the motor was used, it is disabled by calling {@link #disable()}.
	 * 
	 * @param timeout timeout in milliseconds.
	 */
	void setExpiration(int timeout);
	/**
	 * Gets the expiration timeout for motor usage. When this timeout was reached 
	 * since the last time the motor was used, it is disabled by calling {@link #disable()}.
	 * 
	 * @return timeout in milliseconds.
	 */
    int getExpiration();
    
    
	/**
	 * Sets the expiration timeout for motor usage. When this timeout was reached 
	 * since the last time the motor was used, it is disabled by calling {@link #disable()}.
	 * <p>
	 * Default implementation calls {@link #setExpiration(int)} and converts the time to milliseconds.
	 * 
	 * @param timeout timeout in seconds.
	 */
    default void setExpiration(double timeout){
    	setExpiration((int)(timeout * 1000));
    }
	/**
	 * Gets the expiration timeout for motor usage. When this timeout was reached 
	 * since the last time the motor was used, it is disabled by calling {@link #disable()}.
	 * <p>
	 * Default implementation calls {@link #getExpirationSeconds()} and converts the time to seconds.
	 * 
	 * @return timeout in seconds.
	 */
    default double getExpirationSeconds(){
    	return getExpiration() * 0.001;
    }
    

    /**
     * Gets whether or not this actuator is active. Returns true when safety is disabled
     * or if the expiration timeout was not reached.
     * 
     * @return true if active, false otherwise
     */
	boolean isAlive();

	/**
	 * Sets whether or not the safety feature is enabled or disabled. 
	 * <p>
	 * If enabled, this actuator will be checked for activation periodically. If the set timeout for activation
	 * has expired, {@link #disable()} is called.
	 * 
	 * @param enabled true to enable, false otherwise
	 */
	void setSafetyEnabled(boolean enabled);
	/**
	 * Gets whether or not the safety feature is enabled or disabled. 
	 * <p>
	 * If enabled, this actuator will be checked for activation periodically. If the set timeout for activation
	 * has expired, {@link #disable()} is called.
	 * 
	 * @return true if enabled, false otherwise
	 */
	boolean isSafetyEnabled();
}
