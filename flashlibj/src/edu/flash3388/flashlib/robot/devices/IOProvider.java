package edu.flash3388.flashlib.robot.devices;

/**
 * An interface for classes which provide implementations of port interfaces to be used
 * for electronic devices. This interface is used by {@link IOFactory} to globally create 
 * port with ordered implementations. 
 * <p>
 * When using a custom implementation of port interfaces, it is recommended to implement this 
 * interface has well for use with {@link IOFactory}. 
 * <p>
 * To set the implementation for use in {@link IOFactory}, call {@link IOFactory#setProvider(IOProvider)}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public interface IOProvider {

	/**
	 * Provides an implemented {@link DigitalInput} object for a given port number.
	 * 
	 * @param port the port number
	 * @return a new {@link DigitalInput} object, or null if unable to create.
	 */
	DigitalInput createDigitalInput(int port);
	/**
	 * Provides an implemented {@link DigitalOutput} object for a given port number.
	 * 
	 * @param port the port number
	 * @return a new {@link DigitalOutput} object, or null if unable to create.
	 */
	DigitalOutput createDigitalOutput(int port);
	
	/**
	 * Provides an implemented {@link AnalogInput} object for a given port number.
	 * 
	 * @param port the port number
	 * @return a new {@link AnalogInput} object, or null if unable to create.
	 */
	AnalogInput createAnalogInput(int port);
	/**
	 * Provides an implemented {@link AnalogOutput} object for a given port number.
	 * 
	 * @param port the port number
	 * @return a new {@link AnalogOutput} object, or null if unable to create.
	 */
	AnalogOutput createAnalogOutput(int port);
	
	/**
	 * Provides an implemented {@link PWM} object for a given port number.
	 * 
	 * @param port the port number
	 * @return a new {@link PWM} object, or null if unable to create.
	 */
	PWM createPWM(int port);
	
	/**
	 * Provides an implemented {@link PulseCounter} object for a given digital port number.
	 * 
	 * @param port the port number
	 * @return a new {@link PulseCounter} object, or null if unable to create.
	 */
	PulseCounter createPulseCounter(int port);
	/**
	 * Provides an implemented {@link PulseCounter} object for 2 given digital ports. This implementation
	 * is used for quadrature encoding counting on 2 digital ports.
	 * 
	 * @param upPort the forward digital port
	 * @param downPort the backward digital port
	 * @return a new {@link PulseCounter} object, or null if unable to create.
	 */
	PulseCounter createPulseCounter(int upPort, int downPort);
}
