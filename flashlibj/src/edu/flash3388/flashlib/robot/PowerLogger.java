package edu.flash3388.flashlib.robot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.util.LogUtil;
import edu.flash3388.flashlib.util.beans.DoubleSource;

/**
 * PowerLogger provides robot with utilities for tracking and logging robot power issues. This
 * can be used to help track power draw or voltage level issues. This class provides synchronized
 * logging intended to be used by a robot control loop, but if need a thread can be created to continuously 
 * track power.
 * <p>
 * To perform logging, it is necessary to add {@link PowerSource} objects using {@link #addSource(PowerSource)}.
 * Added sources will be kept in check.
 * <p>
 * To check for power issues, {@link #logPower()} needs to be called periodically.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class PowerLogger {

	/**
	 * PowerLoggingSource provides a source to power objects which should be tracked periodically
	 * by a {@link PowerLogger} objects. This class implements {@link DoubleSource} and is abstract.
	 * {@link #get()} needs to be implemented to return the current value of the power source to track.
	 * <p>
	 * Each power source has value boundary. When the value is out of bounds, this is considered an power issue.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.2.0
	 */
	public static abstract class PowerSource implements DoubleSource{
		private String name;
		private double boundMin, boundMax;
		
		public PowerSource(String name, double min, double max) {
			this.name = name;
			this.boundMax = max;
			this.boundMin = min;
		}
		
		public String getName(){
			return name;
		}
		public double getBoundMin(){
			return boundMin;
		}
		public double getBoundMax(){
			return boundMax;
		}
		
		public void setBoundMin(double boundMin){
			this.boundMin = boundMin;
		}
		public void setBoundMax(double boundMax){
			this.boundMax = boundMax;
		}
	}
	
	/**
	 * The default value for robot voltage level which warrants a log warning.
	 */
	public static final double DEFAULT_WARNING_VOLTAGE = 8.5;
	/**
	 * The default value for robot current draw which warrants a log warning.
	 */
	public static final double DEFAULT_WARNING_CURRENT_DRAW = 120.0;
	
	
	private Logger logger;
	private List<PowerSource> loggingSources;
	
	/**
	 * Creates a new PowerLogger.
	 * 
	 * @param logger the logger object to use for logging power issues
	 * @param loggingSources sources to keep track after
	 */
	public PowerLogger(Logger logger, PowerSource...loggingSources) {
		this.logger = logger;
		
		this.loggingSources = new ArrayList<PowerSource>();
		if(loggingSources != null){
			for (PowerSource powerLoggingSource : loggingSources)
				this.loggingSources.add(powerLoggingSource);
		}
	}
	/**
	 * Creates a new PowerLogger.
	 * <p>
	 * Creates a new {@link Logger} object for power logging. Uses {@link LogUtil#getLogger(String)}
	 * to create the log and passes the given log name.
	 * 
	 * @param logname the name of the log to create
	 * @param loggingSources sources to keep track after
	 * 
	 * @throws IOException if creating a logger has encountered an IO exception
	 * @throws SecurityException if creating a logger has encountered a security exception
	 */
	public PowerLogger(String logname, PowerSource...loggingSources) throws SecurityException, IOException {
		this(LogUtil.getLogger(logname), loggingSources);
	}
	
	/**
	 * Gets the {@link Logger} object used by this class to log power issues.
	 * 
	 * @return the log object
	 */
	public Logger getLogger(){
		return logger;
	}
	
	public void addSource(PowerSource source){
		loggingSources.add(source);
	}
	public PowerSource getSource(int index){
		return loggingSources.get(index);
	}
	public void removeSource(PowerSource source){
		loggingSources.remove(source);
	}
	public void removeSource(int index){
		loggingSources.remove(index);
	}
	
	/**
	 * Performs a check on the robot's power supply and logs any issues.
	 * <p>
	 * The check is done by iterating over the collection of {@link PowerSource} objects and for
	 * each one checking if the value returned from {@link PowerSource#get()} is constrained between
	 * the minimum wanted value and the maximum wanted value. If not, this event is logged.
	 */
	public void logPower(){
		for (PowerSource source : loggingSources) {
			double val = source.get();
			
			if(!Mathf.constrained(val, source.getBoundMin(), source.getBoundMax())){
				logger.warning(String.format("Value out of bounds: ", 
						source.getName(), val));
			}
		}
	}
}
