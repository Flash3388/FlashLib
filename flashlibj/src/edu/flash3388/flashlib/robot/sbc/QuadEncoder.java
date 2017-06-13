package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.robot.devices.Encoder;
import edu.flash3388.flashlib.util.FlashUtil;
import io.silverspoon.bulldog.core.Edge;
import io.silverspoon.bulldog.core.Signal;
import io.silverspoon.bulldog.core.event.InterruptEventArgs;
import io.silverspoon.bulldog.core.event.InterruptListener;
import io.silverspoon.bulldog.core.gpio.DigitalInput;
import io.silverspoon.bulldog.core.pin.Pin;

public class QuadEncoder implements Encoder{
	
	private DigitalInput inputA;
	private DigitalInput inputB;
	
	private boolean signalA = false;
	private boolean signalB = false;
	
	private double velocity = 0.0;
	private double distance = 0.0;
	
	private double distancePerRevolution = 1.0;
	private int ticksPerRevolution = 1024;
	
	private int lastTime = 0;
	
	private volatile int position = 0;
	
	public QuadEncoder(Pin a, Pin b){
		inputA = a.as(DigitalInput.class);
		inputB = b.as(DigitalInput.class);
		
		if(inputA == null || inputB == null)
			throw new IllegalArgumentException("Given ports are not supported for digital Input");
		
		inputA.setInterruptTrigger(Edge.Both);
		inputB.setInterruptTrigger(Edge.Both);
		
		if (!inputA.areInterruptsEnabled()) 
			inputA.enableInterrupts();
		if(!inputB.areInterruptsEnabled())
			inputB.enableInterrupts();
		
		inputA.addInterruptListener(new InterruptListener(){
			@Override
			public void interruptRequest(InterruptEventArgs e) {
				signalA = e.getEdge() == Edge.Rising;
				if(signalA && inputB.read() != Signal.High){
					position++;
					update();
				}
			}
		});
		inputB.addInterruptListener(new InterruptListener(){
			@Override
			public void interruptRequest(InterruptEventArgs e) {
				signalB = e.getEdge() == Edge.Rising;
				if(signalB && inputA.read() != Signal.High){
					position--;
					update();
				}
			}
		});
	}
	
	private void update(){
		int millis = FlashUtil.millisInt();
		int time = millis - lastTime;
		
		if(time < 500 && time > 0){
			velocity = distancePerRevolution / ticksPerRevolution / (time * 0.001);
			distance += position / ticksPerRevolution * distancePerRevolution;
		}
		
		lastTime = millis;
	}
	
	public void setDistancePerRevolution(double distance){
		if(distance <= 0)
			throw new IllegalArgumentException("distance must be positive");
		distancePerRevolution = distance;
	}
	public void setTicksPerRevolution(int ticks){
		if(ticks <= 0)
			throw new IllegalArgumentException("distance must be positive");
		ticksPerRevolution = ticks;
	}
	
	public void reset(){
		position = 0;
		velocity = 0.0;
		lastTime = 0;
		signalA = signalB = false;
		distance = 0.0;
	}
	
	public double getTicks(){
		return position;
	}
	@Override
	public double getRate() {
		return velocity;
	}
	@Override
	public double getDistance() {
		return distance;
	}
}
