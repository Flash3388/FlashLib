package edu.flash3388.flashlib.dashboard;

import edu.flash3388.flashlib.dashboard.controls.*;
import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.communications.SendableCreator;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;

public class FlashboardSendableCreator implements SendableCreator{

	private Sendable get(String name, byte type) {
		switch(type){
			case FlashboardSendableType.ACTIVATABLE: return new ButtonControl(name);
			case FlashboardSendableType.BOOLEAN: return new BooleanProperty(name);
			case FlashboardSendableType.DOUBLE: return new DoubleProperty(name);
			case FlashboardSendableType.STRING: return new StringProperty(name);
			case FlashboardSendableType.INPUT: return new InputField(name);
			case FlashboardSendableType.SLIDER: return new Slider(name);
			case FlashboardSendableType.JOYSTICK: return Dashboard.getHIDControl();
			case FlashboardSendableType.CHOOSER: return new Chooser(name);
			case FlashboardSendableType.TESTER: return new FlashboardTester(name);
			case FlashboardSendableType.MOTOR: return new FlashboardTesterMotor(name);
			case FlashboardSendableType.LOG: return new LogWindow.RemoteLog(name);
			case FlashboardSendableType.VISION: return (Sendable) Dashboard.getVision();
			case FlashboardSendableType.PDP: return new PDP(name);
			case FlashboardSendableType.ESTOP: return Dashboard.getEmergencyStopControl();
			case FlashboardSendableType.PIDTUNER: return new DashboardPIDTuner(name);
			case FlashboardSendableType.MODE_SELECTOR: return Dashboard.getModeSelectorControl();
		}
		return null;
	}
	@Override
	public Sendable create(String name, byte type) {
		Sendable s = get(name, type);
		if(s != null && s instanceof Displayable)
			Dashboard.addDisplayable((Displayable)s);
		return s;
	}
}
