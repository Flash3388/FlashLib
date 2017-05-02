package edu.flash3388.flashlib.robot;

import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.robot.devices.DoubleDataSource;
import edu.flash3388.flashlib.util.Log;

public class SpeedControllerMonitor {

	public static enum MonitorType{
		Voltage, Current, Speed, Rpm
	}
	public static interface MonitorLogger{
		boolean check(double val, Log log);
		MonitorType getType();
	}
	public static class LowValueMonitor implements MonitorLogger{
		private MonitorType type;
		private double lowValueThreshold;
		
		public LowValueMonitor(MonitorType t, double thershold){
			type = t;
			lowValueThreshold = thershold;
		}
		
		@Override
		public boolean check(double val, Log log) {
			if(val < lowValueThreshold)
				log.log("Low value: "+val);
			return false;
		}
		@Override
		public MonitorType getType() {
			return null;
		}
	}
	
	private Vector<MonitorLogger> loggers = new Vector<MonitorLogger>();
	private DoubleDataSource voltageSource;
	private DoubleDataSource currentSource;
	private DoubleDataSource speedSource;
	private DoubleDataSource rpmSource;
	private Log controllerLog;
	
	public SpeedControllerMonitor(String name, DoubleDataSource volts, DoubleDataSource amps, DoubleDataSource rpm, DoubleDataSource spe){
		this.currentSource = amps;
		this.voltageSource = volts;
		this.rpmSource = rpm;
		this.speedSource = spe;
		
		controllerLog = new Log(name+"-Monitor");
	}
	
	public void setVoltageSource(DoubleDataSource source){
		voltageSource = source;
	}
	public void setCurrentSource(DoubleDataSource source){
		currentSource = source;
	}
	public void setRpmSource(DoubleDataSource source){
		rpmSource = source;
	}
	public void setSpeedSource(DoubleDataSource source){
		speedSource = source;
	}
	
	public void update(){
		for (Enumeration<MonitorLogger> logEnum = loggers.elements(); logEnum.hasMoreElements();) {
			MonitorLogger monitor = logEnum.nextElement();
			if(monitor.check(getByType(monitor.getType()), controllerLog))
				save();
		}
	}
	public void save(){
		controllerLog.save();
	}
	private double getByType(MonitorType t){
		switch (t) {
			case Current:
				if(currentSource != null)
					return currentSource.get();
				break;
			case Rpm:
				if(rpmSource != null)
					return rpmSource.get();
				break;
			case Voltage:
				if(voltageSource != null)
					return voltageSource.get();
				break;
			case Speed:
				if(speedSource != null)
					return speedSource.get();
				break;
		}
		return 0;
	}
}
