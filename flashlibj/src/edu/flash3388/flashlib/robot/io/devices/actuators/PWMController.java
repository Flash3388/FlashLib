package edu.flash3388.flashlib.robot.io.devices.actuators;

import edu.flash3388.flashlib.robot.io.PWM;
import edu.flash3388.flashlib.util.Resource;

/**
 * Control class for PWM-controlled electronic devices. This class should be extended by electronic devices who use
 * PWM and not instantiated.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class PWMController implements Resource {

	private PWM mPort;
	private final PWMBounds mBounds;

	/**
	 * Creates a new PWM device control for a given PWM port.
	 * 
	 * @param port the PWM port
	 * @param pulseBounds the PWM port bounds
     * @param pwmFrequency frequency of the PWM in Hz
	 */
	public PWMController(PWM port, PWMBounds pulseBounds, double pwmFrequency) {
		mPort = port;
		mPort.setFrequency(pwmFrequency);
		mBounds = calculateDutyBounds(pulseBounds, pwmFrequency);
	}



    public void setDutyCycle(double duty) {
        mPort.setDuty(duty);
    }

    public double getDutyCycle() {
        return mPort.getDuty();
    }

    public PWMBounds getBounds() {
	    return mBounds;
    }

	/**
	 * {@inheritDoc}
	 * <p>
	 * Releases the PWM port used.
	 */
	@Override
	public void free() {
		if (mPort != null) {
			mPort.free();
			mPort = null;
		}
	}

	private PWMBounds calculateDutyBounds(PWMBounds pulseBounds, double frequency) {
		double looptime = 1000.0 / frequency;

		double max = pulseBounds.getMaxPositive() / looptime;
		double deadbandMax = pulseBounds.getDeadbandMax() / looptime;
		double center = pulseBounds.getCenter() / looptime;
		double deadbandMin = pulseBounds.getDeadbandMin() / looptime;
		double min = pulseBounds.getMinNegative() / looptime;

		return new PWMBounds(max, deadbandMax, center, deadbandMin, min, pulseBounds.isDeadbandEliminated());
	}
}
