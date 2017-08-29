package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.flashboard.EmergencyStopControl;
import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.flashboard.SendableLog;
import edu.flash3388.flashlib.robot.RobotFactory.ImplType;
import edu.flash3388.flashlib.robot.hid.Joystick;
import edu.flash3388.flashlib.robot.hid.XboxController;
import edu.flash3388.flashlib.robot.sbc.MotorSafetyHelper;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.DoubleSource;

/**
 * Provides utilities for robots.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class FlashRoboUtil {
	private FlashRoboUtil(){}
	
	public static final byte UTIL_INIT = 0x0;
	public static final byte FLASHBOARD_INIT = 0x1;
	public static final byte SCHEDULER_INIT = 0x1 << 1;
	
	private static boolean init = false;
	private static boolean emergencyStop = false;
	private static byte initCode = 0;
	private static DoubleSource voltageSource;
	private static EmergencyStopControl estopControl;
	private static double expectedVoltage = 13.3;
	
	/**
	 * Gets whether or not the robot is in emergency stop.
	 * @return true if the robot is in emergency
	 */
	public static boolean inEmergencyStop(){
		return emergencyStop;
	}
	/**
	 * Enters the robot into an emergency stop. When emergency stop occurs, the Scheduler is disabled, all actions
	 * are removed and the MotorSafetyHelper disables all motors.
	 */
	public static void enterEmergencyStop(){
		if(emergencyStop) return;
		
		FlashUtil.getLog().logTime("!EMERGENCY STOP!");
		
		RobotFactory.disableScheduler(true);
		if(RobotFactory.isSbcImpl())
			MotorSafetyHelper.disableAll();
		
		estopControl.inEmergencyStop(true);
		emergencyStop = true;
	}
	/**
	 * Exists from emergency stop. The Scheduler returns to standard operation and motors are enabled.
	 */
	public static void exitEmergencyStop(){
		if(!emergencyStop) return;
		
		FlashUtil.getLog().logTime("NORMAL OPERATIONS RESUMED");
		
		if(RobotFactory.hasSchedulerInstance()){
			RobotFactory.getScheduler().setDisabled(true);
		}
		if(RobotFactory.isSbcImpl())
			MotorSafetyHelper.enableAll();
		
		estopControl.inEmergencyStop(false);
		emergencyStop = false;
	}
	
	/**
	 * Updates Human Interface Devices. Calls {@link Joystick#refreshAll()} and
	 * {@link XboxController#refreshAll()}.
	 */
	public static void updateHID(){
		Joystick.refreshAll();
		XboxController.refreshAll();
	}
	
	/**
	 * Scales percent vbus data to match the current voltage. Percent vbus works by providing
	 * a percentage of the current voltage to transfer to the motor. But in order to compensate for voltage drops,
	 * we convert the percentage to work according to the current voltage provided by the battery. Essentially making
	 * sure the same voltage is used throughout the code no matter the current supplied voltage.
	 * 
	 * @param vbus the wanted percent voltage bus if the battery were full
	 * @param currentVoltage the current voltage supplied by the battery
	 * @return the scaled percent voltage bus to match the current voltage
	 */
	public static double scaleVoltageBus(double vbus, double currentVoltage){
		if(currentVoltage <= 5) return vbus;
		return vbus * expectedVoltage / currentVoltage;
	}
	/**
	 * Scales percent vbus data to match the current voltage. Percent vbus works by providing
	 * a percentage of the current voltage to transfer to the motor. But in order to compensate for voltage drops,
	 * we convert the percentage to work according to the current voltage provided by the battery. Essentially making
	 * sure the same voltage is used throughout the code no matter the current supplied voltage.
	 * <p>
	 * The current voltage is read from a data source. If the data source was not set to a voltage supply, the same vbus will
	 * be returned.
	 * </p>
	 * 
	 * @param vbus the wanted percent voltage bus if the battery were full
	 * @return the scaled percent voltage bus to match the current voltage
	 */
	public static double scaleVoltageBus(double vbus){
		if(voltageSource == null) return vbus;
		return scaleVoltageBus(vbus, voltageSource.get());
	}
	/**
	 * Sets the voltage source to use when calling {@link #scaleVoltageBus(double)}.
	 * @param source battery voltage source
	 */
	public static void setVoltageSource(DoubleSource source){
		voltageSource = source;
	}
	/**
	 * Sets the voltage expected in the battery when it is full. The default is 13.7 volts.
	 * @param voltage expected voltage
	 */
	public static void setExpectedVoltage(double voltage){
		expectedVoltage = voltage;
	}
	
	//--------------------------------------------------------------------
	//--------------------------Init--------------------------------------
	//--------------------------------------------------------------------
	
	/**
	 * Gets whether or not Flashboard was initialized when {@link #initFlashLib(int, RobotFactory.ImplType)}
	 * was called.
	 * @return true if flashboard was initialized, false otherwise
	 */
	public static boolean flashboardInit(){
		return (initCode & FLASHBOARD_INIT) != 0;
	}
	/**
	 * Gets whether or not the basis of FlashLib was initialized when {@link #initFlashLib(int, RobotFactory.ImplType)}
	 * was called.
	 * @return true if FlashLib was initialized, false otherwise
	 */
	public static boolean utilInit(){
		return init;
	}
	/**
	 * Gets whether or not Scheduler was initialized when {@link #initFlashLib(int, RobotFactory.ImplType)}
	 * was called.
	 * @return true if Scheduler was initialized, false otherwise
	 */
	public static boolean schedulerInit(){
		return (initCode & SCHEDULER_INIT) != 0;
	}
	/**
	 * Initializes FlashLib. Depending on the initialization code, the following functionalities will be initialized:
	 * <ul>
	 * 	<li> Flashboard: if the init code contains {@link #FLASHBOARD_INIT} </li>
	 * 	<li> Scheduler: if the init code contains {@link #SCHEDULER_INIT} </li>
	 * 	<li> Main flashlib log: always </li>
	 * 	<li> RobotFactory is configured to the given {@link RobotFactory.ImplType}</li>
	 * </ul>
	 * @param mode the initialization code
	 * @param implType the platform used
	 * @throws IllegalStateException if flashlib was already initialized
	 */
	public static void initFlashLib(int mode, ImplType implType){
		if(init) 
			throw new IllegalStateException("FlashLib was already initialized!");
		
		FlashUtil.setStart();
		RobotFactory.setImplementationType(implType);
		estopControl = new EmergencyStopControl();
		
		FlashUtil.getLog().logTime("INITIALIZING...");
		
		if((mode & (FLASHBOARD_INIT)) != 0){
			Flashboard.init();
			Flashboard.attach(estopControl,
						      new SendableLog(FlashUtil.getLog()));
		}
		
		FlashUtil.getLog().logTime("FlashLib " + FlashUtil.VERSION +" INIT - DONE - " +
								Integer.toBinaryString(mode) + " - "+implType);
		
		initCode = (byte)mode;
		init = true;
	}
	/**
	 * Starts Flashboard control.
	 * @see Flashboard#start()
	 */
	public static void startFlashboard(){
		Flashboard.start();
	}
}
