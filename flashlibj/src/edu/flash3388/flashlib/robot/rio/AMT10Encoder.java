package edu.flash3388.flashlib.robot.rio;

import edu.flash3388.flashlib.robot.ScheduledTask;
import edu.flash3388.flashlib.robot.Scheduler;
import edu.flash3388.flashlib.robot.devices.Encoder;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

/**
 * Class for interfacing with the AMT10 encoder.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see <a href="http://www.cui.com/product/components/encoders/incremental/modular/amt10-series">http://www.cui.com/product/components/encoders/incremental/modular/amt10-series</a>
 */
public class AMT10Encoder implements ScheduledTask, PIDSource, Encoder{
	
	private static enum CounterMode{
		Quadrutate, Revolution
	}
	
	public static final int DEFAULT_PULSES_PER_REV = 1024;
	public static final int DEFAULT_CHANGE_MARGIN = 20;
	
	private PIDSourceType pidtype = PIDSourceType.kRate;
	private Counter counter;
	private CounterMode mode;
	private double velocity, rateSum, distancePerRev, lastV, changeMargin = DEFAULT_CHANGE_MARGIN;
	private long revs;
	private boolean manualUpdate = false, resetAvgOnChange = false;
	private int ticks, ticksPerRev = DEFAULT_PULSES_PER_REV, updates;
	
	/**
	 * Creates a new AMT10 object in a quadrature counting mode.
	 * 
	 * @param channelA the first quadrature channel
	 * @param channelB the second quadrature channel
	 */
	public AMT10Encoder(int channelA, int channelB){
		counter = new Counter();
		counter.setUpSource(channelA);
		counter.setDownSource(channelB);
		counter.setUpDownCounterMode();
		mode = CounterMode.Quadrutate;
		reset();
	}
	/**
	 * Creates a new AMT10 object in a revolution counting mode.
	 * 
	 * @param index the index channel
	 */
	public AMT10Encoder(int index){
		counter = new Counter();
		counter.setUpSource(index);
		counter.setUpDownCounterMode();
		counter.setSemiPeriodMode(false);
		mode = CounterMode.Revolution;
		reset();
	}

	public boolean isAutomaticUpdate(){
		return !manualUpdate;
	}
	public double getPulsesPerRevolution(){
		return ticksPerRev;
	}
	public double getDistancePerRevolution(){
		return distancePerRev;
	}
	public double getResetMargin(){
		return changeMargin;
	}
	public boolean isResetAvgOnChange(){
		return resetAvgOnChange;
	}
	public boolean setAutomaticUpdate(boolean auto){
		manualUpdate = !auto;
		if(Scheduler.schedulerHasInstance() && auto)
			Scheduler.getInstance().add(this);
		else if(auto) return false;
		return true;
	}
	public void setPulsesPerRevolution(int pulses){
		ticksPerRev = pulses;
	}
	public void setDistancePerRevolution(double d){
		this.distancePerRev = d;
	}
	public void setResetMargin(double m){
		changeMargin = m;
	}
	public void setResetAvgOnChange(boolean r){
		resetAvgOnChange = r;
	}
	
	public void reset(){
		counter.reset();
		revs = 0;
		velocity = 0;
		lastV = 0;
		resetAvg();
	}
	public void resetAvg(){
		rateSum = 0;
		updates = 0;
	}
	public void update(){
		int pulses = counter.get();
		double period = counter.getPeriod();
		counter.reset();
		switch (mode) {
			case Quadrutate:
				velocity = ((double)pulses / ticksPerRev) / period * 60.0;
				ticks += pulses;
				if(ticks >= ticksPerRev){
					revs = ticks / ticksPerRev;
					ticks %= ticksPerRev;
				}
				break;
			case Revolution:
				velocity = 1 / period * 60.0;
				revs += pulses;
				break;
		}
		if(resetAvgOnChange && Math.abs(velocity - lastV) > changeMargin)
			resetAvg();
		rateSum += velocity;
		updates++;
		lastV = velocity;
	}
	
	public double getAvgRate(){
		if(manualUpdate) update();
		return rateSum / updates;
	}
	@Override
	public double getRate(){
		if(manualUpdate) update();
		return velocity;
	}
	public long getTotal(){
		if(manualUpdate) update();
		return revs;
	}
	public int getTicks(){
		if(manualUpdate) update();
		return ticks;
	}
	@Override
	public double getDistance(){
		if(manualUpdate) update();
		return (revs + (double)ticks/ticksPerRev) * distancePerRev;
	}

	@Override
	public boolean run() {
		update();
		return !manualUpdate;
	}
	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {
		pidtype = pidSource;
	}
	@Override
	public PIDSourceType getPIDSourceType() {
		return pidtype;
	}
	@Override
	public double pidGet() {
		switch(pidtype){
			case kDisplacement: return getDistance();
			case kRate: return getRate();
		}
		return 0;
	}
}
