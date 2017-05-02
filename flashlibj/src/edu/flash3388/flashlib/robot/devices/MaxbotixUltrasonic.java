package edu.flash3388.flashlib.robot.devices;

import edu.flash3388.flashlib.util.Log;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SensorBase;

public class MaxbotixUltrasonic extends SensorBase implements RangeFinder{

	public static enum ReadMode{
		Analog, PulseWidth
	}
	//private static final double ANALOG_IN_COEFFICENT = 5 * 4096 / 1024.0;
	private static final double MVOLTAGE_TO_MM = 4.88 / 5.0;
	private static final double SECONDS_TO_CM = 10e6 / 147.0 * 2.54;
	
	private AnalogInput anInput;
	private Counter counter;
	private ReadMode mode;
	
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
	
	@Override
	public double getRangeCM() {
		switch(mode){
			case Analog: return (MVOLTAGE_TO_MM * (anInput.getVoltage()*1000.0)) / 10.0;
			case PulseWidth: return counter.getPeriod() * SECONDS_TO_CM;
		}
		return -1;
	}

	@Override
	public void ping() {}
}
