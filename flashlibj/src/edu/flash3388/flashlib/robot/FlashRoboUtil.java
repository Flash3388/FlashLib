package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.flashboard.SendableLog;
import edu.flash3388.flashlib.robot.devices.DoubleDataSource;
import edu.flash3388.flashlib.robot.hid.Joystick;
import edu.flash3388.flashlib.robot.hid.XboxController;
import edu.flash3388.flashlib.util.FlashUtil;

public class FlashRoboUtil {
	private FlashRoboUtil(){}
	
	public static final int UTIL_INIT = 0x0;
	public static final int FLASHBOARD_INIT = 0x1 << 1;
	public static final int SCHEDULER_INIT = 0x1 << 2;
	public static final double DEFAULT_EXPECTED_VOLTAGE = 13.3;
	
	private static boolean init = false;
	private static int initCode = 0;
	private static DoubleDataSource voltageSource;
	private static double expectedVoltage = DEFAULT_EXPECTED_VOLTAGE;
	
	public static void updateHID(){
		Joystick.refreshAll();
		XboxController.refreshAll();
	}
	
	public static double scaleVoltageBus(double vbus, double currentVoltage){
		if(currentVoltage <= 5) return vbus;
		return vbus * expectedVoltage / currentVoltage;
	}
	public static double scaleVoltageBus(double vbus){
		if(voltageSource == null) return -1;
		return scaleVoltageBus(vbus, voltageSource.get());
	}
	public static void setVoltageSource(DoubleDataSource source){
		voltageSource = source;
	}
	public static void setExpectedVoltage(double voltage){
		expectedVoltage = voltage;
	}
	
	//--------------------------------------------------------------------
	//--------------------------Init--------------------------------------
	//--------------------------------------------------------------------
	
	public static boolean flashboardInit(){
		return (initCode & FLASHBOARD_INIT) != 0;
	}
	public static boolean utilInit(){
		return init;
	}
	public static boolean schedulerInit(){
		return (initCode & SCHEDULER_INIT) != 0;
	}
	public static void initFlashLib(int mode, RobotFactory.ImplType implType){
		if(init) 
			throw new IllegalStateException("FlashLib was already initialized!");
		
		FlashUtil.setStart();
		RobotFactory.setImplementationType(implType);
		
		FlashUtil.getLog().logTime("INITIALIZING...");
		
		if((mode & (FLASHBOARD_INIT)) != 0){
			Flashboard.init();
			Flashboard.attach(new SendableLog());
		}
		if((mode & (SCHEDULER_INIT)) != 0){
			Scheduler.init();
		}
		
		FlashUtil.getLog().logTime("FlashLib " + FlashUtil.VERSION +" INIT - DONE - 0x" +
								Integer.toHexString(mode) + " - "+implType);
		
		initCode = mode;
		init = true;
	}
	public static void startFlashboard(){
		Flashboard.start();
	}
}
