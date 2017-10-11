package edu.flash3388.flashlib.robot.devices;

public class IndexEncoder implements Encoder{

	private PulseCounter counter;
	private PIDType pidType;
	private double distancePerPulse;
	
	public IndexEncoder(int port) {
		this(port, 0.0);
	}
	public IndexEncoder(int port, double distancePerPulse) {
		this.counter = IOFactory.createPulseCounter(port);
		this.distancePerPulse = distancePerPulse;
		
		if(counter.isQuadrature())
			throw new IllegalArgumentException("Expected a non-quadrature counter, isQuadrature returned true");
	}
	public IndexEncoder(PulseCounter counter) {
		this(counter, 0.0);
	}
	public IndexEncoder(PulseCounter counter, double distancePerPulse) {
		this.counter = counter;
		this.distancePerPulse = distancePerPulse;
		
		if(counter.isQuadrature())
			throw new IllegalArgumentException("Expected a non-quadrature counter, isQuadrature returned true");
	}
	
	public void setDistancePerPulse(double distancePerPulse){
		this.distancePerPulse = distancePerPulse;
	}
	public double getDistancePerPulse(){
		return distancePerPulse;
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
	public double getVelocity(){
		return distancePerPulse / counter.getPulsePeriod();
	}
	@Override
	public double getRate() {
		return 60.0 / counter.getPulsePeriod();
	}
	@Override
	public double getDistance() {
		return counter.get() * distancePerPulse;
	}
	
	@Override
	public PIDType getPIDType() {
		return pidType;
	}
	@Override
	public void setPIDType(PIDType type) {
		pidType = type;
	}
}
