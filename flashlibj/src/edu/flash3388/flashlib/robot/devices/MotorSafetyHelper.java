package edu.flash3388.flashlib.robot.devices;

import edu.flash3388.flashlib.util.FlashUtil;

public class MotorSafetyHelper {

	private static MotorSafetyHelper headMotor;
	
	private int expiration;
	private int stopTime;
	private boolean enabled, motorEnabled;
	private final SafeMotor safeMotor;
	private final MotorSafetyHelper nextMotor;
	
	public MotorSafetyHelper(SafeMotor motor){
		safeMotor = motor;
		nextMotor = headMotor;
		headMotor = this;
		
		enabled = false;
		stopTime = FlashUtil.millisInt();
		expiration = SafeMotor.DEFAULT_EXPIRATION;
	}
	
	public void feed(){
		stopTime = FlashUtil.millisInt() + expiration;
	}
	
	public void setExpiration(int expiration){
		this.expiration = expiration;
	}
	public int getExpiration(){
		return expiration;
	}
	
	public void setSafetyEnabled(boolean enable){
		this.enabled = enable;
	}
	public boolean isSafetyEnabled(){
		return enabled;
	}
	
	public boolean isAlive(){
		return !enabled || stopTime > FlashUtil.millis();
	}
	
	public void check(){
		if(!enabled)
			return;
		if (stopTime <= FlashUtil.millisInt()) {
			safeMotor.disable();
		}
	}
	
	public boolean isMotorEnabled(){
		return motorEnabled;
	}
	public void disableMotor(){
		safeMotor.disable();
		motorEnabled = false;
	}
	public void enableMotor(){
		motorEnabled = true;
	}
	
	public static void checkAll(){
		for(MotorSafetyHelper helper = headMotor; helper != null; helper = helper.nextMotor)
			helper.check();
	}
	public static void disableAll(){
		for(MotorSafetyHelper helper = headMotor; helper != null; helper = helper.nextMotor)
			helper.disableMotor();
	}
	public static void enableAll(){
		for(MotorSafetyHelper helper = headMotor; helper != null; helper = helper.nextMotor)
			helper.enableMotor();
	}
}
