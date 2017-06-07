package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.flashboard.EmergencyStopControl;
import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.flashboard.SendableLog;
import edu.flash3388.flashlib.robot.devices.DoubleDataSource;
import edu.flash3388.flashlib.robot.hid.Joystick;
import edu.flash3388.flashlib.robot.hid.XboxController;
import edu.flash3388.flashlib.robot.sbc.MotorSafetyHelper;
import edu.flash3388.flashlib.util.FlashUtil;

public class FlashRoboUtil {
	private FlashRoboUtil(){}
	
	public static final byte UTIL_INIT = 0x0;
	public static final byte FLASHBOARD_INIT = 0x1 << 1;
	public static final byte SCHEDULER_INIT = 0x1 << 2;
	
	private static boolean init = false;
	private static boolean emergencyStop = false;
	private static byte initCode = 0;
	private static DoubleDataSource voltageSource;
	private static EmergencyStopControl estopControl;
	private static double expectedVoltage = 13.3;
	
	public static boolean inEmergencyStop(){
		return emergencyStop;
	}
	public static void enterEmergencyStop(){
		if(emergencyStop) return;
		
		FlashUtil.getLog().logTime("!EMERGENCY STOP!");
		
		if(Scheduler.schedulerHasInstance()){
			Scheduler.disableScheduler(true);
			Scheduler.getInstance().removeAllActions();
		}
		if(RobotFactory.isSbcImpl())
			MotorSafetyHelper.disableAll();
		
		estopControl.inEmergencyStop(true);
		emergencyStop = true;
	}
	public static void exitEmergencyStop(){
		if(!emergencyStop) return;
		
		FlashUtil.getLog().logTime("NORMAL OPERATIONS RESUMED");
		
		if(Scheduler.schedulerHasInstance()){
			Scheduler.disableScheduler(false);
		}
		if(RobotFactory.isSbcImpl())
			MotorSafetyHelper.enableAll();
		
		estopControl.inEmergencyStop(false);
		emergencyStop = false;
	}
	
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
		estopControl = new EmergencyStopControl();
		
		FlashUtil.getLog().logTime("INITIALIZING...");
		
		if((mode & (FLASHBOARD_INIT)) != 0){
			Flashboard.init();
			Flashboard.attach(estopControl,
						      new SendableLog(FlashUtil.getLog()));
		}
		if((mode & (SCHEDULER_INIT)) != 0){
			Scheduler.init();
		}
		
		FlashUtil.getLog().logTime("FlashLib " + FlashUtil.VERSION +" INIT - DONE - " +
								Integer.toBinaryString(mode) + " - "+implType);
		
		initCode = (byte)mode;
		init = true;
	}
	public static void startFlashboard(){
		Flashboard.start();
	}
}
