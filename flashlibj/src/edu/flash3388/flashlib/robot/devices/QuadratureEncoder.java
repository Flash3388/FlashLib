package edu.flash3388.flashlib.robot.devices;

public class QuadratureEncoder extends EncoderBase{

	private PulseCounter counter;
	
	private double distancePerPulse;
	private int pulsesPerRevolution;
	
	public QuadratureEncoder(int upPort, int downPort) {
		this(upPort, downPort, 0.0, 1);
	}
	public QuadratureEncoder(int upPort, int downPort, double distancePerPulse, int pulsesPerRevolution) {
		this.counter = IOFactory.createPulseCounter(upPort, downPort);
		this.distancePerPulse = distancePerPulse;
		this.pulsesPerRevolution = pulsesPerRevolution;
		
		if(!counter.isQuadrature())
			throw new IllegalArgumentException("Expected a quadrature counter, isQuadrature returned false");
		
		reset();
	}
	public QuadratureEncoder(PulseCounter counter) {
		this(counter, 0.0, 1);
	}
	public QuadratureEncoder(PulseCounter counter, double distancePerPulse, int pulsesPerRevolution) {
		this.counter = counter;
		this.distancePerPulse = distancePerPulse;
		this.pulsesPerRevolution = pulsesPerRevolution;
		
		if(!counter.isQuadrature())
			throw new IllegalArgumentException("Expected a quadrature counter, isQuadrature returned false");
		
		reset();
	}
	
	public void setDistancePerPulse(double distancePerPulse){
		this.distancePerPulse = distancePerPulse;
	}
	public double getDistancePerPulse(){
		return distancePerPulse;
	}
	
	public void setPulsesPerRevolution(int pulsesPerRevolution){
		this.pulsesPerRevolution = pulsesPerRevolution;
	}
	public int getPulsesPerRevolution(){
		return pulsesPerRevolution;
	}
	
	@Override
	public void free() {
		if(counter != null)
			counter.free();
		counter = null;
	}
	@Override
	public void reset() {
		super.reset();
		counter.reset();
	}

	@Override
	public double getRate() {	
		checkRest();
		
		if(counter.get() == 0)
			return 0.0;
		return ((360.0 / pulsesPerRevolution) * 60.0) / counter.getPulsePeriod();
	}
	@Override
	public double getVelocity() {
		return distancePerPulse / counter.getPulsePeriod();
	}
	@Override
	public double getDistance() {
		return counter.get() * distancePerPulse;
	}
	public boolean getDirection(){
		return counter.getDirection();
	}
	@Override
	public int getRaw() {
		return counter.get();
	}
}
