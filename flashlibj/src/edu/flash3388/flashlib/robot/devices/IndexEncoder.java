package edu.flash3388.flashlib.robot.devices;

public class IndexEncoder implements Encoder{

	private PulseCounter counter;
	private DigitalInput indexChannel;
	private PIDType pidType;
	private double distancePerPulse;
	
	public IndexEncoder(DigitalInput indexChannel, PulseCounter counter, double distancePerPulse) {
		this.indexChannel = indexChannel;
		this.counter = counter;
		this.distancePerPulse = distancePerPulse;
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
		
		if(indexChannel != null)
			indexChannel.free();
		indexChannel = null;
	}
	
	@Override
	public void reset() {
		counter.reset();
	}
	
	@Override
	public double getRate() {
		return distancePerPulse / counter.getPeriod();
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
