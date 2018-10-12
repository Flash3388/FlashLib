package edu.flash3388.flashlib.robot.io.devices.actuators;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.robot.io.PWM;

public class PWMPositionController extends PWMController implements PositionController {

    /**
     * Creates a new PWM device control for a given PWM port.
     *
     * @param port the PWM port
     * @param pulseBounds the PWM port bounds
     * @param pwmFrequency frequency of the PWM in Hz
     */
    public PWMPositionController(PWM port, PWMBounds pulseBounds, double pwmFrequency) {
        super(port, pulseBounds, pwmFrequency);
    }

    /**
     * Sets the device position between 0.0 and 1.0.
     * <p>
     * The position value is converted into duty cycle using the defined PWM bounds for
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
        if(position < 0.0 || position > 1.0) {
            throw new IllegalArgumentException("invalid position: " + position);
        }

        position = Mathf.constrain(position, 0.0, 1.0);

        PWMBounds bounds = getBounds();

        setDutyCycle(bounds.getMinNegative() + position * bounds.getFullScaleFactor());
    }

    /**
     * Gets the current position output to the device controller by this class.
     * <p>
     * The output position value is calculated from the current duty cycle set to the PWM port using
     * the PWM bounds configured.
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

        PWMBounds bounds = getBounds();

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
