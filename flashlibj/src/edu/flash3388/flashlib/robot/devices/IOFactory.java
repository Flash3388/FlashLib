package edu.flash3388.flashlib.robot.devices;

public final class IOFactory {
	private IOFactory(){}
	
	private static IOProvider provider;
	
	public static void setProvider(IOProvider iprovider){
		provider = iprovider;
	}
	public static IOProvider getProvider(){
		return provider;
	}
	public static boolean hasProvider(){
		return provider != null;
	}
	
	private static void checkImplementation(){
		if(!hasProvider())
			throw new IllegalStateException("Missing IOFactory provider");
	}
	
	public static DigitalInput createDigitalInputPort(int port){
		checkImplementation();
		return provider.createDigitalInput(port);
	}
	public static DigitalOutput createDigitalOutputPort(int port){
		checkImplementation();
		return provider.createDigitalOutput(port);
	}
	
	public static AnalogInput createAnalogInputPort(int port){
		checkImplementation();
		return provider.createAnalogInput(port);
	}
	public static AnalogOutput createAnalogOutputPort(int port){
		checkImplementation();
		return provider.createAnalogOutput(port);
	}
	
	public static PWM createPWMPort(int port){
		checkImplementation();
		return provider.createPWM(port);
	}
	
	public static PulseCounter createPulseCounter(int port){
		checkImplementation();
		return provider.createPulseCounter(port);
	}
	public static PulseCounter createPulseCounter(int upPort, int downPort){
		checkImplementation();
		return provider.createPulseCounter(upPort, downPort); 
	}
}
