package edu.flash3388.flashlib.robot.devices;

public class PulseWidthRangeFinder implements RangeFinder{

	private static final double DEFAULT_SCALE_FACTOR = 147.0 * 2.54;
	
	private PulseCounter counter;
	private double microsecondsPerCm;
	
	public PulseWidthRangeFinder(int port) {
		this(port, DEFAULT_SCALE_FACTOR);
	}
	public PulseWidthRangeFinder(int port, double microsecondsPerCm) {
		this.counter = IOFactory.createPulseCounter(port);
		this.microsecondsPerCm = microsecondsPerCm;
	}
	public PulseWidthRangeFinder(PulseCounter counter) {
		this(counter, DEFAULT_SCALE_FACTOR);
	}
	public PulseWidthRangeFinder(PulseCounter counter, double microsecondsPerCm) {
		this.counter = counter;
		this.microsecondsPerCm = microsecondsPerCm;
	}
	
	public double getScaleFactor(){
		return microsecondsPerCm;
	}
	public void setScaleFactor(double microsecondsPerCm){
		this.microsecondsPerCm = microsecondsPerCm;
	}
	
	@Override
	public void free() {
		if(counter != null)
			counter.free();
		counter = null;
	}

	@Override
	public double getRangeCM() {
		return counter.getPulseLength() * 1e6 / microsecondsPerCm;
	}
}
