package edu.flash3388.flashlib.robot.io.devices.actuators;

public class PWMBounds {

    private double mCenter;
    private double mMin;
    private double mMax;
    private double mDeadbandMin;
    private double mDeadbandMax;

    private boolean mShouldEliminateDeadband;

    /**
     * Sets the data bounds for the PWM device, configuring it for use. Using those values,
     * speed and position data are converted to duty cycle which is then written to the port.
     * <p>
     * The maximum and minimum bounds indicate the smallest and largest time cycles which can be detected by the device.
     * Those bounds (in milliseconds) are used to determine the duty cycle which will indicate the minimal possible
     * and maximal possible values. The center value indicates the duty cycle which corresponds to 0.0.
     * <p>
     * If wanted it is possible to eliminate out a deadband zone of values around the center. If eliminated, those
     * values are not used in consideration when calculating the duty cycle to be used.
     *
     * @param max the maximum bound of the PWM value in milliseconds
     * @param deadbandMax the maximum bound  for the deadband
     * @param center the center of the PWM value in milliseconds
     * @param deadbandMin the minimum bound for the deadband
     * @param min the minimum bound of the PWM value in milliseconds
     * @param eliminateDeadband whether or not to eliminate the deadband
     */
    public PWMBounds(double max, double deadbandMax, double center, double deadbandMin, double min,
                  boolean eliminateDeadband) {
        mMax = max;
        mDeadbandMax = deadbandMax;
        mCenter = center;
        mDeadbandMin = deadbandMin;
        mMin = min;

        mShouldEliminateDeadband = eliminateDeadband;
    }

    public boolean isDeadbandEliminated(){
        return mShouldEliminateDeadband;
    }

    public double getCenter(){
        return mCenter;
    }

    public double getPositiveScaleFactor(){
        return getMaxPositive() - getMinPositive();
    }

    public double getMaxPositive(){
        return mMax;
    }

    public double getDeadbandMax() {
        return mDeadbandMax;
    }

    public double getMinPositive(){
        return mShouldEliminateDeadband ? mDeadbandMax : mCenter + 0.01;
    }

    public double getNegativeScaleFactor(){
        return getMaxNegative() - getMinNegative();
    }

    public double getMaxNegative(){
        return mShouldEliminateDeadband ? mDeadbandMin : mCenter - 0.01;
    }

    public double getDeadbandMin() {
        return mDeadbandMin;
    }

    public double getMinNegative(){
        return mMin;
    }

    public double getFullScaleFactor(){
        return getMaxPositive() - getMinNegative();
    }
}
