package edu.flash3388.flashlib.robot.rio;

import edu.flash3388.flashlib.robot.RobotFactory;
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
public class AMT10Encoder implements Runnable, PIDSource, Encoder{
	
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

	/**
	 * Gets whether or not the encoder is updated automatically through the scheduler.
	 * @return true if the encoder is updated automatically
	 */
	public boolean isAutomaticUpdate(){
		return !manualUpdate;
	}
	/**
	 * Gets the amount of pulses from the encoder for an entire revolution. Useful in quadrature mode.
	 * @return the amount of pulse in a revolution
	 */
	public double getPulsesPerRevolution(){
		return ticksPerRev;
	}
	/**
	 * Gets the distance passed when the encoder counts one revolution
	 * @return distance per revolution
	 */
	public double getDistancePerRevolution(){
		return distancePerRev;
	}
	/**
	 * Gets the margin of data which causes a reset for the data accumulator.
	 * @return reset margin
	 */
	public double getResetMargin(){
		return changeMargin;
	}
	/**
	 * Gets whether or not the data accumulator is being reset when the margin of data is too high.
	 * @return true if reset occurs, false otherwise
	 */
	public boolean isResetAvgOnChange(){
		return resetAvgOnChange;
	}
	/**
	 * Sets the encoder for automatic updates. If true, the encoder is added as a task to the
	 * scheduler. If not, the task is removed. When in automatic update, data from the encoder is evaluated constantly by
	 * this class, allowing for more accurate tracking.
	 * 
	 * @param auto true to automatically update, false to manually update
	 * @return true if the mode change was successful, false otherwise.
	 */
	public boolean setAutomaticUpdate(boolean auto){
		manualUpdate = !auto;
		if(RobotFactory.hasSchedulerInstance() && auto){
			RobotFactory.getScheduler().addTask(this);
			return true;
		}else if(RobotFactory.hasSchedulerInstance() && !auto){
			RobotFactory.getScheduler().remove(this);
			return true;
		}
		return false;
	}
	
	/**
	 * Sets the amount of pulses being sent by the encoder to indicate a full revolution. Only needed when
	 * in quadrature mode.
	 * @param pulses amount of pulses
	 */
	public void setPulsesPerRevolution(int pulses){
		ticksPerRev = pulses;
	}
	/**
	 * Sets the distance passed when the encoder makes one revolution. Used to calculate distance passed.
	 * @param d distance per revolution
	 */
	public void setDistancePerRevolution(double d){
		this.distancePerRev = d;
	}
	/**
	 * Sets the reset margin for the accumulator. When the margin between 2 velocities has passed this value, the accumulator
	 * is reset.
	 * @param m reset margin
 	 */
	public void setResetMargin(double m){
		changeMargin = m;
	}
	/**
	 * Sets whether or not to reset the accumulator when margin between 2 velocities has passed the reset.
	 * @param r true to allow reset, false otherwise
 	 */
	public void setResetAvgOnChange(boolean r){
		resetAvgOnChange = r;
	}
	
	/**
	 * Resets all data from the encoder.
	 */
	public void reset(){
		counter.reset();
		revs = 0;
		velocity = 0;
		lastV = 0;
		resetAvg();
	}
	/**
	 * Resets the data accumulator.
	 */
	public void resetAvg(){
		rateSum = 0;
		updates = 0;
	}
	
	/**
	 * Updates the encoder data. Calculates the velocity and distance passed by the encoder.
	 * When in auto mode, should not be called manually.
	 */
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
	
	/**
	 * Gets the average angular velocity measured by the encoder. If in manual update mode, {@link #update()} will
	 * be called.
	 * @return the average velocity
	 */
	public double getAvgRate(){
		if(manualUpdate) update();
		return rateSum / updates;
	}
	/**
	 * Gets the angular velocity measured by the encoder. If in manual update mode, {@link #update()} will
	 * be called.
	 * @return the angular velocity
	 */
	@Override
	public double getRate(){
		if(manualUpdate) update();
		return velocity;
	}
	/**
	 * Gets the revolutions measured by the encoder. If in manual update mode, {@link #update()} will
	 * be called.
	 * @return revolutions of the encoder
	 */
	public long getTotal(){
		if(manualUpdate) update();
		return revs;
	}
	/**
	 * Gets the amount of pulses sent by the encoder. If in manual update mode, {@link #update()} will
	 * be called.
	 * @return pulses
	 */
	public int getTicks(){
		if(manualUpdate) update();
		return ticks;
	}
	/**
	 * Gets the distance calculated. If in manual update mode, {@link #update()} will
	 * be called.
	 * @return the distance. Measurement units depends on the set distance per revolution data
	 * @see #getDistancePerRevolution()
	 */
	@Override
	public double getDistance(){
		if(manualUpdate) update();
		return (revs + (double)ticks/ticksPerRev) * distancePerRev;
	}

	@Override
	public void run() {
		update();
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
