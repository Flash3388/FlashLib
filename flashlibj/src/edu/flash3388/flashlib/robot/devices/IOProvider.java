package edu.flash3388.flashlib.robot.devices;

public final class IOProvider {
	private IOProvider(){}
	
	private static IOProviderInterface provider;
	
	public static void setImplementation(IOProviderInterface iprovider){
		provider = iprovider;
	}
	public static IOProviderInterface getImplementation(){
		return provider;
	}
	public static boolean hasImplementation(){
		return provider != null;
	}
	
	private static void checkImplementation(){
		if(!hasImplementation())
			throw new IllegalStateException("Missing IOProvider implementation");
	}
	
	public static DigitalInput createDigitalInput(int port){
		checkImplementation();
		return provider.createDigitalInput(port);
	}
	public static DigitalOutput createDigitalOutput(int port){
		checkImplementation();
		return provider.createDigitalOutput(port);
	}
	
	public static AnalogInput createAnalogInput(int port){
		checkImplementation();
		return provider.createAnalogInput(port);
	}
	public static AnalogOutput createAnalogOutput(int port){
		checkImplementation();
		return provider.createAnalogOutput(port);
	}
	
	public static PWM createPWM(int port){
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
