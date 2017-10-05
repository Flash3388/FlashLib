package edu.flash3388.flashlib.robot.devices;

import edu.flash3388.flashlib.math.Mathf;

public class PWMDevice implements IOPort{

	private PWM port;
	private double center, min, max, deadbandMin, deadbandMax;
	private boolean enableDeadband = false;
	
	public PWMDevice(PWM port) {
		if(port == null)
			throw new NullPointerException("PWM port is null");
		this.port = port;
	}
	
	private double getCenter(){
		return center;
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
	private double getFullScaleFactor(){
		return getMaxPositive() - getMinNegative();
	}
	
	protected void setBounds(double max, double deadbandMax, double center, double deadbandMin, double min){
		double looptime = 1000.0 / port.getFrequency();
		this.max = max / looptime;
		this.min = min / looptime;
		this.center = center / looptime;
		this.deadbandMin = deadbandMin / looptime;
		this.deadbandMax = deadbandMax / looptime;
	}
	protected void setFrequency(double frequency){
		port.setFrequency(frequency);
	}
	protected double getFrequency(){
		return port.getFrequency();
	}
	protected void setDeadbandEnabled(boolean enable) {
		enableDeadband = enable;
	}
	protected boolean isDeadbandEnabled(){
		return enableDeadband;
	}
	
	public void setDutyCycle(double duty){
		port.setDuty(duty);
	}
	public double getDutyCycle(){
		return port.getDuty();
	}
	public void disable(){
		setDutyCycle(0.0);
	}
	
	public void setSpeed(double speed) {
		speed = Mathf.constrain(speed, -1.0, 1.0);
		if(speed == 0.0)
			setDutyCycle(getCenter());
		else if(speed > 0.0)
			setDutyCycle(getMinPositive() + speed * getPositiveScaleFactor());
		else
			setDutyCycle(getMaxNegative() + speed * getNegativeScaleFactor());
	}
	public void setPosition(double pos){
		if(pos < 0.0)
			pos = Math.abs(pos);
		pos = Mathf.constrain(pos, 0.0, 1.0);
		
		setDutyCycle(getMinNegative() + pos * getFullScaleFactor());
	}
	
	public double getSpeed() {
		double duty = getDutyCycle();
		
		if(duty == 0.0)
			return 0.0;
		if(duty > getMaxPositive())
			return 1.0;
		if(duty < getMinNegative())
			return -1.0;
		if(duty > getMinPositive())
			return (duty - getMinPositive()) / getPositiveScaleFactor();
		if(duty < getMaxNegative())
			return (duty - getMaxNegative()) / getNegativeScaleFactor();
		return 0.0;
	}
	public double getPosition(){
		double duty = getDutyCycle();
		
		if(duty > getMaxPositive())
			return 1.0;
		if(duty < getMinNegative())
			return -1.0;
		
		return (duty - getMinNegative()) / getFullScaleFactor();
	}
	
	@Override
	public void free() {
		if(port != null)
			port.free();
		port = null;
	}
}
