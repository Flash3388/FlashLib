package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.robot.devices.MotorSafetyHelper;

/**
 * An extension of {@link RobotBase}. This class provides extended and easier control over robot
 * operation by providing a control loop which calls user methods depending on the operation mode.
 * <p>
 * The control loop divides each operation mode into two types
 * <ul>
 * 	<li> init: initialization of the operation mode</li>
 *  <li> periodic: execution of the operation mode</li>
 * </ul>
 * `init` is called every time the robot enters a new mode. `periodic` is called every ~10ms while the robot
 * is in the operation mode.
 * <p>
 * Users extending this class must implement:
 * <ul>
 * 	<li> {@link #robotInit()}: initialization of robot systems
 * 	<li> {@link #disabledInit()}: initialization for disabled mode </li>
 * 	<li> {@link #disabledPeriodic()}: execution of disabled mode </li>
 *  <li> {@link #modeInit(int)}: initialization for a given operation mode </li>
 * 	<li> {@link #modePeriodic(int)}: execution of a given operation mode </li>
 * </ul>
 * {@link #robotInit()} is called after FlashLib systems finished initialization and are ready to be used.
 * Use this to initialize robot systems.
 * <p>
 * Since FlashLib supports custom operation modes, {@link #modeInit(int)} and {@link #modePeriodic(int)} are
 * called for every operation mode that is not disabled mode. The passed parameter is the mode's value, so it
 * is recommended to pay attention to the value.
 * <p>
 * Each iteration of the control loop puts the current thread into sleep for {@value #ITERATION_DELAY} milliseconds.
 * <p>
 * The control loop which is provided here performs several background operations before execution of user 
 * code:
 * <ul>
 * 	<li> execution of FlashLib's scheduling system </li>
 * 	<li> execution of FlashLib's motor safety </li>
 * </ul>
 * The scheduling system is updated by the control loop to allow operation of that system. While the robot
 * is in a mode, the {@link Scheduler#run()} method is executed periodically, insuring correct operation
 * of that system. When operation modes change, all {@link Action} objects are interrupted by calling 
 * {@link Scheduler#removeAllActions()} so that unwanted execution will not remain and cause issues. In
 * addition, when in disabled mode the scheduling enters {@link Scheduler#MODE_TASKS} mode so {@link Action} objects
 * are not executed, only tasks are, this is for safety of operation.
 * <p>
 * The {@link MotorSafetyHelper} class is used to provide safety for motor operations. When in disabled mode,
 * {@link MotorSafetyHelper#disableAll()} is used to disable usage of safe actuators. When in other operation
 * modes, {@link MotorSafetyHelper#checkAll()} is called periodically (every {@value #SAFETY_CHECK_COUNTER} iterations) 
 * to insure motor safety.
 * <p>
 * This class provides extended custom initialization. When the robot is initializing, {@link #preInit(IterativeRobotInitializer)}
 * is called for custom initialization. The passed object, {@link IterativeRobotInitializer} is an extension
 * of {@link RobotInitializer} which adds additional initialization options.
 * <p>
 * If flashboard was initialized, {@link Flashboard#start()} is called automatically.
 * <p>
 * When the robot enters shutdown mode {@link #robotFree()} is called to allow user shutdown operations.
 * 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public abstract class IterativeRobot extends RobotBase implements Robot{
	
	protected static class IterativeRobotInitializer extends RobotInitializer{
		/**
		 * Indicates whether or not to add an auto HID update task to the {@link Scheduler}. This will
		 * refresh HID data automatically, allowing for HID-activated actions.
		 * <p>
		 * The default value is `false`.
		 */
		public boolean autoUpdateHid = false;
		
		public void copy(IterativeRobotInitializer initializer){
			super.copy(initializer);

			autoUpdateHid = initializer.autoUpdateHid;
		}
	}
	
	public static final int ITERATION_DELAY = 5; //ms
	public static final int SAFETY_CHECK_COUNTER = 4;//counts
	
	private boolean stop = false;
	private Scheduler schedulerImpl;
	
	private void robotLoop(){
		if((Flashboard.getInitMode() & Flashboard.INIT_COMM) != 0)
			Flashboard.start();
		
		int lastState;
		int safetyCounter = 0;
		
		while(!stop){
			if(FlashRobotUtil.inEmergencyStop()){
				
				while(FlashRobotUtil.inEmergencyStop() && !stop){
					FlashUtil.delay(ITERATION_DELAY);
				}
			}
			else if(isDisabled()){
				schedulerImpl.removeAllActions();
				schedulerImpl.setMode(Scheduler.MODE_TASKS);
				MotorSafetyHelper.disableAll();
				disabledInit();
				
				while(isDisabled() && !FlashRobotUtil.inEmergencyStop() && !stop){
					schedulerImpl.run();
					disabledPeriodic();
					FlashUtil.delay(ITERATION_DELAY);
				}
			}else{
				lastState = getMode();
				safetyCounter = 0;
				schedulerImpl.removeAllActions();
				schedulerImpl.setMode(Scheduler.MODE_FULL);
				
				modeInit(lastState);
				
				while(isMode(lastState) && !FlashRobotUtil.inEmergencyStop() && !stop){
					if((++safetyCounter) >= SAFETY_CHECK_COUNTER){
						safetyCounter = 0;
						MotorSafetyHelper.checkAll();
					}
					
					schedulerImpl.run();
					modePeriodic(lastState);
					FlashUtil.delay(ITERATION_DELAY);
				}
			}
		}
	}
	
	@Override
	protected void configInit(RobotInitializer initializer){
		schedulerImpl = Scheduler.getInstance();
		
		IterativeRobotInitializer ainitializer = new IterativeRobotInitializer();
		preInit(ainitializer);
		
		if(ainitializer.autoUpdateHid)
			schedulerImpl.addTask(new HIDUpdateTask());
		
		initializer.copy(ainitializer);
	}
	@Override
	protected void robotMain() {
		robotInit();
		robotLoop();
	}
	@Override
	protected void robotShutdown(){
		stop = true;
		schedulerImpl.removeAllActions();
		schedulerImpl.setDisabled(true);
		robotFree();
	}
	

	protected void preInit(IterativeRobotInitializer initializer){}
	protected abstract void robotInit();
	protected void robotFree(){}
	
	protected abstract void disabledInit();
	protected abstract void disabledPeriodic();
	
	protected abstract void modeInit(int mode);
	protected abstract void modePeriodic(int mode);
}
