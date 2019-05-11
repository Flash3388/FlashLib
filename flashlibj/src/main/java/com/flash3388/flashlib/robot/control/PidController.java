package com.flash3388.flashlib.robot.control;

import com.beans.DoubleProperty;
import com.beans.properties.SimpleDoubleProperty;
import com.jmath.ExtendedMath;

import java.util.function.DoubleSupplier;

import static com.jmath.ExtendedMath.constrain;

/**
 * Provides a PID controller for controlling motors more efficiently.
 * <p>
 * A proportional–integral–derivative controller (PID controller) is a control loop feedback 
 * mechanism (controller) commonly used in industrial control systems. A PID controller continuously 
 * calculates an error value e(t) as the difference between a desired setpoint 
 * and a measured process variable and applies a correction based on proportional, integral, and derivative 
 * terms (sometimes denoted P, I, and D respectively) which give their name to the controller type.
 * </p>
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see <a href="https://en.wikipedia.org/wiki/PID_controller">https://en.wikipedia.org/wiki/PID_controller</a>
 */
public class PidController {

	private final DoubleProperty mKp;
	private final DoubleProperty mKi;
	private final DoubleProperty mKd;
	private final DoubleProperty mKf;

	private double mMinimumOutput;
	private double mMaximumOutput;

	private double mTotalError;
	private double mLastError;

	private boolean mIsFirstRun;
	
	/**
	 * Creates a new PID controller. Uses given constant for the control loop, a DataSource for the set point and a pid mPidSource
	 * for the feedback data.
	 * @param kp the proportional constant
	 * @param ki the integral constant
	 * @param kd the differential constant
	 * @param kf the feed forward constant
	 */
	public PidController(DoubleProperty kp, DoubleProperty ki, DoubleProperty kd, DoubleProperty kf){
		mKp = kp;
		mKi = ki;
		mKd = kd;
		mKf = kf;

		mMinimumOutput = -1;
		mMaximumOutput = 1;

		mTotalError = 0;
        mLastError = 0.0;

		mIsFirstRun = true;
	}

    public PidController(double kp, double ki, double kd, double kf) {
	    this(new SimpleDoubleProperty(kp), new SimpleDoubleProperty(ki), new SimpleDoubleProperty(kd), new SimpleDoubleProperty(kf));
    }

    /**
     * The proportional constant property
     * @return the proportional property
     */
    public DoubleProperty kpProperty(){
        return mKp;
    }

    /**
     * The integral constant property
     * @return the integral property
     */
    public DoubleProperty kiProperty(){
        return mKi;
    }

    /**
     * The differential constant property
     * @return the differential property
     */
    public DoubleProperty kdProperty(){
        return mKd;
    }

    /**
     * The feed forward constant property
     * @return the feed forward property
     */
    public DoubleProperty kfProperty(){
        return mKf;
    }

    public void setOutputRange(double min, double max) {
        mMinimumOutput = min;
        mMaximumOutput = max;
    }

    /**
	 * Resets the controller. This erases the I term buildup, and removes 
	 * D gain on the next loop.
	 */
	public void reset(){
		mIsFirstRun = true;
		mTotalError = 0.0;
	}

    /**
     * Calculates the output to the system to compensate for the error.
     *
     * @param processVariable the process variable of the system.
     * @param setPoint the desired set point.
     * @param processType processing type for the controller
     *
     * @return the compensation value from the PID loop calculation
     */
    public double calculate(double processVariable, double setPoint, PidProcessType processType) {
        double kp = mKp.getAsDouble();
        double ki = mKi.getAsDouble();
        double kd = mKd.getAsDouble();
        double kf = mKf.getAsDouble();

        double feedForward = kf * setPoint;

        double error = setPoint - processVariable;
        double result;

        if(mIsFirstRun){
            mIsFirstRun = false;
            mLastError = error;
        }

        switch (processType) {
            case RATE: {
                if (kd != 0.0) {
                    mTotalError = constrain(mTotalError + error,
                            mMinimumOutput / kd, mMaximumOutput / kd);
                }

                result = kp * mTotalError + kd * error + feedForward;
                break;
            }
            case DISPLACEMENT: {
                if (ki != 0.0) {
                    mTotalError = constrain(mTotalError + error,
                            mMinimumOutput / ki, mMaximumOutput / ki);
                }

                result = kp * error + ki * mTotalError + kd * (error - mLastError) + feedForward;
                break;
            }
            default:
                throw new IllegalArgumentException("unknown process type");
        }

        mLastError = error;

        return constrain(result, mMinimumOutput, mMaximumOutput);
    }
}
