package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.util.beans.DoubleSource;

/**
 * Provides a PID controller for controlling motors more efficiently.
 * <p>
 * A proportional–integral–derivative controller (PID controller) is a control loop feedback 
 * mechanism (controller) commonly used in industrial control systems. A PID controller continuously 
 * calculates an error value e(t) as the difference between a desired setpoint 
 * and a measured process variable and applies a correction based on proportional, integral, and derivative 
 * terms (sometimes denoted P, I, and D respectively) which give their name to the controller type.
 * </p>
 * <p>
 * 	{@link PidSource} provides feedback data from the feedback sensor.
 * </p>
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see <a href="https://en.wikipedia.org/wiki/PID_controller">https://en.wikipedia.org/wiki/PID_controller</a>
 */
public class PidController {
	
	private PidSource source;
	private DoubleSource setPoint;
	private double minimumOutput = -1, maximumOutput = 1;
	private double kp, ki, kd, kf;
	private double lastVal, lastOut;
	private double maxIOutput = 0, setpointRange = 0, outRampRate = 0;
	private double totalError, error, maxError = 0;
	private boolean enabled = false, firstRun = true;
	
	/**
	 * Creates a new PID controller. Uses given constant for the control loop, a DataSource for the set point and a pid source
	 * for the feedback data.
	 * @param kp the proportional constant
	 * @param ki the integral constant
	 * @param kd the differential constant
	 * @param kf the feed forward constant
	 * @param setPoint the set point
	 * @param source the feedback source
	 */
	public PidController(double kp, double ki, double kd, double kf, DoubleSource setPoint, PidSource source){
		setPID(kp, ki, kd, kf);
		this.setPoint = setPoint;
		this.source = source;
	}
	/**
	 * Creates a new PID controller. Uses given constant for the control loop, a DataSource for the set point and without
	 * a pid source.
	 * @param kp the proportional constant
	 * @param ki the integral constant
	 * @param kd the differential constant
	 * @param kf the feed forward constant
	 * @param setPoint the set point
	 */
	public PidController(double kp, double ki, double kd, double kf, DoubleSource setPoint){
		this(kp, ki, kd, kf, setPoint, null);
	}
	/**
	 * Creates a new PID controller. Uses given constant for the control loop, without a set point and a pid source
	 * @param kp the proportional constant
	 * @param ki the integral constant
	 * @param kd the differential constant
	 * @param kf the feed forward constant
	 */
	public PidController(double kp, double ki, double kd, double kf){
		this(kp, ki, kd, kf, null);
	}
	
	/**
	 * Calculates the output to the system to compensate for the error. If the PID controller is not enabled,
	 * 0 is returned.
	 * 
	 * @return the compensation value from the PID loop calculation
	 * @throws IllegalStateException if the feedback source or the set point source is null
	 */
	public double calculate(){
		if(!enabled) return 0;
		if(source == null)
			throw new IllegalStateException("PID Source is missing!");
		if(setPoint == null)
			throw new IllegalStateException("PID SetPoint is missing!");
		
		double currentVal = source.pidGet();
		if(setpointRange != 0)
			currentVal = Mathf.constrain(currentVal, currentVal - setpointRange, currentVal + setpointRange);
		
		double result = 0;
		error = setPoint.get() - currentVal;
		
		double pOut = kp * error;
		double fOut = kf * setPoint.get();
		
		if(firstRun){
			firstRun = false;
			lastOut = pOut + fOut;
			lastVal = currentVal;
		}
		
		double iOut = ki * totalError;
		double dOut = -kd * (currentVal - lastVal);
		
		if(maxIOutput != 0)
			iOut = Mathf.constrain(iOut, -maxIOutput, maxIOutput);
		
		result = pOut + iOut + dOut + fOut;
		
		totalError += error;
		
		if(minimumOutput != maximumOutput && !Mathf.constrained(result, minimumOutput, maximumOutput))
			totalError = error;
		else if(maxIOutput != 0)
			totalError = Mathf.constrain(totalError + error, -maxError, maxError);
		
		if(outRampRate != 0 && !Mathf.constrained(result, lastOut - outRampRate, lastOut + outRampRate)){
			totalError = error;
			result = Mathf.constrain(result, lastOut - outRampRate, lastOut + outRampRate);
		}
		
		if(minimumOutput != maximumOutput)
			result = Mathf.constrain(result, minimumOutput, maximumOutput);
		
		lastOut = result;
		lastVal = currentVal;
		return result;
	}
	/**
	 * Resets the controller. This erases the I term buildup, and removes 
	 * D gain on the next loop.
	 */
	public void reset(){
		firstRun = true;
		totalError = 0;
	}

	/**
     * Set the maximum rate the output can increase per cycle.
     * 
	 * @param rate rate of output change per cycle
	 */
	public void setOutputRampRate(double rate){
		outRampRate = rate;
	}

