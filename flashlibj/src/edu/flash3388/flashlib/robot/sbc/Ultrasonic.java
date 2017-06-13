package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.robot.devices.RangeFinder;
import edu.flash3388.flashlib.util.FlashUtil;
import io.silverspoon.bulldog.core.Edge;
import io.silverspoon.bulldog.core.event.InterruptEventArgs;
import io.silverspoon.bulldog.core.event.InterruptListener;
import io.silverspoon.bulldog.core.gpio.DigitalInput;
import io.silverspoon.bulldog.core.gpio.DigitalOutput;
import io.silverspoon.bulldog.core.pin.Pin;

public class Ultrasonic implements RangeFinder{

	private static class PingTask implements Runnable{
		@Override
		public void run() {
			Ultrasonic sonic = null;
			while(automaticEnabled){
				if(sonic == null)
					sonic = head;
				if(sonic == null)
					return;
				
				if(sonic.enabled){
					sonic.pingInternal();
					FlashUtil.delay(100);
				}
				sonic = sonic.next;
			}
		}
	}
	
	private static final double SPEED_OF_SOUND_CM_MS = 1130.0 * 12.0 * 2.54;
	private static final int PING_TIME_NS = (int) (10 * 1e3);
	
	private static Ultrasonic head = null;
	private static boolean automaticEnabled = false;
	private static PingTask pingTask = new PingTask();
	private static Thread autoThread;
	
	private final Ultrasonic next;
	
	private DigitalInput echo;
	private DigitalOutput trigger;
	
	private int millisStart = -1;
	private int millisPeriod = -1;
	
	private boolean enabled = true;
	
	public Ultrasonic(Pin trigger, Pin echo) {
		this.trigger = trigger.as(DigitalOutput.class);
		this.echo = echo.as(DigitalInput.class);
		
		if(this.trigger == null || this.echo == null)
			throw new IllegalArgumentException("Given ports are not supported for digital IO");
		
		setAutomaticEnabled(false);
		
		this.echo.addInterruptListener(new InterruptListener(){
			@Override
			public void interruptRequest(InterruptEventArgs e) {
				if(e.getEdge() == Edge.Rising)
					startCount();
				else if(e.getEdge() == Edge.Falling)
					stopCount();
			}
		});
		
		this.echo.setInterruptTrigger(Edge.Both);
		
		if(!this.echo.areInterruptsEnabled())
			this.echo.enableInterrupts();
		
		next = head;
		head = this;
	}
	
	private void startCount(){
		millisStart = FlashUtil.millisInt();
	}
	private void stopCount(){
		if(millisStart > 0)
			millisPeriod = FlashUtil.millisInt() - millisStart;
	}
	private void pingInternal(){
		millisPeriod = -1;
		trigger.high();
		FlashUtil.delayns(PING_TIME_NS);
		trigger.low();
	}
	
	@Override
	public double getRangeCM() {
		if(millisPeriod <= 0)
			return 0.0;
		return millisPeriod * SPEED_OF_SOUND_CM_MS * 0.5;
	}
	@Override
	public void ping() {
		if(!isEnabled() || automaticEnabled) return;
		pingInternal();
	}
	
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
	public boolean isEnabled(){
		return enabled;
	}
	
	public static void setAutomaticEnabled(boolean auto){
		if(auto == automaticEnabled)
			return;
		
		automaticEnabled = auto;
		if(auto){
			if(autoThread == null)
				autoThread = new Thread(pingTask);
			autoThread.start();
		}else{
			if(autoThread != null){
				try {
					autoThread.join();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					e.printStackTrace();
				}
			}
		}
	}
	public static boolean isAutomaticEnabled(){
		return automaticEnabled;
	}
}
