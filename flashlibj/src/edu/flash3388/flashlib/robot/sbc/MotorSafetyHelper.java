package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.robot.RobotState;
import edu.flash3388.flashlib.util.FlashUtil;

public class MotorSafetyHelper {

	private static MotorSafetyHelper headMotor;
	
	private int expiration;
	private int stopTime;
	private boolean enabled;
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
		if(!enabled || RobotState.isRobotDisabled())
			return;
		if (stopTime <= FlashUtil.millisInt()) {
			safeMotor.stop();
		}
	}
	
	public static void checkAll(){
		for(MotorSafetyHelper helper = headMotor; helper != null; helper = helper.nextMotor)
			helper.check();
	}
}
