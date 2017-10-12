package edu.flash3388.flashlib.robot.devices;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * An helper class for FlashLib's motor safety feature. This class is defined by a {@link SafeMotor} object
 * and is responsible for handling safe operations. 
 * <p>
 * For the safety feature to work appropriately, it is necessary to call {@link #checkAll()} periodically, performing 
 * safety check for each motor. 
 * <p>
 * It is possible to disable or enable all {@link SafeMotor} objects by using {@link #enableAll()} or {@link #disableAll()}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class MotorSafetyHelper {

	private static MotorSafetyHelper headMotor;
	
	private int expiration;
	private int stopTime;
	private boolean enabled, motorEnabled;
	private final SafeMotor safeMotor;
	private final MotorSafetyHelper nextMotor;
	
	/**
	 * Creates a new safety helper object for a given {@link SafeMotor}.
	 * 
	 * @param motor the safe motor to monitor.
	 */
	public MotorSafetyHelper(SafeMotor motor){
		safeMotor = motor;
		nextMotor = headMotor;
		headMotor = this;
		
		enabled = false;
		stopTime = FlashUtil.millisInt();
		expiration = SafeMotor.DEFAULT_EXPIRATION;
		motorEnabled = true;
	}
	
	/**
	 * If safety is enabled, this indicates that the motor is still being used at this time,
	 * affectively reset the expiration point.
	 */
	public void feed(){
		if(enabled)
			stopTime = FlashUtil.millisInt() + expiration;
	}
	
	/**
	 * Sets the expiration timeout for motor usage. When this timeout was reached 
	 * since the last time the motor was used, it is disabled by calling {@link SafeMotor#disable()}.
	 * 
	 * @param expiration expiration in milliseconds.
	 */
	public void setExpiration(int expiration){
		this.expiration = expiration;
	}
	/**
	 * Gets the expiration timeout for motor usage. When this timeout was reached 
	 * since the last time the motor was used, it is disabled by calling {@link SafeMotor#disable()}.
	 * 
	 * @return expiration in milliseconds.
	 */
	public int getExpiration(){
		return expiration;
	}
	
	/**
	 * Sets whether or not the safety feature is enabled or disabled. 
	 * <p>
	 * If enabled, this actuator will be checked for activation periodically. If the set timeout for activation
	 * has expired, {@link SafeMotor#disable()} is called.
	 * 
	 * @param enabled true to enable, false otherwise
	 */
	public void setSafetyEnabled(boolean enabled){
		this.enabled = enabled;
	}
	/**
	 * Gets whether or not the safety feature is enabled or disabled. 
	 * <p>
	 * If enabled, this actuator will be checked for activation periodically. If the set timeout for activation
	 * has expired, {@link SafeMotor#disable()} is called.
	 * 
	 * @return true if enabled, false otherwise
	 */
	public boolean isSafetyEnabled(){
		return enabled;
	}
	
	/**
	 * Gets whether or not the {@link SafeMotor} object is alive. If the actuator is disabled,  
	 * this returns false. If safety is disabled, this returns true. If safety is enabled and time has not expired, 
	 * this will return true.
	 * 
	 * @return true if the actuator is enabled and safety is disabled or safety has not expired.
	 */
	public boolean isAlive(){
		return (motorEnabled && !enabled || stopTime > FlashUtil.millisInt());
	}
	
	/**
	 * Performs a safety check on the {@link SafeMotor} object associated with this
	 * helper. If safety is enabled and operation time has expired, {@link SafeMotor#disable()}
	 * is called.
	 */
	public void check(){
		if(!enabled)
			return;
		if (stopTime <= FlashUtil.millisInt()) {
			safeMotor.disable();
		}
	}
	
	/**
	 * Gets whether or no the actuator using this helper is enabled for usage.
	 * 
	 * @return true if enabled, false otherwise.
	 */
	public boolean isMotorEnabled(){
		return motorEnabled;
	}
	/**
	 * Disables the actuator object using this helper, effectively
	 * stopping its operation.
	 */
	public void disableMotor(){
		safeMotor.disable();
		motorEnabled = false;
	}
	/**
	 * Enables the actuator object using this helper, allowing
	 * it to be used.
	 */
	public void enableMotor(){
		motorEnabled = true;
	}
	
	/**
	 * Runs a check on all defines objects of this class, calling {@link #check()} of each object.
	 * <p>
	 * This method performs the operation of the safety feature. By calling this, actuators 
	 * are tested and it is insured that all operates safely.
	 */
	public static void checkAll(){
		for(MotorSafetyHelper helper = headMotor; helper != null; helper = helper.nextMotor)
			helper.check();
	}
	/**
	 * Disables the operation of all safe objects. Doing so will not allow those objects
	 * to be activated. This is done by calling {@link #disableMotor()} of each defined helper 
	 * object.
	 */
	public static void disableAll(){
		for(MotorSafetyHelper helper = headMotor; helper != null; helper = helper.nextMotor)
			helper.disableMotor();
	}
	/**
	 * Enables the operation of all safe objects. Doing so will allow those objects
	 * to be activated. This is done by calling {@link #enableMotor()} of each defined helper 
	 * object.
	 */
	public static void enableAll(){
		for(MotorSafetyHelper helper = headMotor; helper != null; helper = helper.nextMotor)
			helper.enableMotor();
	}
}
