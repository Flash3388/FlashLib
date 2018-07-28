package edu.flash3388.flashlib.robot.control;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.DoubleSource;
import edu.flash3388.flashlib.util.beans.SimpleDoubleProperty;

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
 * 	{@link PIDSource} provides feedback data from the feedback sensor.
 * </p>
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see <a href="https://en.wikipedia.org/wiki/PID_controller">https://en.wikipedia.org/wiki/PID_controller</a>
 */
public class PIDController {
	
	private PIDSource mPidSource;
	private DoubleSource mSetPoint;

	private DoubleProperty mKp;
	private DoubleProperty mKi;
	private DoubleProperty mKd;
	private DoubleProperty mKf;

	private double mMinimumOutput;
	private double mMaximumOutput;

	private double mSetPointRange;
	private double mOutRampRate;

	private double mTotalError;

	private double mLastOutput;
	private double mLastInput;

	private boolean mIsEnabled;
	private boolean mIsFirstRun;
	
	/**
	 * Creates a new PID controller. Uses given constant for the control loop, a DataSource for the set point and a pid mPidSource
	 * for the feedback data.
	 * @param kp the proportional constant
	 * @param ki the integral constant
	 * @param kd the differential constant
	 * @param kf the feed forward constant
	 * @param setPoint the set point
	 * @param mPidSource the feedback mPidSource
	 */
	public PIDController(DoubleProperty kp, DoubleProperty ki, DoubleProperty kd, DoubleProperty kf,
						 DoubleSource setPoint, PIDSource source){
		mKp = kp;
		mKi = ki;
		mKd = kd;
		mKf = kf;

		mSetPoint = setPoint;
		mPidSource = source;

		mMinimumOutput = -1;
		mMaximumOutput = 1;

		mSetPointRange = 0;
		mOutRampRate = 0;

		mTotalError = 0;

		mIsFirstRun = true;
		mIsEnabled = true;
	}

	/**
	 * Calculates the output to the system to compensate for the error. If the PID controller is not enabled,
	 * 0 is returned.
	 * 
	 * @return the compensation value from the PID loop calculation
	 * @throws IllegalStateException if the feedback mPidSource or the set point mPidSource is null
	 */
	public double calculate(){
		double input = mPidSource.pidGet();

		if(mSetPointRange != 0) {
			input = Mathf.constrain(input, input - mSetPointRange, input + mSetPointRange);
		}

		double error = mSetPoint.get() - input;
		
		double pOut = mKp.get() * error;
		double fOut = mKf.get() * mSetPoint.get();
		
		if(mIsFirstRun){
			mIsFirstRun = false;
			mLastOutput = pOut + fOut;
			mLastInput = input;
		}
		
		double iOut = mKi.get() * mTotalError;
		double dOut = -mKd.get() * (input - mLastInput);
		
		double output = pOut + iOut + dOut + fOut;
		
		mTotalError += error;
		
		if(mMinimumOutput != mMaximumOutput && !Mathf.constrained(output, mMinimumOutput, mMaximumOutput)) {
			mTotalError = error;
		}
		
		if(mOutRampRate != 0 && !Mathf.constrained(output, mLastOutput - mOutRampRate, mLastOutput + mOutRampRate)){
			mTotalError = error;
			output = Mathf.constrain(output, mLastOutput - mOutRampRate, mLastOutput + mOutRampRate);
		}
		
		if(mMinimumOutput != mMaximumOutput) {
			output = Mathf.constrain(output, mMinimumOutput, mMaximumOutput);
		}
		
		mLastOutput = output;
		mLastInput = input;

		return output;
	}
	/**
	 * Resets the controller. This erases the I term buildup, and removes 
	 * D gain on the next loop.
	 */
	public void reset(){
		mIsFirstRun = true;
		mTotalError = 0;
	}

	/**
	 * The proportional constant property
	 * @return the proportional property
	 */
	public DoubleProperty kpProperty(){
		return mKp;
	}
	/**
	 * Gets the proportional constant of the loop.
	 * @return proportional constant
	 */
	public double getP(){
		return mKp.get();
	}
	/**
	 * Sets the value of the proportional constant
	 * @param p proportional constant
	 */
	public void setP(double p){
		mKp.set(p);
	}
	
	/**
	 * The integral constant property
	 * @return the integral property
	 */
	public DoubleProperty kiProperty(){
		return mKi;
	}

