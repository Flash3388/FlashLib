package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.robot.sbc.SbcBot;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.LoggingInterface;
import edu.wpi.first.wpilibj.DriverStation;

public class RobotFactory {
	private RobotFactory(){}
	
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
	
	public static boolean isSbcImpl(){
		return type == ImplType.SBC;
	}
	public static boolean isRioImpl(){
		return type == ImplType.RIO;
	}
	
	public static boolean isControllerConnected(int controller){
		switch(type){
			case RIO: return true;
			case SBC: return SbcBot.getControlStation().isStickConnected(controller);
			default: return false;
		}
	}
	public static boolean isAxisConnected(int stick, int axis){
		switch(type){
			case RIO: return axis < DriverStation.getInstance().getStickAxisCount(stick);
			case SBC: return SbcBot.getControlStation().isStickConnected(stick);
			default: return false;
		}
	}
	public static boolean isPovConnected(int stick){
		switch(type){
			case RIO: return 1 <= DriverStation.getInstance().getStickAxisCount(stick);
			case SBC: return isControllerConnected(stick);
			default: return false;
		}
	}
	public static boolean isButtonConnected(int stick, int button){
		switch(type){
			case RIO: return button <= DriverStation.getInstance().getStickButtonCount(stick);
			case SBC: return button <= SbcBot.getControlStation().getButtonsCount(stick);
			default: return false;
		}
	}
	
	public static double getStickAxis(int stick, int axis){
		if(FlashRoboUtil.inEmergencyStop() || !isAxisConnected(stick, axis))
			return 0;
		
		switch(type){
			case RIO: return DriverStation.getInstance().getStickAxis(stick, axis);
			case SBC: return SbcBot.getControlStation().getStickAxis(stick, axis);
			default: return 0;
		}
	}
	public static int getStickPov(int stick){
		if(FlashRoboUtil.inEmergencyStop() || !isPovConnected(stick))
			return -1;
		
		switch(type){
			case RIO: return DriverStation.getInstance().getStickPOV(stick, 0);
			case SBC: return SbcBot.getControlStation().getStickPOV(stick);
			default: return -1;
		}
	}
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
