package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.flashboard.EmergencyStopControl;
import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.flashboard.Flashboard.FlashboardInitData;
import edu.flash3388.flashlib.flashboard.SendableLog;
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
public class FlashRobotUtil {
	private FlashRobotUtil(){}
	
	private static boolean init = false;
	private static boolean emergencyStop = false;
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
		
		RobotFactory.getImplementation().scheduler().setDisabled(true);
		if(!RobotFactory.getImplementation().isFRC())
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
		
		RobotFactory.getImplementation().scheduler().setDisabled(false);
		if(!RobotFactory.getImplementation().isFRC())
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
	 * Gets whether or not the basis of FlashLib was initialized.
	 * 
	 * @return true if FlashLib was initialized, false otherwise
	 */
	public static boolean robotInitialized(){
		return init;
	}
	/**
	 * Initializes FlashLib for robotics use. Sets the {@link Robot} implementation given to {@link RobotFactory}
	 * and initializes {@link Flashboard} according to the given initialization data.
	 * 
	 * @param robot the robot implementation
	 * @param flashboardInitData the flashboard initialization data
	 * 
	 * @throws IllegalStateException if flashlib was already initialized
	 */
	public static void initFlashLib(Robot robot, FlashboardInitData flashboardInitData){
		if(init) 
			throw new IllegalStateException("FlashLib was already initialized!");
		
		RobotFactory.setImplementation(robot);
		estopControl = new EmergencyStopControl();
		
		FlashUtil.getLog().logTime("INITIALIZING...");
		
		if(flashboardInitData != null){
			Flashboard.init(flashboardInitData);
			Flashboard.attach(estopControl,
						      new SendableLog(FlashUtil.getLog()));
		}
		
		FlashUtil.getLog().logTime("FlashLib " + FlashUtil.VERSION +" INIT - DONE");
		init = true;
	}
}