	/** 
     * Set a limit on how far the setpoint can be from the current position.
     * 
     * @param range range of setpoint
	 */
	public void setSetpointRange(double range){
		setpointRange = range;
	}
	/**
	 * Gets the maximum output of the loop.
	 * @return the maximum output
	 */
	public double getMaximumOutput(){
		return maximumOutput;
	}
	/**
	 * Gets the minimum output of the loop.
	 * @return the minimum output
	 */
	public double getMinimumOutput(){
		return minimumOutput;
	}
	/**
	 * Gets the proportional constant of the loop.
	 * @return proportional constant
	 */
	public double getP(){
		return kp;
	}
	/**
	 * Gets the integral constant of the loop.
	 * @return integral constant
	 */
	public double getI(){
		return ki;
	}
	/**
	 * Gets the differential constant of the loop.
	 * @return differential constant
	 */
	public double getD(){
		return kd;
	}
	/**
	 * Gets the set point data source used by this loop.
	 * @return set point
	 */
	public DoubleSource getSetPoint(){
		return setPoint;
	}
	/**
	 * Gets the pid source used by this loop.
	 * @return pid source
	 */
	public PidSource getSource(){
		return source;
	}
	
	public void setMaxIOutput(double maximum){
		maxIOutput=maximum;
		if(ki != 0)
			maxError=maxIOutput / ki;
	}
	
	/**
	 * Sets the minimum and maximum outputs of the loop.
	 * @param min minimum output
	 * @param max maximum output
	 */
	public void setOutputLimit(double min, double max){
		if(max < min)
			throw new IllegalArgumentException("The min value cannot be bigger than the max");
		this.minimumOutput = min;
		this.maximumOutput = max;
		
		if(maxIOutput == 0 || maxIOutput > (max - min))
			setMaxIOutput(max - min);
	}
	/**
	 * Sets the output limit of the loop. The maximum output will be equal to the given value, while the minimum output
	 * will be equal to the negative value.
	 * @param l the output limit
	 */
	public void setOutputLimit(double l){
		this.maximumOutput = l;
		this.minimumOutput = -l;
	}
	/**
	 * Sets the value of the proportional constant
	 * @param p proportional constant
	 */
	public void setP(double p){
		this.kp = p;
	}
	/**
	 * Sets the value of the integral constant
	 * @param i integral constant
	 */
	public void setI(double i){
		if(ki != 0)
			totalError = totalError * ki/i;
		if(maxIOutput != 0)
			maxError = maxIOutput / i;
		
		this.ki = i;
	}
	/**
	 * Sets the value of the differential constant
	 * @param d differential constant
	 */
	public void setD(double d){
		this.kd = d;
	}
	/**
	 * Sets the feed forward gain value
	 * @param f feed forward gain
	 */
	public void setF(double f){
		this.kf = f;
	}
	/**
	 * Sets the PID constants: proportional, integral and differential used by this loop.
	 * @param p proportional constant
	 * @param i integral constant
	 * @param d differential constant
	 */
	public void setPID(double p, double i, double d){
		this.kp = p;
		setI(i);
		this.kd = d;
	}
	/**
	 * Sets the PID constants: proportional, integral and differential used by this loop.
	 * @param p proportional constant
	 * @param i integral constant
	 * @param d differential constant
	 * @param f feed forward gain
	 */
	public void setPID(double p, double i, double d, double f){
		this.kp = p;
		setI(i);
		this.kd = d;
		this.kf = f;
	}
	
	/**
	 * Sets the set point source used by this loop.
	 * @param setpoint set point source
	 */
	public void setSetPoint(DoubleSource setpoint){
		this.setPoint = setpoint;
	}
	/**
	 * Sets the pid source used by this loop.
	 * @param source pid source
	 */
	public void setPIDSource(PidSource source){
		this.source = source;
	}
	
	/**
	 * Gets whether or not this loop is enabled for use.
	 * @return true if the loop is enabled, false otherwise
	 */
	public boolean isEnabled(){
		return enabled;
	}
	/**
	 * Sets whether or not the loop is enabled for use.
	 * @param enable true to enable, false to disable
	 */
	public void setEnabled(boolean enable){
		this.enabled = enable;
	}
}
/*if(source.getType() == PidType.Rate){
double pGain = (totalError + error) * kp;
if(Mathf.limited(pGain, minimumOutput, maximumOutput))
	totalError += error;
else if(pGain < maximumOutput)
	totalError = minimumOutput / kp;
else 
	totalError = maximumOutput / kp;

result = kp * totalError + kd * error;
}else{//DISPLACEMENT!
double iGain = (totalError + error) * ki;
if(Mathf.limited(iGain, minimumOutput, maximumOutput))
	totalError += error;
else if(iGain < maximumOutput)
	totalError = minimumOutput / ki;
else 
	totalError = maximumOutput / ki;

result = kp * error + ki * totalError + kd * (error - preError);
}*/