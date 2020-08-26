package com.flash3388.flashlib.io.devices.actuators;

import com.flash3388.flashlib.io.Pwm;
import com.jmath.ExtendedMath;

public class PwmPositionController extends PwmController implements PositionController {

    /**
     * Creates a new Pwm device control for a given Pwm port.
     *
     * @param port the Pwm port
     * @param pulseBounds the Pwm port bounds
     * @param pwmFrequency frequency of the Pwm in Hz
     */
    public PwmPositionController(Pwm port, PwmBounds pulseBounds, double pwmFrequency) {
        super(port, pulseBounds, pwmFrequency);
    }

    /**
     * Sets the device position between 0.0 and 1.0.
     * <p>
     * The position value is converted into duty cycle using the defined Pwm bounds for
     * the device. The output duty cycle is equal to the minimum negative value, adding to it
     * the position multiplied by the full bound scale factor:
     * <pre>
     * minNegative + position * fullScaleFactor
     * </pre>
     *
     * @param position device position between 0.0 and 1.0.
     */
    @Override
    public void set(double position) {
        if(!ExtendedMath.constrained(position, 0.0, 1.0)) {
            throw new IllegalArgumentException("invalid position: " + position);
        }

        PwmBounds bounds = getBounds();

        setDutyCycle(bounds.getMinNegative() + position * bounds.getFullScaleFactor());
    }

    /**
     * Gets the current position output to the device controller by this class.
     * <p>
     * The output position value is calculated from the current duty cycle set to the Pwm port using
     * the Pwm bounds configured.
     * <p>
     * The value is calculated using the full bound range:
     * <pre>
     * (duty - minNegative) / fullRangeScale
     * </pre>
     *
     * @return the output position value between 0.0 and 1.0
     */
    @Override
    public double get() {
        double duty = getDutyCycle();

        PwmBounds bounds = getBounds();

        if(duty > bounds.getMaxPositive()) {
            return 1.0;
        }
        if(duty < bounds.getMinNegative()) {
            return -1.0;
        }

        return (duty - bounds.getMinNegative()) / bounds.getFullScaleFactor();
    }

    @Override
    public void stop() {
        setDutyCycle(0.0);
    }
}
