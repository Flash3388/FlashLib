package com.flash3388.flashlib.robot.control;

import com.flash3388.flashlib.app.net.NetworkInterface;
import com.flash3388.flashlib.control.ClosedLoopController;
import com.flash3388.flashlib.net.obsr.ObjectStorage;
import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.net.obsr.Value;
import com.flash3388.flashlib.net.obsr.ValueProperty;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.RunningRobot;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
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
 * @since FlashLib 1.0.0
 * @see <a href="https://en.wikipedia.org/wiki/PID_controller">https://en.wikipedia.org/wiki/PID_controller</a>
 */
public class PidController implements ClosedLoopController {

    private final Clock mClock;
    private final DoubleSupplier mKp;
    private final DoubleSupplier mKi;
    private final DoubleSupplier mKd;
    private final DoubleSupplier mKf;

    private double mMinimumOutput;
    private double mMaximumOutput;

    private double mSetPointRange;
    private double mOutRampRate;
    private double mTolerance;
    private Time mToleranceTimeout;

    private double mTotalError;
    private double mLastError;

    private double mLastOutput;
    private Time mToleranceAcceptableTime;

    private boolean mIsFirstRun;

    /**
     * Creates a new PID controller. Uses given constant for the control loop, a DataSource for the set point and a pid mPidSource
     * for the feedback data.
     *
     * @param clock
     * @param kp    the proportional constant
     * @param ki    the integral constant
     * @param kd    the differential constant
     * @param kf    the feed forward constant
     */
    public PidController(Clock clock, DoubleSupplier kp, DoubleSupplier ki, DoubleSupplier kd, DoubleSupplier kf) {
        mClock = clock;
        mKp = kp;
        mKi = ki;
        mKd = kd;
        mKf = kf;

        mMinimumOutput = -1;
        mMaximumOutput = 1;

        mSetPointRange = 0;
        mOutRampRate = 0;
        mTolerance = 0;
        mToleranceTimeout = Time.INVALID;

        mTotalError = 0;
        mLastError = 0;
        mToleranceAcceptableTime = Time.INVALID;

        mLastOutput = 0;

        mIsFirstRun = true;
    }

    public PidController(Clock clock, double kp, double ki, double kd, double kf) {
        this(clock, () -> kp, () -> ki, () ->  kd, () -> kf);
    }

    public PidController(Clock clock, StoredObject object, double kp, double ki, double kd, double kf) {
        this(clock,
                createEntryForVariable(object, "kp", kp),
                createEntryForVariable(object, "ki", ki),
                createEntryForVariable(object, "kd", kd),
                createEntryForVariable(object, "kf", kf)
        );
    }

    public PidController(Clock clock, StoredObject object) {
        this(clock, object, 0.0, 0.0, 0.0, 0.0);
    }

    public static PidController newNamedController(String name, double kp, double ki, double kd, double kf) {
        RobotControl control = RunningRobot.getControl();

        NetworkInterface networkInterface = control.getNetworkInterface();
        if (networkInterface.getMode().isObjectStorageEnabled()) {
            ObjectStorage objectStorage = networkInterface.getObjectStorage();
            StoredObject root = objectStorage.getInstanceRoot().getChild("PIDControllers").getChild(name);
            return new PidController(control.getClock(), root, kp, ki, kd, kf);
        } else {
            control.getLogger().warn("OBSR not enabled, creating non-named PID controller");
        }

        return new PidController(control.getClock(), kp, ki, kd, kf);
    }

    /**
     * Resets the controller. This erases the I term buildup, and removes
     * D gain on the next loop.
     */
    public void reset() {
        mIsFirstRun = true;
        mToleranceAcceptableTime = Time.INVALID;
        mLastOutput = 0;
        mLastError = 0;
    }

    /**
     * Set the maximum rate the output can increase per cycle.
     *
     * @param rate rate of output change per cycle
     */
    public void setOutputRampRate(double rate) {
        mOutRampRate = rate;
    }

    /**
     * Set a limit on how far the setpoint can be from the current position.
     *
     * @param range range of setpoint
     */
    public void setSetpointRange(double range) {
        mSetPointRange = range;
    }

    /**
     * Gets the maximum output of the loop.
     * @return the maximum output
     */
    public double getMaximumOutput() {
        return mMaximumOutput;
    }

    /**
     * Gets the minimum output of the loop.
     * @return the minimum output
     */
    public double getMinimumOutput() {
        return mMinimumOutput;
    }

    /**
     * Sets the minimum and maximum outputs of the loop.
     * @param min minimum output
     * @param max maximum output
     */
    public void setOutputLimit(double min, double max) {
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
    public void setOutputLimit(double outputLimit) {
        setOutputLimit(-outputLimit, outputLimit);
    }

    /**
     * Gets the error tolerance.
     *
     * @return tolerance, in measurement points used for set point.
     */
    public double getTolerance() {
        return mTolerance;
    }

    /**
     * Gets the time defined for an error tolerance to be considered acceptable.
     * That is, if the error is within the given tolerance for this amount of time,
     * the error is acceptable and the target is in the setpoint.
     * @return tolerance timeout
     */
    public Time getToleranceTimeout() {
        return mToleranceTimeout;
    }

    /**
     * Sets the error tolerance.
     *
     * @param tolerance tolerance, in measurement points used for set point.
     * @param toleranceTimeout timeout for the error, if in tolerance, to consider the error acceptable.
     */
    public void setTolerance(double tolerance, Time toleranceTimeout) {
        mTolerance = tolerance;
        mToleranceTimeout = toleranceTimeout;
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
        if(mSetPointRange != 0) {
            processVariable = ExtendedMath.constrain(processVariable, processVariable - mSetPointRange, processVariable + mSetPointRange);
        }

        double error = setpoint - processVariable;

        double pOut = mKp.getAsDouble() * error;
        double fOut = mKf.getAsDouble() * setpoint;

        if(mIsFirstRun){
            mIsFirstRun = false;
            mTotalError = 0;
            mLastOutput = pOut + fOut;
            mLastError = error;
        }

        double iOut = mKi.getAsDouble() * mTotalError;
        double dOut = mKd.getAsDouble() * (error - mLastError);

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

        if (ExtendedMath.constrained(error, setpoint - mTolerance, setpoint + mTolerance)) {
            if (mToleranceTimeout.isValid() && !mToleranceAcceptableTime.isValid()) {
                Time now = mClock.currentTime();
                mToleranceAcceptableTime = now.add(mToleranceTimeout);
            }
        } else {
            mToleranceAcceptableTime = Time.INVALID;
        }

        return output;
    }

    /**
     * Determines whether the current error can be acceptably considered as within the setpoint range.
     *
     * @param processVariable the process variable of the system.
     * @param setpoint the desired set point.
     *
     * @return <b>true</b> if the error can be acceptably considered as within the setpoint range, <b>false</b> otherwise.
     */
    @Override
    public boolean isInTolerance(double processVariable, double setpoint) {
        double error = setpoint - processVariable;
        if (ExtendedMath.constrained(error, setpoint - mTolerance, setpoint + mTolerance)) {
            if (!mToleranceTimeout.isValid()) {
                // no timeout so immediately true.
                return true;
            } else if (mToleranceAcceptableTime.isValid()) {
                Time now = mClock.currentTime();
                return now.after(mToleranceAcceptableTime);
            }
        }

        return false;
    }

    private static DoubleSupplier createEntryForVariable(StoredObject object, String name, double initialValue) {
        ValueProperty property = object.getEntry(name).valueProperty();
        property.set(Value.newDouble(initialValue));
        return property.asDouble(0.0);
    }
}
