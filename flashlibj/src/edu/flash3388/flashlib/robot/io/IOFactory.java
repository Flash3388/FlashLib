package edu.flash3388.flashlib.robot.io;

import edu.flash3388.flashlib.robot.io.devices.PulseCounter;

/**
 * Provides a factory for IO ports to be used with devices. Although not a must, this class is recommended
 * due to the abstract nature of FlashLib, allowing to quickly replace implementation or use IO ports without
 * have to constantly check which implementation of port interfaces is to be used.
 * <p>
 * For this class to function, it is necessary to set an {@link IOProvider} object which will provide the
 * actual implementations.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public final class IOFactory {
	private IOFactory(){}
	
	private static IOProvider provider;
	
	/**
	 * Sets the {@link IOProvider} implementation which is responsible for providing implementations.
	 * 
	 * @param iprovider port provider to create ports with
	 */
	public static void setProvider(IOProvider iprovider){
		provider = iprovider;
	}
	/**
	 * Gets the {@link IOProvider} implementation which is responsible for providing implementations.
	 * 
	 * @return port provider to create ports with
	 */
	public static IOProvider getProvider(){
		return provider;
	}
	/**
	 * Gets whether or not an {@link IOProvider} implementation was set to this factory. If not, 
	 * ports will not be created.
	 * 
	 * @return true if a provider is set, false otherwise
	 */
	public static boolean hasProvider(){
		return provider != null;
	}
	
	private static void checkImplementation(){
		if(!hasProvider())
			throw new IllegalStateException("Missing IOFactory provider");
	}
	
	/**
	 * Creates a new {@link DigitalInput} port object from the set {@link IOProvider} implementation
	 * by calling {@link IOProvider#createDigitalInput(int)}.
	 * <p>
	 * If not {@link IOProvider} implementation was set, an exception is thrown. 
	 * <p>
	 * If the {@link IOProvider} provided a null object, an exception is thrown.
	 * 
	 * @param port the port number
	 * 
	 * @return a {@link DigitalInput} object for the given port number.
	 * 
	 * @throws IllegalStateException if no {@link IOProvider} is available.
	 * @throws NullPointerException if {@link IOProvider} returned a null port object.
	 */
	public static DigitalInput createDigitalInputPort(int port){
		checkImplementation();
		
		DigitalInput portObj = provider.createDigitalInput(port);
		if(portObj != null)
			return portObj;
		
		throw new NullPointerException("IOProvider was unable to create port, null returned");
	}
	/**
	 * Creates a new {@link DigitalOutput} port object from the set {@link IOProvider} implementation
	 * by calling {@link IOProvider#createDigitalOutput(int)}.
	 * <p>
	 * If not {@link IOProvider} implementation was set, an exception is thrown. 
	 * <p>
	 * If the {@link IOProvider} provided a null object, an exception is thrown.
	 * 
	 * @param port the port number
	 * 
	 * @return a {@link DigitalOutput} object for the given port number.
	 * 
	 * @throws IllegalStateException if no {@link IOProvider} is available.
	 * @throws NullPointerException if {@link IOProvider} returned a null port object.
	 */
	public static DigitalOutput createDigitalOutputPort(int port){
		checkImplementation();
		
		DigitalOutput portObj = provider.createDigitalOutput(port);
		if(portObj != null)
			return portObj;
		
		throw new NullPointerException("IOProvider was unable to create port, null returned");
	}
	
	/**
	 * Creates a new {@link AnalogInput} port object from the set {@link IOProvider} implementation
	 * by calling {@link IOProvider#createAnalogInput(int)}.
	 * <p>
	 * If not {@link IOProvider} implementation was set, an exception is thrown. 
	 * <p>
	 * If the {@link IOProvider} provided a null object, an exception is thrown.
	 * 
	 * @param port the port number
	 * 
	 * @return a {@link AnalogInput} object for the given port number.
	 * 
	 * @throws IllegalStateException if no {@link IOProvider} is available.
	 * @throws NullPointerException if {@link IOProvider} returned a null port object.
	 */
	public static AnalogInput createAnalogInputPort(int port){
		checkImplementation();
		
		AnalogInput portObj = provider.createAnalogInput(port);
		if(portObj != null)
			return portObj;
		
		throw new NullPointerException("IOProvider was unable to create port, null returned");
	}
	/**
	 * Creates a new {@link AnalogOutput} port object from the set {@link IOProvider} implementation
	 * by calling {@link IOProvider#createAnalogOutput(int)}.
	 * <p>
	 * If not {@link IOProvider} implementation was set, an exception is thrown. 
	 * <p>
	 * If the {@link IOProvider} provided a null object, an exception is thrown.
	 * 
	 * @param port the port number
	 * 
	 * @return a {@link AnalogOutput} object for the given port number.
	 * 
	 * @throws IllegalStateException if no {@link IOProvider} is available.
	 * @throws NullPointerException if {@link IOProvider} returned a null port object.
	 */
	public static AnalogOutput createAnalogOutputPort(int port){
		checkImplementation();
		
		AnalogOutput portObj = provider.createAnalogOutput(port);
		if(portObj != null)
			return portObj;
		
		throw new NullPointerException("IOProvider was unable to create port, null returned");
	}
	
	/**
	 * Creates a new {@link PWM} port object from the set {@link IOProvider} implementation
	 * by calling {@link IOProvider#createPWM(int)}.
	 * <p>
	 * If not {@link IOProvider} implementation was set, an exception is thrown. 
	 * <p>
	 * If the {@link IOProvider} provided a null object, an exception is thrown.
	 * 
	 * @param port the port number
	 * 
	 * @return a {@link PWM} object for the given port number.
	 * 
	 * @throws IllegalStateException if no {@link IOProvider} is available.
	 * @throws NullPointerException if {@link IOProvider} returned a null port object.
	 */
	public static PWM createPWMPort(int port){
		checkImplementation();
		
		PWM portObj = provider.createPWM(port);
		if(portObj != null)
			return portObj;
		
		throw new NullPointerException("IOProvider was unable to create port, null returned");
	}
	
	/**
	 * Creates a new {@link PulseCounter} object from the set {@link IOProvider} implementation
	 * by calling {@link IOProvider#createPulseCounter(int)}.
	 * <p>
	 * If not {@link IOProvider} implementation was set, an exception is thrown. 
	 * <p>
	 * If the {@link IOProvider} provided a null object, an exception is thrown.
	 * 
	 * @param port the digital port number for which to create the pulse counter
	 * 
	 * @return a {@link PulseCounter} object for the given port number.
	 * 
	 * @throws IllegalStateException if no {@link IOProvider} is available.
	 * @throws NullPointerException if {@link IOProvider} returned a null port object.
	 */
	public static PulseCounter createPulseCounter(int port){
		checkImplementation();
		
		PulseCounter counterObj = provider.createPulseCounter(port);
		if(counterObj != null)
			return counterObj;
		
		throw new NullPointerException("IOProvider was unable to create counter, null returned");
	}
	/**
	 * Creates a new {@link PulseCounter} object from the set {@link IOProvider} implementation
	 * by calling {@link IOProvider#createPulseCounter(int, int)}. This pulse counter is used
	 * specifically for quadrature encoding count.
	 * <p>
	 * If not {@link IOProvider} implementation was set, an exception is thrown. 
	 * <p>
	 * If the {@link IOProvider} provided a null object, an exception is thrown.
	 * 
	 * @param upPort the forward digital port number for which to create the pulse counter
	 * @param downPort the backward digital port number for which to create the pulse counter
	 * 
	 * @return a {@link PulseCounter} object for the given port number.
	 * 
	 * @throws IllegalStateException if no {@link IOProvider} is available.
	 * @throws NullPointerException if {@link IOProvider} returned a null port object.
	 */
	public static PulseCounter createPulseCounter(int upPort, int downPort){
		checkImplementation();
		
		PulseCounter counterObj = provider.createPulseCounter(upPort, downPort); 
		if(counterObj != null)
			return counterObj;
		
		throw new NullPointerException("IOProvider was unable to create counter, null returned");
	}
}
