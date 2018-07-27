package edu.flash3388.flashlib.robot.io.devices.sensors;

import edu.flash3388.flashlib.robot.io.DigitalOutput;
import edu.flash3388.flashlib.robot.io.IOFactory;
import edu.flash3388.flashlib.util.FlashUtil;

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
 * <p>
 * It is possible to enter all the ultrasonic objects from this class into automatic mode using {@link #setAutomaticMode(boolean)},
 * which will start or stop a thread. The thread will iterate over all the created ultrasonics and if {@link #isEnabled()} returns
 * true, {@link #ping()} is called. Using automatic mode creates a "round robin" status for ultrasonics where they updated
 * one by one, which also makes sure that sound waves do not cancel each other since only one sensor is updated at any given
 * time.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
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
	
	private boolean enabled = true;
	
	private Ultrasonic nextSonic;
	
	private static Ultrasonic headSonic;
	
	/**
	 * Creates a new ultrasonic sensor. 
	 * <p>
	 * The ping port given is used to ping the sensor, sending out a sound wave. The echo channel receives a pulse
	 * from the ultrasonic which is then converted to distance.
	 * <p>
	 * The digital output channel is created using {@link IOFactory#createDigitalOutputPort(int)} and the pulse
	 * counter for echo is created using {@link IOFactory#createPulseCounter(int)}.
	 * 
	 * @param pingChannel channel for ping
	 * @param echoChannel channel for echo
	 */
	public Ultrasonic(int pingChannel, int echoChannel) {
		this.counter = IOFactory.createPulseCounter(echoChannel);
		this.pingChannel = IOFactory.createDigitalOutputPort(pingChannel);
		
		nextSonic = headSonic;
		headSonic = this;
	}
	/**
	 * Creates a new ultrasonic sensor. 
	 * <p>
	 * The digital output port given is used to ping the sensor, sending out a sound wave. The pulse counter
	 * is used to measure the length of the pulse in the echo channel, which is converted to distance.
	 * 
	 * @param pingChannel digital output channel for ping
	 * @param counter pulse counter for echo
	 */
	public Ultrasonic(DigitalOutput pingChannel, PulseCounter counter) {
		this.counter = counter;
		this.pingChannel = pingChannel;
		
		nextSonic = headSonic;
		headSonic = this;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Releases both the counter and the ping channel and removes this object from
	 * automatic update. If automatic update is enabled and no more sensors were created, the update
	 * thread is stopped.
	 */
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
	
	/**
	 * Sets whether or not this ultrasonic is to be updated while in automatic.
	 * 
	 * @param enabled true to enable, false to disable.
	 */
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
	/**
	 * Gets whether or not this ultrasonic is to be updated while in automatic.
	 * 
	 * @return true if enabled, false if disabled.
	 */
	public boolean isEnabled(){
		return enabled;
	}
	
	/**
	 * Sends a pulse to the ultrasonic sending our a sound wave.
	 * <p>
	 * If automatic mode is enabled, an {@link IllegalStateException} is thrown.
	 * 
	 * @throws IllegalStateException if automatic mode is enabled.
	 */
	public void ping(){
		if(inAutomaticMode)
			throw new IllegalStateException("Cannot ping, automatic mode is enabled");
		counter.reset();
		pingChannel.pulse(PING_TIME);
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
		return counter.get() > 0;
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
		if(!isRangeValid())
			return 0.0;
		return counter.getPulseLength() * SPEED_OF_SOUND * 0.5;
	}
	
	/**
	 * Starts or stops the automatic update thread. If enabled, the thread
	 * automatically sends a ping to each ultrasonic sensor created by this class updating it 
	 * and allowing to call {@link #getRangeCM()} immediately.
	 * 
	 * @param enabled true to enable, false otherwise
	 */
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
