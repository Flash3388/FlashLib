package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.robot.sbc.SbcBot;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.LoggingInterface;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * To allow FlashLib to work with several different platforms, RobotFactory provides assistance based on the platform in
 * question. When FlashLib is initialized from {@link FlashRoboUtil#initFlashLib(int, ImplType)}, it sets the implementation
 * type which than allows users calling functions to get the result according to the platform.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class RobotFactory {
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
	
	protected static void setImplementationType(ImplType type){
		RobotFactory.type = type;
		
		if(type == ImplType.RIO){
			FlashUtil.getLog().addLoggingInterface(new LoggingInterface(){
				@Override
				public void reportError(String err) {
					DriverStation.reportError(err, false);
				}
				@Override
				public void reportWarning(String war) {
					DriverStation.reportWarning(war, false);
				}
				@Override
				public void log(String log) {}
			});
			RobotState.setImplementation(new RobotState(){
				@Override
				public boolean isDisabled() {
					return DriverStation.getInstance().isDisabled();
				}
				@Override
				public boolean isTeleop() {
					return DriverStation.getInstance().isOperatorControl();
				}
				
			});
		}
		else if(type == ImplType.SBC){
			RobotState.setImplementation(new RobotState(){
				@Override
				public boolean isDisabled() {
					return SbcBot.isDisabled();
				}
				@Override
				public boolean isTeleop() {
					return SbcBot.getCurrentState() == SbcBot.STATE_TELEOP;
				}
				
			});
		}
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
	
	/**
	 * Gets whether or not a controller is connected.
	 * 
	 * @param controller the controller index
	 * @return true if the controller is connected, false otherwise
	 */
	public static boolean isControllerConnected(int controller){
		switch(type){
			case RIO: return true;
			case SBC: return SbcBot.getControlStation().isStickConnected(controller);
			default: return false;
		}
	}
	/**
	 * Gets whether or not an axis exists.
	 * @param stick the stick index
	 * @param axis the axis index
	 * @return true if the stick contains the axis
	 */
	public static boolean isAxisConnected(int stick, int axis){
		switch(type){
			case RIO: return axis < DriverStation.getInstance().getStickAxisCount(stick);
			case SBC: return SbcBot.getControlStation().isStickConnected(stick);
			default: return false;
		}
	}
	/**
	 * Gets whether or not a pov exists on the stick.
	 * @param stick the stick index
	 * @return true if the stick contains a pov
	 */
	public static boolean isPovConnected(int stick){
		switch(type){
			case RIO: return 1 <= DriverStation.getInstance().getStickAxisCount(stick);
			case SBC: return isControllerConnected(stick);
			default: return false;
		}
	}
	/**
	 * Gets whether or not an button exists.
	 * @param stick the stick index
	 * @param button the button index
	 * @return true if the stick contains the button
	 */
	public static boolean isButtonConnected(int stick, int button){
		switch(type){
			case RIO: return button <= DriverStation.getInstance().getStickButtonCount(stick);
			case SBC: return button <= SbcBot.getControlStation().getButtonsCount(stick);
			default: return false;
		}
	}
	
	/**
	 * Gets the value of an axis on a stick. If the axis is not connected, or the robot is in emergency stop than 0
	 * is returned.
	 * @param stick the stick index
	 * @param axis the axis index
	 * @return the value of the axis
	 */
	public static double getStickAxis(int stick, int axis){
		if(FlashRoboUtil.inEmergencyStop() || !isAxisConnected(stick, axis))
			return 0;
		
		switch(type){
			case RIO: return DriverStation.getInstance().getStickAxis(stick, axis);
			case SBC: return SbcBot.getControlStation().getStickAxis(stick, axis);
			default: return 0;
		}
	}
	/**
	 * Gets the value of the pov on a stick. If the pov is not connected, or the robot is in emergency stop than -1
	 * is returned.
	 * @param stick the stick index
	 * @return the value of the pov
	 */
	public static int getStickPov(int stick){
		if(FlashRoboUtil.inEmergencyStop() || !isPovConnected(stick))
			return -1;
		
		switch(type){
			case RIO: return DriverStation.getInstance().getStickPOV(stick, 0);
			case SBC: return SbcBot.getControlStation().getStickPOV(stick);
			default: return -1;
		}
	}
	/**
	 * Gets the value of a button on a stick. If the button is not connected, or the robot is in emergency stop than false
	 * is returned.
	 * @param stick the stick index
	 * @param button the button index
	 * @return the value of the axis
	 */
	public static boolean getStickButton(int stick, byte button){
		if(FlashRoboUtil.inEmergencyStop() || !isButtonConnected(stick, button))
			return false;
		
		switch(type){
			case RIO: return DriverStation.getInstance().getStickButton(stick, button);
			case SBC: return SbcBot.getControlStation().getStickButton(stick, button);
			default: return false;
		}
	}
}
