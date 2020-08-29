package com.flash3388.flashlib.robot.control;

import com.jmath.ExtendedMath;

import java.util.function.DoubleSupplier;

/**
 * Provides a PID controller for controlling motors more efficiently.
 * <p>
 * A proportional–integral–derivative controller (PID controller) is a control loop feedback 
 * mechanism (controller) commonly used in industrial control systems. A PID controller continuously 
 * calculates an error value e(t) as the difference between a desired setpoint 
 * and a measured process variable and applies a correction based on proportional, integral, and derivative 
 * terms (sometimes denoted P, I, and D respectively) which give their name to the controller type.
 * </p>
 * @author Tom Tzook and Daniel Mikhailov
 * @since FlashLib 3.0.0
 * @see <a href="https://en.wikipedia.org/wiki/PID_controller">https://en.wikipedia.org/wiki/PID_controller</a>
 */
public class PidController {

    private final DoubleSupplier mKp;
    private final DoubleSupplier mKi;
    private final DoubleSupplier mKd;
    private final DoubleSupplier mKf;

    private double mMinimumOutput;
    private double mMaximumOutput;

    private double mSetPointRange;
    private double mOutRampRate;

    private double mTotalError;
    private double mLastError;
    private double mLastTimeSeconds;

    private double mLastOutput;

    private boolean mIsFirstRun;

    /**
     * Creates a new PID controller. Uses given constant for the control loop, a DataSource for the set point and a pid mPidSource
     * for the feedback data.
     * @param kp the proportional constant
     * @param ki the integral constant
     * @param kd the differential constant
     * @param kf the feed forward constant
     */
    public PidController(DoubleSupplier kp, DoubleSupplier ki, DoubleSupplier kd, DoubleSupplier kf){
        mKp = kp;
        mKi = ki;
        mKd = kd;
        mKf = kf;

        mMinimumOutput = -1;
        mMaximumOutput = 1;

        mSetPointRange = 0;
        mOutRampRate = 0;

        mTotalError = 0;
        mLastError = 0;
        mLastTimeSeconds = 0;

        mLastOutput = 0;

        mIsFirstRun = true;
    }

    public PidController(double kp, double ki, double kd, double kf) {
        this(() -> kp, () -> ki, () ->  kd, () -> kf);
    }

    /**
     * Resets the controller. This erases the I term buildup, and removes
     * D gain on the next loop.
     */
    public void reset(){
        mIsFirstRun = true;
        mTotalError = 0;
        mLastError = 0;
        mLastTimeSeconds = 0;
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
     * Sets the minimum and maximum outputs of the loop.
     * @param min minimum output
     * @param max maximum output
     */
    public void setOutputLimit(double min, double max){
        if(max < min) {
            throw new IllegalArgumentException("The min value cannot be bigger than the max");
        }

        mMinimumOutput = min;
        mMaximumOutput = max;
    }

    /**
     * Sets the output limit of the loop. The maximum output will be equal to the given value, while the minimum output
     * will be equal to the negative value.
     * @param outputLimit the output limit
     */
    public void setOutputLimit(double outputLimit){
        setOutputLimit(-outputLimit, outputLimit);
    }

    /**
     * Calculates the output to the system to compensate for the error.
     *
     * @param processVariable the process variable of the system.
     * @param setpoint the desired set point.
     *
     * @return the compensation value from the PID loop calculation
     */
    public double calculate(double processVariable, double setpoint, double currentTimeSeconds) {
        if(mSetPointRange != 0) {
            processVariable = ExtendedMath.constrain(processVariable, processVariable - mSetPointRange, processVariable + mSetPointRange);
        }

        double error = setpoint - processVariable;

        double pOut = mKp.getAsDouble() * error;
        double fOut = mKf.getAsDouble() * setpoint;

        if(mIsFirstRun){
            mIsFirstRun = false;
            mLastOutput = pOut + fOut;
            mLastError = error;
            mLastTimeSeconds = currentTimeSeconds - 1;
        }

        double iOut = mKi.getAsDouble() * mTotalError;
        double dOut = mKd.getAsDouble() * ((error - mLastError)/(currentTimeSeconds-mLastOutput));

        double output = pOut + iOut + dOut + fOut;

        mTotalError += error;
        mLastError = error;

        if(mMinimumOutput != mMaximumOutput && !ExtendedMath.constrained(output, mMinimumOutput, mMaximumOutput)) {
            mTotalError = error;
        }

        if(mOutRampRate != 0 && !ExtendedMath.constrained(output, mLastOutput - mOutRampRate, mLastOutput + mOutRampRate)){
            mTotalError = error;
            output = ExtendedMath.constrain(output, mLastOutput - mOutRampRate, mLastOutput + mOutRampRate);
        }

        if(mMinimumOutput != mMaximumOutput) {
            output = ExtendedMath.constrain(output, mMinimumOutput, mMaximumOutput);
        }

        mLastOutput = output;
        mLastTimeSeconds = currentTimeSeconds;

        return output;
    }
}
