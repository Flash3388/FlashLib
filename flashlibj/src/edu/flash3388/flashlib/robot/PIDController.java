package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.math.Mathd;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class PIDController {
	
	public static class PIDTask implements Runnable{
		PIDController controller;
		boolean stop = false;
		
		public PIDTask(PIDController controller){
			this.controller = controller;
		}
		
		@Override
		public void run(){
			while(!stop){
				controller.run();
				FlashUtil.delay(controller.period);
			}
		}
	}
	
	public static final double DEFAULT_PERIOD = 0.05;
	
	private PIDSource source;
	private PIDOutput output;
	private double setPoint;
	private double minimumInput = -1, maximumInput = 1;
	private double minimumOutput = -1, maximumOutput = 1;
	private double period;
	private double kp, ki, kd;
	private double totalError, error, preError;
	private boolean enabled;
	
	public PIDController(double kp, double ki, double kd, double setPoint, double period, PIDSource source, PIDOutput output){
		this.kp = kp;
		this.ki = ki;
		this.kd = kd;
		this.setPoint = setPoint;
		this.source = source;
		this.output = output;
	}
	public PIDController(double kp, double ki, double kd, double setPoint, double period){
		this(kp, ki, kd, setPoint, period, null, null);
	}
	public PIDController(double kp, double ki, double kd, double setPoint){
		this(kp, ki, kd, setPoint, DEFAULT_PERIOD, null, null);
	}
	public PIDController(double kp, double ki, double kd){
		this(kp, ki, kd, 0);
	}
	public PIDController(double kp, double kd){
		this(kp, 0, kd, 0);
	}
	public PIDController(double kp){
		this(kp, 0, 0, 0);
	}
	
	public void run(){
		if(!enabled) return;
		if(output != null)
			output.pidWrite(calculate());
	}
	public double calculate(){
		if(!enabled) return 0;
		if(source == null)
			throw new IllegalStateException("PID Source is missing!");
		
		double currentVal = source.pidGet();
		double result = 0;
		error = setPoint - currentVal;
		
		if(source.getPIDSourceType().equals(PIDSourceType.kRate)){
			double pGain = (totalError + error) * kp;
			if(Mathd.limited(pGain, minimumOutput, maximumOutput))
				totalError += error;
			else if(pGain < maximumOutput)
				totalError = minimumOutput / kp;
			else 
				totalError = maximumOutput / kp;
			
			result = kp * totalError + kd * error;
		}else{//DISPLACEMENT!
			double iGain = (totalError + error) * ki;
			if(Mathd.limited(iGain, minimumOutput, maximumOutput))
				totalError += error;
			else if(iGain < maximumOutput)
				totalError = minimumOutput / ki;
			else 
				totalError = maximumOutput / ki;
			
			result = kp * error + ki * totalError + kd * (error - preError);
		}
		
		preError = error;
		result = Mathd.limit(result, minimumOutput, maximumOutput);
		return result;
	}
	
	public double getMaximumInput(){
		return maximumInput;
	}
	public double getMinimumInput(){
		return minimumInput;
	}
	public double getMaximumOutput(){
		return maximumOutput;
	}
	public double getMinimumOutput(){
		return minimumOutput;
	}
	public double getPeriod(){
		return period;
	}
	public double getP(){
		return kp;
	}
	public double getI(){
		return ki;
	}
	public double getInegralTime(){
		return kp / ki;
	}
	public double getD(){
		return kd;
	}
	public double getDerivativeTime(){
		return kd / kp;
	}
	public double getSetPoint(){
		return setPoint;
	}
	public PIDSource getSource(){
		return source;
	}
	public PIDOutput getOutput(){
		return output;
	}
	
	public void setMaximumInput(double m){
		this.maximumInput = m;
	}
	public void setMinimumInput(double m){
		this.minimumInput = m;
	}
	public void setInputLimit(double l){
		this.maximumInput = l;
		this.minimumInput = -l;
	}
	public void setMaximumOutput(double m){
		this.maximumOutput = m;
		this.setPoint = Mathd.limit(setPoint, minimumOutput, maximumOutput);
	}
	public void setMinimumOutput(double m){
		this.minimumOutput = m;
		this.setPoint = Mathd.limit(setPoint, minimumOutput, maximumOutput);
	}
	public void setOutputLimit(double l){
		this.maximumOutput = l;
		this.minimumOutput = -l;
		this.setPoint = Mathd.limit(setPoint, minimumOutput, maximumOutput);
	}
	public void setPeriod(double seconds){
		this.period = seconds;
	}
	public void setPeriod(long millis){
		this.period = millis / 1000.0;
	}
	public void setP(double p){
		this.kp = p;
	}
	public void setI(double i){
		this.ki = i;
	}
	public void setInegralTime(double t){
		this.ki = kp / t;
	}
	public void setD(double d){
		this.kd = d;
	}
	public void setDerivativeTime(double t){
		this.kd = t * kp;
	}
	public void setPID(double p, double i, double d){
		this.kp = p;
		this.ki = i;
		this.kd = d;
	}
	public void setSetPoint(double setpoint){
		this.setPoint = Mathd.limit(setpoint, minimumOutput, maximumOutput);
	}
	public void setPIDSource(PIDSource source){
		this.source = source;
	}
	public void setPIDOutput(PIDOutput output){
		this.output = output;
	}
	
	public boolean isEnabled(){
		return enabled;
	}
	public void setEnabled(boolean enable){
		this.enabled = enable;
	}
}
