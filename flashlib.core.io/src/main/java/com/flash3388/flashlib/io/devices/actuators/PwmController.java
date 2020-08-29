package com.flash3388.flashlib.io.devices.actuators;

import com.flash3388.flashlib.io.Pwm;

import java.io.Closeable;
import java.io.IOException;

/**
 * Control class for Pwm-controlled electronic devices. This class should be extended by electronic devices who use
 * Pwm and not instantiated.
 *
 * @since FlashLib 1.2.0
 */
public class PwmController implements Closeable {

	private Pwm mPort;
	private final PwmBounds mBounds;

	/**
	 * Creates a new Pwm device control for a given Pwm port.
	 * 
	 * @param port the Pwm port
	 * @param pulseBounds the Pwm port bounds
     * @param pwmFrequency frequency of the Pwm in Hz
	 */
	public PwmController(Pwm port, PwmBounds pulseBounds, double pwmFrequency) {
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

    public PwmBounds getBounds() {
	    return mBounds;
    }

	/**
	 * {@inheritDoc}
	 * <p>
	 * Releases the Pwm port used.
	 */
	@Override
	public void close() throws IOException {
		if (mPort != null) {
			mPort.close();
			mPort = null;
		}
	}

	private PwmBounds calculateDutyBounds(PwmBounds pulseBounds, double frequency) {
		double looptime = 1000.0 / frequency;

		double max = pulseBounds.getMaxPositive() / looptime;
		double deadbandMax = pulseBounds.getDeadbandMax() / looptime;
		double center = pulseBounds.getCenter() / looptime;
		double deadbandMin = pulseBounds.getDeadbandMin() / looptime;
		double min = pulseBounds.getMinNegative() / looptime;

		return new PwmBounds(max, deadbandMax, center, deadbandMin, min, pulseBounds.isDeadbandEliminated());
	}
}
