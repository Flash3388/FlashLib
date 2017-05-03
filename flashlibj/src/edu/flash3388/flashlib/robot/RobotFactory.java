package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.robot.hid.Button;
import edu.flash3388.flashlib.robot.hid.DPad;
import edu.flash3388.flashlib.robot.hid.POVButton;
import edu.flash3388.flashlib.robot.hid.Stick;
import edu.flash3388.flashlib.robot.hid.Triggers.Trigger;
import edu.flash3388.flashlib.robot.rio.RioButton;
import edu.flash3388.flashlib.robot.rio.RioDpad;
import edu.flash3388.flashlib.robot.rio.RioDpadButton;
import edu.flash3388.flashlib.robot.rio.RioStick;
import edu.flash3388.flashlib.robot.rio.RioTrigger;
import edu.flash3388.flashlib.robot.sbc.SbcBot;
import edu.flash3388.flashlib.robot.sbc.SbcButton;
import edu.flash3388.flashlib.robot.sbc.SbcDpad;
import edu.flash3388.flashlib.robot.sbc.SbcDpadButton;
import edu.flash3388.flashlib.robot.sbc.SbcStick;
import edu.flash3388.flashlib.robot.sbc.SbcTrigger;
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
		
		if(type.equals(ImplType.RIO)){
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
	}
	
	public static double getStickAxis(int stick, int axis){
		switch(type){
			case RIO: return DriverStation.getInstance().getStickAxis(stick, axis);
			case SBC: return SbcBot.getControlStation().getStickAxis(stick, axis);
			default: return 0;
		}
	}
	public static boolean getStickButton(int stick, byte button){
		switch(type){
			case RIO: return DriverStation.getInstance().getStickButton(stick, button);
			case SBC: return SbcBot.getControlStation().getStickButton(stick, button);
			default: return false;
		}
	}
	
	public static Stick createStick(int stick, int axisX, int axisY){
		switch(type){
			case RIO: return new RioStick(stick, axisX, axisY);
			case SBC: return new SbcStick(stick, axisX, axisY);
			default: return null;
		}
	}
	public static Button createButton(int stick, int button){
		switch(type){
			case RIO: return new RioButton(stick, button);
			case SBC: return new SbcButton(stick, button);
			default: return null;
		}
	}
	public static Button createButton(String name, int stick, int button){
		switch(type){
			case RIO: return new RioButton(name, stick, button);
			case SBC: return new SbcButton(name, stick, button);
			default: return null;
		}
	}
	public static DPad createDpad(int stick){
		switch(type){
			case RIO: return new RioDpad(stick);
			case SBC: return new SbcDpad(stick);
			default: return null;
		}
	}
	public static POVButton createDpadButton(int stick, POVButton.Type t){
		switch(type){
			case RIO: return new RioDpadButton(stick, t);
			case SBC: return new SbcDpadButton(stick, t);
			default: return null;
		}
	}
	public static Trigger createTrigger(int stick, int trig){
		switch(type){
			case RIO: return new RioTrigger(stick, trig);
			case SBC: return new SbcTrigger(stick, trig);
			default: return null;
		}
	}
}
