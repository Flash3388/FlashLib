package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.math.Mathd;
import io.silverspoon.bulldog.core.pin.Pin;
import io.silverspoon.bulldog.core.pwm.Pwm;

public class SbcPwm{

	private Pwm port;
	private double center, min, max, deadbandMin, deadbandMax;
	private boolean enableDeadband = false;
	
	public SbcPwm(Pin port){
		this.port = port.as(Pwm.class);
		if(this.port == null)
			throw new IllegalArgumentException("Given port does not support Pulse-Width Modulation");
		this.port.enable();
	}
	
	private double getPositiveScaleFactor(){
		return getMaxPositive() - getMinPositive();
	}
	private double getMaxPositive(){
		return max;
	}
	private double getMinPositive(){
		return enableDeadband? deadbandMax : center + 0.01;
	}
	private double getNegativeScaleFactor(){
		return getMaxNegative() - getMinNegative();
	}
	private double getMaxNegative(){
		return enableDeadband? deadbandMin : center - 0.01;
	}
	private double getMinNegative(){
		return min;
	}
	
	protected void setBounds(double max, double deadbandMax, double center, double deadbandMin, double min){
		double looptime = 1 / port.getFrequency();
		this.max = max / looptime;
		this.min = min / looptime;
		this.center = center / looptime;
		this.deadbandMin = deadbandMin / looptime;
		this.deadbandMax = deadbandMax / looptime;
	}
	protected void setFrequency(double freq){
		port.setFrequency(freq);
	}
	protected void setDeadbandEnabled(boolean enable) {
		enableDeadband = enable;
	}
	protected boolean isDeadbandEnabled(){
		return enableDeadband;
	}
	
	public void setDutyCycle(double raw){
		this.port.setDuty(raw);
	}
	public double getDutyCycle(){
		return port.getDuty();
	}
	public void disable(){
		setDutyCycle(0);
	}
	
	public void setSpeed(double speed) {
		speed = Mathd.limit(speed, -1, 1);
		if(speed == 0)
			setDutyCycle(0);
		else if(speed > 0)
			setDutyCycle(getMinPositive() + speed * getPositiveScaleFactor());
		else
			setDutyCycle(getMaxNegative() + speed * getNegativeScaleFactor());
	}
	public double getSpeed() {
		double duty = getDutyCycle();
		
		if(duty == 0)
			return 0;
		if(duty > getMaxPositive())
			return 1;
		if(duty < getMinNegative())
			return -1;
		if(duty > getMinPositive())
			return (duty - getMinPositive()) / getPositiveScaleFactor();
		if(duty < getMaxNegative())
			return (duty - getMaxNegative()) / getNegativeScaleFactor();
		return 0;
	}
}
