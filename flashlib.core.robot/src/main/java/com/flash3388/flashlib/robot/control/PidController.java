package com.flash3388.flashlib.robot.control;

import com.flash3388.flashlib.app.net.NetworkInterface;
import com.flash3388.flashlib.control.ClosedLoopController;
import com.flash3388.flashlib.net.obsr.ObjectStorage;
import com.flash3388.flashlib.net.obsr.StoredEntry;
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

    private static final Time DEFAULT_PERIOD = Time.milliseconds(20);

    private final StoredEntry mErrorEntry;
    private final StoredEntry mErrorVelocityEntry;
    private final StoredEntry mProcessVariableEntry;
    private final StoredEntry mSetPointEntry;

    private final DoubleSupplier mKp;
    private final DoubleSupplier mKi;
    private final DoubleSupplier mKd;
    private final DoubleSupplier mKf;

    private double mPeriodSeconds;

    private double mMinimumOutput;
    private double mMaximumOutput;

    private double mIZone;
    private double mOutRampRate;
    private double mTolerance;
    private double mVelocityTolerance;

    private double mTotalError;
    private double mLastError;
    private double mLastErrorVelocity;

    private double mLastOutput;

    private boolean mIsFirstRun;

    /**
     * Creates a new PID controller. Uses given constant for the control loop, a DataSource for the set point and a pid mPidSource
     * for the feedback data.
     *
     * @param kp    the proportional constant
     * @param ki    the integral constant
     * @param kd    the differential constant
     * @param kf    the feed forward constant
     */
    public PidController(StoredObject object,
                         DoubleSupplier kp, DoubleSupplier ki, DoubleSupplier kd, DoubleSupplier kf) {
        mErrorEntry = object.getEntry("Error");
        mErrorVelocityEntry = object.getEntry("VelocityError");
        mProcessVariableEntry = object.getEntry("ProcessVariable");
        mSetPointEntry = object.getEntry("SetPoint");

        mKp = kp;
        mKi = ki;
        mKd = kd;
        mKf = kf;

        mPeriodSeconds = DEFAULT_PERIOD.valueAsSeconds();

        mMinimumOutput = -1;
        mMaximumOutput = 1;

        mIZone = 0;
        mOutRampRate = 0;
        mTolerance = Double.POSITIVE_INFINITY;
        mVelocityTolerance = Double.POSITIVE_INFINITY;

        reset();
    }

    public PidController(DoubleSupplier kp, DoubleSupplier ki, DoubleSupplier kd, DoubleSupplier kf) {
        this(new StoredObject.Stub(), kp, ki, kd, kf);
    }

    public PidController(double kp, double ki, double kd, double kf) {
        this(() -> kp, () -> ki, () ->  kd, () -> kf);
    }

    public PidController(StoredObject object, double kp, double ki, double kd, double kf) {
        this(object,
                createEntryForVariable(object, "kp", kp),
                createEntryForVariable(object, "ki", ki),
                createEntryForVariable(object, "kd", kd),
                createEntryForVariable(object, "kf", kf)
        );
    }

    public static PidController newNamedController(String name, double kp, double ki, double kd, double kf) {
        RobotControl control = RunningRobot.getControl();

        NetworkInterface networkInterface = control.getNetworkInterface();
        if (networkInterface.getMode().isObjectStorageEnabled()) {
            ObjectStorage objectStorage = networkInterface.getObjectStorage();
            StoredObject root = objectStorage.getInstanceRoot().getChild("PIDControllers").getChild(name);
            return new PidController(root, kp, ki, kd, kf);
        } else {
            control.getLogger().warn("OBSR not enabled, creating non-named PID controller");
        }

        return new PidController(kp, ki, kd, kf);
    }

    /**
     * Resets the controller. This erases the I term buildup, and removes
     * D gain on the next loop.
     */
    public void reset() {
        mIsFirstRun = true;
        mLastOutput = 0;
        mLastError = 0;
        mLastErrorVelocity = 0;

        mErrorEntry.setDouble(0);
        mErrorVelocityEntry.setDouble(0);
        mProcessVariableEntry.setDouble(0);
        mSetPointEntry.setDouble(0);
    }

    /**
     * Gets the <em>calculate</em> calling period, i.e. the time between each <em>calculate</em> calls.
     * @param period time
     */
    public void setPeriod(Time period) {
        mPeriodSeconds = period.valueAsSeconds();
    }

    /**
     * Sets the <em>calculate</em> calling period, i.e. the time between each <em>calculate</em> calls.
     * @return period
     */
    public Time getPeriod() {
        return Time.seconds(mPeriodSeconds);
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
     * Get the configured IZone.
     * IZone is the error value at which the total error of the controller is reset automatically.
     *
     * @return IZone value
     */
    public double getIZone() {
        return mIZone;
    }

    /**
     * Get the configured IZone.
     * IZone is the error value at which the total error of the controller is reset automatically.
     *
     * @param IZone new IZone value.
     */
    public void setIZone(double IZone) {
        mIZone = IZone;
    }

    /**
     * Gets the configured error tolerance.
     * This determines the result of {@link #isInTolerance()}, read there for more info.
     *
     * @return tolerance in <em>error units</em>.
     */
    public double getTolerance() {
        return mTolerance;
    }

    /**
     * Gets the configured error velocity tolerance.
     * This determines the result of {@link #isInTolerance()}, read there for more info.
     *
     * @return tolerance in <em>error units/seconds</em>, or -1 if not configured.
     */
    public double getVelocityTolerance() {
        return mVelocityTolerance;
    }

    /**
     * Sets the error tolerance.
     * This determines the result of {@link #isInTolerance()}, read there for more info.
     * <p>
     * If tolerance limits are not wanted, pass {@link Double#POSITIVE_INFINITY} to the respective arguments
     * {@code setTolerance(5, Double.POSITIVE_INFINITY)}.
     *
     * @param tolerance tolerance for error, in <em>error units</em>.
     * @param velocityTolerance tolerance for velocity of the error, i.e. it's change, in <em>error units/seconds</em>.
     */
    public void setTolerance(double tolerance, double velocityTolerance) {
        mTolerance = tolerance;
        mVelocityTolerance = velocityTolerance;
    }

    /**
     * Calculates the output to the system to compensate for the error.
     * <p>
     * The output is composed of multiple components: Proportional, Integral, Derivative and Feed-Forward.
     * Each component is affected by a constant: <em>kP</em>, <em>kI</em>, <em>kD</em> and <em>kF</em>. These
     * constants determine how much the component affects the output: higher values yield greater effect.
     * All the component operate on the <em>error</em>, which is the error between <em>processVariable</em> and <em>setpoint</em>.
     *
     * <ul>
     *     <li>
     *         The proportional component is directly taken from the <em>error</em>. It is affected by <em>kP</em>.
     *     </li>
     *     <li>
     *         The integral component is computed from the integral of the <em>error</em>, which in affect,
     *         is the sum of all the errors. It is affected by <em>kI</em>.
     *     </li>
     *     <li>
     *         The derivative component is computed from the derivative of the <em>error</em>, which in affect,
     *         is the change between the current error and the last error. It is affected by <em>kD</em>
     *     </li>
     *     <li>
     *         The feed-forward component is taken directly from the <em>setpoint</em>. It is affected by <em>kF</em>.
     *     </li>
     * </ul>
     * <p>
     * There are several additional effects on the output, other than the main components.
     * <ul>
     *     <li>
     *         The <em>I Zone</em> affects the computation of the integral component. It is used to automatically reset
     *         the error accumulated for the integral. When configured (with {@link #setIZone(double)}), if the
     *         current error, is larger then the value configured, the accumulated error is reset.
     *     </li>
     *     <li>
     *         The <em>Output Ramp Rate</em> affects the output directly. It is used to limit the rate of change
     *         for the output, as to slow-down or smooth the output. When configured ({@link #setOutputRampRate(double)}), if
     *         the current output changed from the last output by an amount greater then the output ramp rate,
     *         the output is decreased to {@code lastOutput + outputRampRate * direction}.
     *     </li>
     *     <li>
     *         The <em>Output Limit</em> affects the output directly. It is used to limit the output value returned,
     *         to prevent too high/low a output. When configured (via {@link #setOutputLimit(double, double)}), if
     *         the current output will be constrained within the minimum and maximum limits.
     *     </li>
     *     <li>
     *         The <em>period</em> affects the calculation of the derivative component. It is used
     *         to describe the change in error over time. It must be configured (with {@link #setPeriod(Time)})
     *         to the period of calls to this method.
     *     </li>
     * </ul>
     *
     * <p>
     * Before starting to use, {@link #reset()} should be called, to reset any accumulated or stored
     * information about previous iterations.
     *
     *
     * @param processVariable the process variable of the system.
     * @param setpoint the desired set point.
     *
     * @return the compensation value from the PID loop calculation
     */
    @Override
    public double applyAsDouble(double processVariable, double setpoint) {
        mProcessVariableEntry.setDouble(processVariable);
        mSetPointEntry.setDouble(setpoint);

        double error = setpoint - processVariable;
        mErrorEntry.setDouble(error);

        double pOut = mKp.getAsDouble() * error;
        double fOut = mKf.getAsDouble() * setpoint;

        if(mIsFirstRun){
            mIsFirstRun = false;
            mTotalError = 0;
            mLastOutput = pOut + fOut;
            mLastError = error;
        }

        if (mIZone != 0 && Math.abs(error) >= mIZone) {
            mTotalError = 0;
        }

        double errorVelocity = (error - mLastError) / mPeriodSeconds;
        mErrorVelocityEntry.setDouble(errorVelocity);

        double iOut = mKi.getAsDouble() * mTotalError;
        double dOut = mKd.getAsDouble() * errorVelocity;

        double output = pOut + iOut + dOut + fOut;

        mTotalError += error;
        mLastError = error;
        mLastErrorVelocity = errorVelocity;

        if(mOutRampRate != 0 && !ExtendedMath.constrained(output, mLastOutput - mOutRampRate, mLastOutput + mOutRampRate)){
            mTotalError = error;
            output = ExtendedMath.constrain(output, mLastOutput - mOutRampRate, mLastOutput + mOutRampRate);
        }

        if(mMinimumOutput != mMaximumOutput) {
            output = ExtendedMath.constrain(output, mMinimumOutput, mMaximumOutput);
        }

        mLastOutput = output;

        return output;
    }

    /**
     * Gets whether the system is currently within the configured tolerance.
     * <p>
     * This is checked against the last state of the system as stored from the latest call to
     * {@link #applyAsDouble(double, double)} and as such, should only be used after such a call.
     * <p>
     * Whether the system is in tolerance is determined by both the latest error and the latest
     * error <em>velocity</em>, e.g. {@code (currentError - lastError) / getPeriod()}. If both are within
     * the limit configured from {@link #setTolerance(double, double)}.
     * <p>
     * Make sure to call {@link #setTolerance(double, double)} and configure wanted tolerance before using this method.
     *
     * @return <b>true</b> if system is within tolerance, <b>false</b> otherwise.
     */
    @Override
    public boolean isInTolerance() {
        if (mIsFirstRun) {
            return false;
        }

        boolean isInErrorTolerance = ExtendedMath.constrained(mLastError, -mTolerance, mTolerance);
        boolean isInVelocityErrorTolerance = ExtendedMath.constrained(mLastErrorVelocity, -mVelocityTolerance, mVelocityTolerance);

        return isInErrorTolerance && isInVelocityErrorTolerance;
    }

    private static DoubleSupplier createEntryForVariable(StoredObject object, String name, double initialValue) {
        ValueProperty property = object.getEntry(name).valueProperty();
        property.set(Value.newDouble(initialValue));
        return property.asDouble(0.0);
    }
}
