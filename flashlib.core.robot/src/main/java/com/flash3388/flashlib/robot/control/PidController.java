package com.flash3388.flashlib.robot.control;

import com.jmath.ExtendedMath;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

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
public class PidController implements DoubleBinaryOperator, DoubleUnaryOperator {

    private final DoubleSupplier mKp;
    private final DoubleSupplier mKi;
    private final DoubleSupplier mKd;
    private final DoubleSupplier mKf;
    private final DoubleSupplier mProcessVariable;

    private double mMinimumOutput;
    private double mMaximumOutput;

    private double mSetPointRange;
    private double mOutRampRate;

    private double mTotalError;

    private double mLastOutput;
    private double mLastProcessVariable;

    private boolean mIsFirstRun;

    /**
     * Creates a new PID controller. Uses given constant for the control loop, a DataSource for the set point and a pid mPidSource
     * for the feedback data.
     * @param kp the proportional constant
     * @param ki the integral constant
     * @param kd the differential constant
     * @param kf the feed forward constant
     * @param processVariable the process value supplier
     */
    public PidController(DoubleSupplier kp, DoubleSupplier ki, DoubleSupplier kd, DoubleSupplier kf, DoubleSupplier processVariable){
        mKp = kp;
        mKi = ki;
        mKd = kd;
        mKf = kf;
        mProcessVariable = processVariable;

        mMinimumOutput = -1;
        mMaximumOutput = 1;

        mSetPointRange = 0;
        mOutRampRate = 0;

        mTotalError = 0;

        mLastProcessVariable = 0.0;
        mLastOutput = 0.0;

        mIsFirstRun = true;
    }

    public PidController(DoubleSupplier kp, DoubleSupplier ki, DoubleSupplier kd, DoubleSupplier kf) {
        this(kp, ki, kd, kf, () -> 0.0);
    }

    public PidController(double kp, double ki, double kd, double kf, DoubleSupplier processVariable) {
        this(() -> kp, () -> ki, () ->  kd, () -> kf, processVariable);
    }

    public PidController(double kp, double ki, double kd, double kf) {
        this(kp, ki, kd, kf, () -> 0.0);
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
     * Calculates the output to the system to compensate for the error using the given process value supplier.
     *
     * @param setpoint the desired set point.
     *
     * @return the compensation value from the PID loop calculation
     */
    @Override
    public double applyAsDouble(double setpoint) {
        return applyAsDouble(mProcessVariable.getAsDouble(), setpoint);
    }

    /**
     * Calculates the output to the system to compensate for the error.
     *
     * @param processVariable the process variable of the system.
     * @param setpoint the desired set point.
     *
     * @return the compensation value from the PID loop calculation
     */
    @Override
    public double applyAsDouble(double processVariable, double setpoint) {
        return calculate(processVariable, setpoint);
    }

    private double calculate(double processVariable, double setPoint){
        if(mSetPointRange != 0) {
            processVariable = ExtendedMath.constrain(processVariable, processVariable - mSetPointRange, processVariable + mSetPointRange);
        }

        double error = setPoint - processVariable;

        double pOut = mKp.getAsDouble() * error;
        double fOut = mKf.getAsDouble() * setPoint;

        if(mIsFirstRun){
            mIsFirstRun = false;
            mLastOutput = pOut + fOut;
            mLastProcessVariable = processVariable;
        }

        double iOut = mKi.getAsDouble() * mTotalError;
        double dOut = -mKd.getAsDouble() * (processVariable - mLastProcessVariable);

        double output = pOut + iOut + dOut + fOut;

        mTotalError += error;

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
        mLastProcessVariable = processVariable;

        return output;
    }
}
