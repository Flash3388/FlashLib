package edu.flash3388.flashlib.robot.io.devices.sensors;

import edu.flash3388.flashlib.robot.io.IOFactory;

/**
 * Control class for a relative encoder sensor using the index channel. Relative encoders measure the 
 * rotation of wheels axes and are used to get the rotation rate of axes, distance passed by wheels or even
 * linear velocity.
 * <p> 
 * In reality, relative encoder simply measure parts of rotations and send a pulse through a digital channel, 
 * but use those it is possible to calculate a lot of data. For example, to calculate rotation rate, the time between
 * 2 pulses is calculated and then the amount of degrees passed during those 2 pulses is divided by the time.
 * <p>
 * Some relative encoder sensors have an index channel, which sends a pulse once per full rotation detected. This class
 * interfaces with this channel. Using only the index channel can makes everything simpler, but comes at the cost of
 * accuracy and certain data. 
 * 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class IndexEncoder extends EncoderBase{
	
	/**
	 * Creates a new index relative encoder sensor using a pulse counter object.
	 * <p>
	 * The given pulse counter object should measure pulses from the index channel to which the encoder is
	 * connected.
	 * <p>
	 * Distance per pulse indicates the amount of distance the object measured has passed per one revolution. In the case of a wheel, 
	 * this value will be its circumference.
	 * <p>
	 * The distance per revolution is set to 0.
	 * <p>
	 * The pulse counter is created by calling {@link IOFactory#createPulseCounter(int)}.
	 * 
	 * @param port the pulse counter object
	 */
	public IndexEncoder(int port) {
		this(port, 0.0);
	}
	/**
	 * Creates a new index relative encoder sensor using a pulse counter object.
	 * <p>
	 * The given pulse counter object should measure pulses from the index channel to which the encoder is
	 * connected.
	 * <p>
	 * Distance per pulse indicates the amount of distance the object measured has passed per one revolution. In the case of a wheel, 
	 * this value will be its circumference.
	 * <p>
	 * The pulse counter is created by calling {@link IOFactory#createPulseCounter(int)}.
	 * 
	 * @param port the pulse counter object
	 * @param distancePerRevolution distance per revolution in meters
	 */
	public IndexEncoder(int port, double distancePerRevolution) {
		super(port, 1, distancePerRevolution);
	}
	/**
	 * Creates a new index relative encoder sensor using a pulse counter object.
	 * <p>
	 * The given pulse counter object should measure pulses from the index channel to which the encoder is
	 * connected.
	 * <p>
	 * Distance per pulse indicates the amount of distance the object measured has passed per one revolution. In the case of a wheel, 
	 * this value will be its circumference.
	 * <p>
	 * The distance per revolution is set to 0.
	 * 
	 * @param counter the pulse counter object
	 */
	public IndexEncoder(PulseCounter counter) {
		this(counter, 0.0);
	}
	/**
	 * Creates a new index relative encoder sensor using a pulse counter object.
	 * <p>
	 * The given pulse counter object should measure pulses from the index channel to which the encoder is
	 * connected.
	 * <p>
	 * Distance per pulse indicates the amount of distance the object measured has passed per one revolution. In the case of a wheel, 
	 * this value will be its circumference.
	 * 
	 * @param counter the pulse counter object
	 * @param distancePerRevolution distance per revolution in meters
	 */
	public IndexEncoder(PulseCounter counter, double distancePerRevolution) {
		super(counter, 1, distancePerRevolution, false);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * In this case, this value indicates the distance passed by the object when it completes a full rotation. 
	 * In the case of a wheel, this value will be its circumference.
	 */
	@Override
	public void setDistancePerPulse(double distancePerPulse) {
		super.setDistancePerPulse(distancePerPulse);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * In this case, this value indicates the distance passed by the object when it completes a full rotation. 
	 * In the case of a wheel, this value will be its circumference.
	 */
	@Override
	public double getDistancePerPulse() {
		return super.getDistancePerPulse();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns true always.
	 */
	@Override
	public boolean getDirection(){
		return true;
	}
}
