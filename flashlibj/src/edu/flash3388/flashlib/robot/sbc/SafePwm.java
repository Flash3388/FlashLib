package edu.flash3388.flashlib.robot.sbc;

import io.silverspoon.bulldog.core.pin.Pin;

public class SafePwm extends SbcPwm implements SafeMotor{

	private MotorSafetyHelper helper;
	
	public SafePwm(Pin port) {
		super(port);
		helper = new MotorSafetyHelper(this);
	}

	@Override
	public void setSpeed(double speed){
		if(helper.isMotorEnabled()){
			super.setSpeed(speed);
			helper.feed();
		}
	}
	@Override
	public void setPosition(double pos){
		if(helper.isMotorEnabled()){
			super.setPosition(pos);
			helper.feed();
		}
	}
	
	@Override
	public void setExpiration(int timeout) {
		helper.setExpiration(timeout);
	}
	@Override
	public int getExpiration() {
		return helper.getExpiration();
	}
	
	@Override
	public boolean isAlive() {
		return helper.isAlive();
	}

	@Override
	public void setSafetyEnabled(boolean enabled) {
		helper.setSafetyEnabled(enabled);
	}
	@Override
	public boolean isSafetyEnabled() {
		return helper.isSafetyEnabled();
	}
	
	public void feed(){
		helper.feed();
	}
}
