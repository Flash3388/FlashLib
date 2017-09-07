package edu.flash3388.flashlib.robot.frc;

import edu.flash3388.flashlib.robot.devices.RangeFinder;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.SensorBase;

/**
 * A class for ultrasonic sensors by Maxbotix. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class MaxbotixUltrasonic extends SensorBase implements RangeFinder{

	/**
	 * Enumeration for data interfaces to the ultrasonics.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.0
	 */
	public static enum ReadMode{
		Analog, PulseWidth
	}
	
	private static final double MVOLTAGE_TO_MM = 4.88 / 5.0;
	private static final double SECONDS_TO_CM = 10e6 / 147.0 * 2.54;
	
	private AnalogInput anInput;
	private Counter counter;
	private ReadMode mode;
	
	/**
	 * Creates a new instance for handling Maxbotix ultrasonic data using a given data interface and 
	 * a channel.
	 * 
	 * @param channel ultrasonic data in channel
	 * @param mode the data interface
	 */
	public MaxbotixUltrasonic(int channel, ReadMode mode){
		switch(mode){
			case Analog:
				anInput = new AnalogInput(channel);
				break;
			case PulseWidth:
				counter = new Counter(channel);
				counter.setSemiPeriodMode(true);
				break;
		}
		this.mode = mode;
	}
	
	/**
	 * Gets the range measured by the ultrasonic in centimeters. Uses the data interface initialized.
	 * 
	 * @return range in centimeters
	 */
	@Override
	public double getRangeCM() {
		switch(mode){
			case Analog: return (MVOLTAGE_TO_MM * (anInput.getVoltage()*1000.0)) / 10.0;
			case PulseWidth: return counter.getPeriod() * SECONDS_TO_CM;
		}
		return -1;
	}
}