	/**
	 * Gets the integral constant of the loop.
	 * @return integral constant
	 */
	public double getI(){
		return mKi.get();
	}

	/**
	 * Sets the value of the integral constant
	 * @param i integral constant
	 */
	public void setI(double i){
		mKi.set(i);
	}
	
	/**
	 * The differential constant property
	 * @return the differential property
	 */
	public DoubleProperty kdProperty(){
		return mKd;
	}
	/**
	 * Gets the differential constant of the loop.
	 * @return differential constant
	 */
	public double getD(){
		return mKd.get();
	}
	/**
	 * Sets the value of the differential constant
	 * @param d differential constant
	 */
	public void setD(double d){
		mKd.set(d);
	}
	
	/**
	 * The feed forward constant property
	 * @return the feed forward property
	 */
	public DoubleProperty kfProperty(){
		return mKf;
	}
	/**
	 * Gets the feed forward constant of the loop.
	 * @return differential constant
	 */
	public double getF(){
		return mKf.get();
	}
	/**
	 * Sets the feed forward gain value
	 * @param f feed forward gain
	 */
	public void setF(double f){
		mKf.set(f);
	}
	
	/**
     * Set the maximum rate the output can increase per cycle.
     * 
	 * @param rate rate of output change per cycle
	 */
	public void setOutputRampRate(double rate){
		mOutRampRate = rate;
	}

	/** 
     * Set a limit on how far the setpoint can be from the current position.
     * 
     * @param range range of setpoint
	 */
	public void setSetpointRange(double range){
		mSetPointRange = range;
	}
	/**
	 * Gets the maximum output of the loop.
	 * @return the maximum output
	 */
	public double getMaximumOutput(){
		return mMaximumOutput;
	}
	/**
	 * Gets the minimum output of the loop.
	 * @return the minimum output
	 */
	public double getMinimumOutput(){
		return mMinimumOutput;
	}

	/**
	 * Gets the set point data mPidSource used by this loop.
	 * @return set point
	 */
	public DoubleSource getSetPoint(){
		return mSetPoint;
	}
	/**
	 * Gets the pid mPidSource used by this loop.
	 * @return pid mPidSource
	 */
	public PIDSource getPIDSource(){
		return mPidSource;
	}
	
	/**
	 * Sets the minimum and maximum outputs of the loop.
	 * @param min minimum output
	 * @param max maximum output
	 */
	public void setOutputLimit(double min, double max){
		if(max < min)
			throw new IllegalArgumentException("The min value cannot be bigger than the max");

		mMinimumOutput = min;
		mMaximumOutput = max;
	}
	/**
	 * Sets the output limit of the loop. The maximum output will be equal to the given value, while the minimum output
	 * will be equal to the negative value.
	 * @param l the output limit
	 */
	public void setOutputLimit(double l){
		mMaximumOutput = l;
		mMinimumOutput = -l;
	}

	/**
	 * Sets the PID constants: proportional, integral and differential used by this loop.
	 * @param p proportional constant
	 * @param i integral constant
	 * @param d differential constant
	 */
	public void setPID(double p, double i, double d){
		setP(p);
		setI(i);
		setD(d);
	}
	/**
	 * Sets the PID constants: proportional, integral and differential used by this loop.
	 * @param p proportional constant
	 * @param i integral constant
	 * @param d differential constant
	 * @param f feed forward gain
	 */
	public void setPID(double p, double i, double d, double f){
		setP(p);
		setI(i);
		setD(d);
		setF(f);
	}
	
	/**
	 * Sets the set point mPidSource used by this loop.
	 * @param setpoint set point mPidSource
	 */
	public void setSetPoint(DoubleSource setpoint){
		mSetPoint = setpoint;
	}

	/**
	 * Sets the pid mPidSource used by this loop.
	 * @param source pid mPidSource
	 */
	public void setPIDSource(PIDSource source){
		mPidSource = source;
	}
	
	/**
	 * Gets whether or not this loop is enabled for use.
	 * @return true if the loop is enabled, false otherwise
	 */
	public boolean isEnabled(){
		return mIsEnabled;
	}

	/**
	 * Sets whether or not the loop is enabled for use.
	 * @param enable true to enable, false to disable
	 */
	public void setEnabled(boolean enable){
		mIsEnabled = enable;
	}
}
