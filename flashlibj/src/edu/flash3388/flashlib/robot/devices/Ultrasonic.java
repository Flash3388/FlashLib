package edu.flash3388.flashlib.robot.devices;

import edu.flash3388.flashlib.util.FlashUtil;

public class Ultrasonic implements RangeFinder{

	private static class SonicTask implements Runnable{
		@Override
		public void run() {
			Ultrasonic sonic = null;
			while(inAutomaticMode){
				if(sonic == null)
					sonic = headSonic;
				if(sonic == null)
					return;
				if(sonic.isEnabled())
					sonic.pingChannel.pulse(PING_TIME);
				sonic = sonic.nextSonic;
				FlashUtil.delay(50);
			}
		}
	}
	
	private static final double PING_TIME = 10 * 1e-6;
	private static final double SPEED_OF_SOUND = 340.29 * 100;//cm/sec
	
	private static boolean inAutomaticMode = false;
	private static SonicTask autoTask = new SonicTask();
	private static Thread autoThread;
	
	private PulseCounter counter;
	private DigitalOutput pingChannel;
	private DigitalInput echoChannel;
	
	private boolean enabled = true;
	
	private Ultrasonic nextSonic;
	
	private static Ultrasonic headSonic;
	
	public Ultrasonic(DigitalOutput pingChannel, DigitalInput echoChannel, PulseCounter counter) {
		this.counter = counter;
		this.pingChannel = pingChannel;
		this.echoChannel = echoChannel;
		
		nextSonic = headSonic;
		headSonic = this;
	}
	
	@Override
	public void free() {
		boolean wasAutomatic = inAutomaticMode;
		setAutomaticMode(false);
		
		setEnabled(false);
		
		if(counter != null)
			counter.free();
		counter = null;
		
		if(pingChannel != null)
			pingChannel.free();
		pingChannel = null;
	
		if(echoChannel != null)
			echoChannel.free();
		echoChannel = null;
		
		if(this == headSonic){
			headSonic = nextSonic;
			if(headSonic == null)
				setAutomaticMode(false);
		}else{
			for (Ultrasonic sonic = headSonic; sonic != null; sonic = sonic.nextSonic) {
				if(this == sonic.nextSonic){
					sonic.nextSonic = sonic.nextSonic.nextSonic;
					break;
				}
	        }
		}
		
		if(headSonic != null && wasAutomatic)
			setAutomaticMode(true);
	}
	
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
	public boolean isEnabled(){
		return enabled;
	}
	
	public void ping(){
		if(inAutomaticMode)
			throw new IllegalStateException("Cannot ping, automatic mode is enabled");
		counter.reset();
		pingChannel.pulse(PING_TIME);
	}
	public boolean isRangeValid(){
		return counter.get() > 0;
	}
	
	@Override
	public double getRangeCM() {
		if(!isRangeValid())
			return 0.0;
		return counter.getPeriod() * SPEED_OF_SOUND * 0.5;
	}
	
	
	public static void setAutomaticMode(boolean enabled){
		if(inAutomaticMode == enabled)
			return;
		
		inAutomaticMode = enabled;
		if(enabled){
			if(autoThread == null)
				autoThread = new Thread(autoTask, "Ultrasonic Updater");
			
			for (Ultrasonic sonic = headSonic; sonic != null; sonic = sonic.nextSonic) {
				sonic.counter.reset();
	        }
			
			autoThread.start();
		}else{
			if(autoThread != null && autoThread.isAlive()){
				try {
					autoThread.join();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					e.printStackTrace();
				}
			}
			
			for (Ultrasonic sonic = headSonic; sonic != null; sonic = sonic.nextSonic) {
				sonic.counter.reset();
	        }
		}
	}
}
