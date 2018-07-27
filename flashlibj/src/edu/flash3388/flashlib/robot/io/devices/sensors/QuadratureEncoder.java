package edu.flash3388.flashlib.robot.io.devices.sensors;

import edu.flash3388.flashlib.robot.io.IOFactory;
import edu.flash3388.flashlib.robot.io.devices.PulseCounter;

/**
 * Control class for a relative encoder sensor using quadrature data. Relative encoders measure the 
 * rotation of wheels axes and are used to get the rotation rate of axes, distance passed by wheels or even
 * linear velocity.
 * <p> 
 * In reality, relative encoder simply measure parts of rotations and send a pulse through a digital channel, 
 * but use those it is possible to calculate a lot of data. For example, to calculate rotation rate, the time between
 * 2 pulses is calculated and then the amount of degrees passed during those 2 pulses is divided by the time.
 * <p>
 * Most relative encoders use the quadrature encoding method in which they send data through 2 digital ports: A and B.
 * While rotating pulses are sent through both ports, but with a slight time delay between the 2. Depending on which 
 * port sent the data first, the direction of rotation is determined. In addition, there are usually several pulses 
 * sent per one rotation of the object, allowing greater accuracy of data.
 * 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class QuadratureEncoder extends EncoderBase {
	
	private static final int DEFAULT_PPR = 4096;
	
	/**
	 * Creates a new quadrature relative encoder sensor using a pulse counter object.
	 * <p>
	 * The given pulse counter object should measure pulses from both A and B encoder channel using quadrature encoding 
	 * mode.
	 * <p>
	 * Pulses per revolution indicates the amount of pulses the encoder outputs through a single channel per one rotation revolution.
	 * Distance per pulse indicates the amount of distance the object measured has passed per one pulse. In the case of a wheel, 
	 * this value will be its circumference divided by the amount of pulses.
	 * <p>
	 * The pulses per revolution is set to {@value #DEFAULT_PPR}. The distance per pulse is set to 0.0.
	 * <p>
	 * The pulse counter is created by calling {@link IOFactory#createPulseCounter(int, int)}.
	 * 
	 * @param upPort the A channel
	 * @param downPort the B channel
	 */
	public QuadratureEncoder(int upPort, int downPort) {
		this(upPort, downPort, DEFAULT_PPR, 0.0);
	}
	/**
	 * Creates a new quadrature relative encoder sensor using a pulse counter object.
	 * <p>
	 * The given pulse counter object should measure pulses from both A and B encoder channel using quadrature encoding 
	 * mode.
	 * <p>
	 * Pulses per revolution indicates the amount of pulses the encoder outputs through a single channel per one rotation revolution.
	 * Distance per pulse indicates the amount of distance the object measured has passed per one pulse. In the case of a wheel, 
	 * this value will be its circumference divided by the amount of pulses.
	 * <p>
	 * The pulse counter is created by calling {@link IOFactory#createPulseCounter(int, int)}.
	 * 
	 * @param upPort the A channel
	 * @param downPort the B channel
	 * @param pulsesPerRevolution amount of pulses per revolution of the encoder
	 * @param distancePerPulse distance per pulse in meters
	 */
	public QuadratureEncoder(int upPort, int downPort, int pulsesPerRevolution, double distancePerPulse) {
		super(upPort, downPort, pulsesPerRevolution, distancePerPulse);
	}
	/**
	 * Creates a new quadrature relative encoder sensor using a pulse counter object.
	 * <p>
	 * The given pulse counter object should measure pulses from both A and B encoder channel using quadrature encoding 
	 * mode.
	 * <p>
	 * Pulses per revolution indicates the amount of pulses the encoder outputs through a single channel per one rotation revolution.
	 * Distance per pulse indicates the amount of distance the object measured has passed per one pulse. In the case of a wheel, 
	 * this value will be its circumference divided by the amount of pulses.
	 * <p>
	 * The pulses per revolution is set to {@value #DEFAULT_PPR}. The distance per pulse is set to 0.0.
	 * 
	 * @param counter the pulse counter object
	 */
	public QuadratureEncoder(PulseCounter counter) {
		this(counter, DEFAULT_PPR, 0.0);
	}
	/**
	 * Creates a new quadrature relative encoder sensor using a pulse counter object.
	 * <p>
	 * The given pulse counter object should measure pulses from both A and B encoder channel using quadrature encoding 
	 * mode.
	 * <p>
	 * Pulses per revolution indicates the amount of pulses the encoder outputs through a single channel per one rotation revolution.
	 * Distance per pulse indicates the amount of distance the object measured has passed per one pulse. In the case of a wheel, 
	 * this value will be its circumference divided by the amount of pulses.
	 * 
	 * @param counter the pulse counter object
	 * @param pulsesPerRevolution amount of pulses per revolution of the encoder
	 * @param distancePerPulse distance per pulse in meters
	 */
	public QuadratureEncoder(PulseCounter counter, int pulsesPerRevolution, double distancePerPulse) {
		super(counter, pulsesPerRevolution, distancePerPulse, true);
	}
}
