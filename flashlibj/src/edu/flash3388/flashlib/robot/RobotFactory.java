package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.robot.FlashRoboUtil;
import edu.flash3388.flashlib.robot.rio.RioHidInterface;
import edu.flash3388.flashlib.robot.rio.RioRobotState;
import edu.flash3388.flashlib.robot.sbc.SbcHidInterface;
import edu.flash3388.flashlib.robot.sbc.SbcRobotState;

/**
 * To allow FlashLib to work with several different platforms, RobotFactory provides assistance based on the platform in
 * question. When FlashLib is initialized from {@link FlashRoboUtil#initFlashLib(int, ImplType)}, it sets the implementation
 * type which than allows users calling functions to get the result according to the platform.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public final class RobotFactory {
	private RobotFactory(){}
	
	/**
	 * Enumeration for the implementation types of the RobotFactory.
	 * There are two types:
	 * <ul>
	 * 	<li> SBC: Refers to robots using any Single-Board computer which is not RoboRio. For now should be used only 
	 * 		with Raspberry PI and BeagleBone Black. </li>
	 * 	<li> RIO: Refers the robots using RoboRio (FRC) </li>
	 * </ul>
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.0
	 */
	public static enum ImplType{
		SBC, RIO
	}
	
	private static ImplType type;
	private static HIDInterface hid;
	private static Scheduler scheduler;
	
	protected static void setImplementationType(ImplType type){
		RobotFactory.type = type;
		
		if(isSbcImpl()){
			setHidInterface(new SbcHidInterface());
			RobotState.setImplementation(new SbcRobotState());
		}else if(isRioImpl()){
			setHidInterface(new RioHidInterface());
			RobotState.setImplementation(new RioRobotState());
		}
	}
	public static void setHidInterface(HIDInterface hid){
		RobotFactory.hid = hid;
	}
	
	/**
	 * Gets whether the implementation used is {@link ImplType#SBC}.
	 * @return true if the used implementation is SBC, false otherwise
	 */
	public static boolean isSbcImpl(){
		return type == ImplType.SBC;
	}
	/**
	 * Gets whether the implementation used is {@link ImplType#RIO}.
	 * @return true if the used implementation is RIO, false otherwise
	 */
	public static boolean isRioImpl(){
		return type == ImplType.RIO;
	}
	

	public static HIDInterface getHidInterface(){
		if(hid == null)
			hid = new HIDInterface.EmptyHIDInterface();
		return hid;
	}
	
	public static Scheduler getScheduler(){
		if(scheduler == null)
			scheduler = new Scheduler();
		return scheduler;
	}
	public static boolean hasSchedulerInstance(){
		return scheduler != null;
	}
	public static void disableScheduler(boolean disable){
		if(hasSchedulerInstance()){
			scheduler.removeAllActions();
			scheduler.setDisabled(disable);
		}
	}
	public static void runScheduler(){
		if(hasSchedulerInstance())
			scheduler.run();
	}
}
