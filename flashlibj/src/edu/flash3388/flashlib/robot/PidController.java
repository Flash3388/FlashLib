package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.robot.devices.DoubleDataSource;

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
 * There are 2 types of control loops: 
 * <ul>
 * 	<li> {@link PidType#Displacement}: calculates PID for displacement input.</li>
 * 	<li> {@link PidType#Rate}: calculates PID for rate input.</li>
 * </ul>
 * <p>
 * 	{@link PidSource} provides feedback data from the feedback sensor.
 * </p>
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see <a href="https://en.wikipedia.org/wiki/PID_controller">https://en.wikipedia.org/wiki/PID_controller</a>
 */
public class PidController {
	
	private PidSource source;
	private DoubleDataSource setPoint;
	private double minimumOutput = -1, maximumOutput = 1;
	private double kp, ki, kd;
	private double totalError, error, preError;
	private boolean enabled = true;
	
	/**
	 * Creates a new PID controller. Uses given constant for the control loop, a DataSource for the set point and a pid source
	 * for the feedback data.
	 * @param kp the proportional constant
	 * @param ki the integral constant
	 * @param kd the differential constant
	 * @param setPoint the set point
	 * @param source the feedback source
	 */
	public PidController(double kp, double ki, double kd, DoubleDataSource setPoint, PidSource source){
		this.kp = kp;
		this.ki = ki;
		this.kd = kd;
		this.setPoint = setPoint;
		this.source = source;
	}
	/**
	 * Creates a new PID controller. Uses given constant for the control loop, a DataSource for the set point and without
	 * a pid source.
	 * @param kp the proportional constant
	 * @param ki the integral constant
	 * @param kd the differential constant
	 * @param setPoint the set point
	 */
	public PidController(double kp, double ki, double kd, DoubleDataSource setPoint){
		this(kp, ki, kd, setPoint, null);
	}
	/**
	 * Creates a new PID controller. Uses given constant for the control loop, without a set point and a pid source
	 * @param kp the proportional constant
	 * @param ki the integral constant
	 * @param kd the differential constant
	 */
	public PidController(double kp, double ki, double kd){
		this(kp, ki, kd, null);
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
		double result = 0;
		error = setPoint.get() - currentVal;
		
		if(source.getType() == PidType.Rate){
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
		}
		
		preError = error;
		result = Mathf.limit(result, minimumOutput, maximumOutput);
		return result;
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
	public DoubleDataSource getSetPoint(){
		return setPoint;
	}
	/**
	 * Gets the pid source used by this loop.
	 * @return pid source
	 */
	public PidSource getSource(){
		return source;
	}
	
	/**
	 * Sets the maximum output of the loop
	 * @param m maximum output
	 */
	public void setMaximumOutput(double m){
		this.maximumOutput = m;
	}
	/**
	 * Sets the minimum output of the loop
	 * @param m minimum output
	 */
	public void setMinimumOutput(double m){
		this.minimumOutput = m;
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
	 * Sets the PID constants: proportional, integral and differential used by this loop.
	 * @param p proportional constant
	 * @param i integral constant
	 * @param d differential constant
	 */
	public void setPID(double p, double i, double d){
		this.kp = p;
		this.ki = i;
		this.kd = d;
	}
	/**
	 * Sets the set point source used by this loop.
	 * @param setpoint set point source
	 */
	public void setSetPoint(DoubleDataSource setpoint){
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
