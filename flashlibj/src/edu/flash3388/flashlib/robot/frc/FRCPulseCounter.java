package edu.flash3388.flashlib.robot.frc;

import edu.flash3388.flashlib.robot.devices.PulseCounter;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;

public class FRCPulseCounter implements PulseCounter{
	
	private Counter counter;
	private boolean quadrature;

	public FRCPulseCounter(DigitalInput port, boolean pulseLength) {
		counter = new Counter();
		
		if(pulseLength){
			counter.setPulseLengthMode(0.01);
		}else{
			counter.setUpSource(port);
			counter.setUpDownCounterMode();
			counter.setSemiPeriodMode(false);
		}
		
		quadrature = false;
	}
	public FRCPulseCounter(int port, boolean pulseLength){
		this(new DigitalInput(port), pulseLength);
	}
	
	public FRCPulseCounter(DigitalInput upPort, DigitalInput downPort) {
		counter = new Counter();
		
		counter.setUpSource(upPort);
		counter.setDownSource(downPort);
		counter.setUpDownCounterMode();
		counter.setSemiPeriodMode(false);
		
		quadrature = true;
	}
	public FRCPulseCounter(int upPort, int downPort){
		this(new DigitalInput(upPort), new DigitalInput(downPort));
	}
	
	@Override
	public void free() {
		if(counter != null)
			counter.free();
		counter = null;
	}

	@Override
	public void reset() {
		counter.reset();
	}

	@Override
	public int get() {
		return counter.get();
	}

	@Override
	public double getPulseLength() {
		return counter.getPeriod();
	}

	@Override
	public double getPulsePeriod() {
		return counter.getPeriod();
	}
	@Override
	public boolean getDirection() {
		return counter.getDirection();
	}
	@Override
	public boolean isQuadrature() {
		return quadrature;
	}
}
