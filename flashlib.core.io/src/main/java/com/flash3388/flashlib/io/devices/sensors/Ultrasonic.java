package com.flash3388.flashlib.io.devices.sensors;

import com.castle.util.closeables.Closer;
import com.castle.util.throwables.Throwables;
import com.flash3388.flashlib.io.Counter;
import com.flash3388.flashlib.io.DigitalOutput;

import java.io.IOException;

/**
 * Control class for an ultrasonic range finder sensor. Range finders are sensors used to measure distances between
 * them and an object in front of them. There are several ways range finders measure distances, for example: sound waves,
 * infrared, etc.
 * <p>
 * An ultrasonic uses sound waves to measure the distance to an object. This class is meant for specific control over ultrasonic
 * sensors similar to the HC-SR04 model which receive a ping to send a sound wave and then sends a HIGH output through a 
 * different channel until the sound wave returns. There are 2 digital channels used: the ping channel is used to send a
 * short ~10 us pulse to send a sound wave, the echo channel is used to receive a pulse until the sound wave is returned.
 * The length of the pulse measured in the echo channel is the time the passed from when the sound wave was sent until the time
 * it was received. This time value is then divided by 2 and multiplied by the speed of sound.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class Ultrasonic implements RangeFinder {
	
	private static final double PING_TIME = 10 * 1e-6;
	private static final double SPEED_OF_SOUND = 340.29 * 100;//cm/sec
	
	private Counter mCounter;
	private DigitalOutput mPingPort;

	/**
	 * Creates a new ultrasonic sensor. 
	 * <p>
	 * The digital output port given is used to ping the sensor, sending out a sound wave. The pulse counter
	 * is used to measure the length of the pulse in the echo channel, which is converted to distance.
	 * 
	 * @param pingChannel digital output channel for ping
	 * @param counter pulse counter for echo
	 */
	public Ultrasonic(DigitalOutput pingChannel, Counter counter) {
		this.mCounter = counter;
		this.mPingPort = pingChannel;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Releases both the counter and the ping channel.
	 */
	@Override
	public void close() throws IOException {
        Closer closer = Closer.empty();

		if (mCounter != null) {
			closer.add(mCounter);
            mCounter = null;
		}

		if (mPingPort != null) {
			closer.add(mPingPort);
            mPingPort = null;
		}

        try {
            closer.close();
        } catch (Exception e) {
            Throwables.throwAsType(e, IOException.class, IOException::new);
        }
    }
	
	/**
	 * Sends a pulse to the ultrasonic sending our a sound wave.
	 */
	public void ping(){
		mCounter.reset();
		mPingPort.pulse(PING_TIME);
	}

	/**
	 * Gets whether or not a range was measured from the ultrasonic. 
	 * <p>
	 * This is determined by checking whether at least one pulse was measured by the
	 * counter.
	 * 
	 * @return true if a range was measured, false otherwise
	 */
	public boolean isRangeValid(){
		return mCounter.get() > 0;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * The range is measured by getting the last counted pulse length from the counter, dividing
	 * it by 2 and multiplying by the speed of sound. If not pulse was measured by the counter,
	 * 0.0 is returned.
	 */
	@Override
	public double getRangeCM() {
		if(!isRangeValid()) {
			return 0.0;
		}

		return mCounter.getPulseLength() * SPEED_OF_SOUND * 0.5;
	}
}
