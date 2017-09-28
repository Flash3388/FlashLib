package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.util.Log;
import edu.flash3388.flashlib.util.beans.DoubleSource;

/**
 * PowerLogger provides robot with utilities for tracking and logging robot power issues. This
 * can be used to help track power draw or voltage level issues. This class provides synchronized
 * logging intended to be used by a robot control loop, but if need a thread can be created to continuously 
 * track power.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class PowerLogger {

	/**
	 * The default value for robot voltage level which warrants a log warning.
	 */
	public static final double DEFAULT_WARNING_VOLTAGE = 8.5;
	/**
	 * The default value for robot current draw which warrants a log warning.
	 */
	public static final double DEFAULT_WARNING_POWER_DRAW = 120.0;
	
	private Log log;
	
	private DoubleSource voltageSource;
	private DoubleSource powerDrawSource;
	
	private double warningVoltage;
	private double warningPowerDraw;
	
	/**
	 * Creates a new PowerLogger.
	 * 
	 * @param log the log object to use for logging power issues
	 * @param voltageSource a source object which gets the current voltage of the robot's power supply
	 * @param powerDrawSource a source object which gets the current power draw from the robot's power supply
	 * @param warningVoltage indicates the level of voltage to consider too low and require a warning
	 * @param warningPowerDraw indicates the current draw to consider too high and require a warning
	 */
	public PowerLogger(Log log, DoubleSource voltageSource, DoubleSource powerDrawSource,
			double warningVoltage, double warningPowerDraw) {
		this.log = log;
		this.powerDrawSource = powerDrawSource;
		this.voltageSource = voltageSource;
		this.warningVoltage = warningVoltage;
		this.warningPowerDraw = warningPowerDraw;
	}
	/**
	 * Creates a new PowerLogger.
	 * <p>
	 * Uses default values for warning levels: voltage - {@value #DEFAULT_WARNING_VOLTAGE},
	 * power draw: {@value #DEFAULT_WARNING_POWER_DRAW}.
	 * 
	 * @param log the log object to use for logging power issues
	 * @param voltageSource a source object which gets the current voltage of the robot's power supply
	 * @param powerDrawSource a source object which gets the current power draw from the robot's power supply
	 */
	public PowerLogger(Log log, DoubleSource voltageSource, DoubleSource powerDrawSource) {
		this(log, voltageSource, powerDrawSource, DEFAULT_WARNING_VOLTAGE, DEFAULT_WARNING_POWER_DRAW);
	}
	/**
	 * Creates a new PowerLogger.
	 * <p>
	 * Uses default values for warning levels: voltage - {@value #DEFAULT_WARNING_VOLTAGE},
	 * power draw: {@value #DEFAULT_WARNING_POWER_DRAW}.
	 * <p>
	 * Creates a new {@link Log} object for power logging. Uses {@link Log#createBufferedLog(String)}
	 * to create the log and passes the given log name.
	 * 
	 * @param logname the name of the log to create
	 * @param voltageSource a source object which gets the current voltage of the robot's power supply
	 * @param powerDrawSource a source object which gets the current power draw from the robot's power supply
	 */
	public PowerLogger(String logname, DoubleSource voltageSource, DoubleSource powerDrawSource) {
		this(Log.createBufferedLog(logname), voltageSource, powerDrawSource);
	}
	
	/**
	 * Gets the {@link Log} object used by this class to log power issues.
	 * 
	 * @return the log object
	 */
	public Log getLog(){
		return log;
	}
	
	/**
	 * Gets the {@link DoubleSource} object which gets the voltage level of the robot's power
	 * supply.
	 * 
	 * @return the voltage source
	 */
	public DoubleSource getVoltageSource(){
		return voltageSource;
	}
	/**
	 * Gets the {@link DoubleSource} object which gets the current draw from the robot's power
	 * supply.
	 * 
	 * @return the current draw source
	 */
	public DoubleSource getPowerDrawSource(){
		return powerDrawSource;
	}
	
	/**
	 * Gets the voltage level which is considered too low and should warrant a warning in the power log.
	 * 
	 * @return the voltage level warning
	 */
	public double getVoltageWarning(){
		return warningVoltage;
	}
	/**
	 * Gets the current draw which is considered too high and should warrant a warning in the power log.
	 * 
	 * @return the current draw warning
	 */
	public double getPowerDrawWarning(){
		return warningPowerDraw;
	}
	
	/**
	 * Sets the {@link DoubleSource} object which gets the voltage level of the robot's power supply.
	 * 
	 * @param voltageSource the voltage source
	 */
	public void setVoltageSource(DoubleSource voltageSource){
		this.voltageSource = voltageSource;
	}
	/**
	 * Sets the {@link DoubleSource} object which gets the current draw from the robot's power supply.
	 * 
	 * @param powerDrawSource the current source
	 */
	public void setPowerDrawSource(DoubleSource powerDrawSource){
		this.powerDrawSource = powerDrawSource;
	}
	
	/**
	 * Sets the current draw which is considered too high and should warrant a warning in the power log.
	 * 
	 * @param powerDrawWarning current draw warning value
	 */
	public void setPowerDrawWarning(double powerDrawWarning){
		this.warningPowerDraw = powerDrawWarning;
	}
	/**
	 * Sets the voltage which is considered too low and should warrant a warning in the power log.
	 * 
	 * @param voltageWarning voltage warning value
	 */
	public void setVoltageWarning(double voltageWarning){
		this.warningVoltage = voltageWarning;
	}
	
	/**
	 * Performs a check on the robot's power supply and logs any issues.
	 * <p>
	 * The check is done in 2 parts: voltage level check, power draw check.
	 * <p>
	 * When checking the voltage level, the current voltage is retrieving by calling {@link DoubleSource#get()}
	 * for the set voltage source. If the value is equal or smaller than the set voltage warning level, then
	 * data is logged into the set power log.
	 * <p>
	 * When checking the current draw, the current draw is retrieving by calling {@link DoubleSource#get()}
	 * for the set power draw source. If the value is equal or bigger than the set power draw warning level, then
	 * data is logged into the set power log.
	 * <p>
	 * If either of the checks warrants a data log, the log is saved by calling {@link Log#save()} insuring that
	 * the data is saved in case of power loss.
	 * 
	 */
	public void logPower(){
		boolean save = false;
		
		double voltage = voltageSource.get();
		double powerDraw = powerDrawSource.get();
		
		if(voltage <= warningVoltage){
			log.logTime("LOW VOLTAGE: "+voltage, "PowerLogger");
			save = true;
		}
		if(powerDraw >= warningPowerDraw){
			log.logTime("HIGH POWER DRAW: "+powerDraw, "PowerLogger");
			save = true;
		}
		
		if(save){
			log.save();
		}
	}
}
