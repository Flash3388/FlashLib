package edu.flash3388.flashlib.dashboard;

import edu.flash3388.flashlib.dashboard.controls.*;
import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.communications.SendableCreator;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.vision.ThreadedVisionRunner;
import edu.flash3388.flashlib.vision.VisionRunner;

public class FlashboardSendableCreator implements SendableCreator{

	private Sendable get(String name, byte type) {
		switch(type){
			case FlashboardSendableType.ACTIVATABLE: return new Button(name);
			case FlashboardSendableType.BOOLEAN: return new BooleanProperty(name);
			case FlashboardSendableType.DOUBLE: return new DoubleProperty(name);
			case FlashboardSendableType.STRING: return new StringProperty(name);
			case FlashboardSendableType.INPUT: return new InputField(name);
			case FlashboardSendableType.SLIDER: return new Slider(name);
			case FlashboardSendableType.JOYSTICK: return null;
			case FlashboardSendableType.CHOOSER: return new Chooser(name);
			case FlashboardSendableType.TESTER: return new FlashboardTester(name);
			case FlashboardSendableType.MOTOR: return new FlashboardTesterMotor(name);
			case FlashboardSendableType.LOG: return new LogWindow.RemoteLog(name);
			case FlashboardSendableType.VISION: return Dashboard.visionInitialized()? null : new ThreadedVisionRunner(name);
			case FlashboardSendableType.PDP: return new PDP(name);
			case FlashboardSendableType.ESTOP: return new EmergencyStopControl();
			case FlashboardSendableType.PIDTUNER: return new DashboardPidTuner(name);
		}
		return null;
	}
	@Override
	public Sendable create(String name, byte type) {
		Sendable s = get(name, type);
		if(s != null && s instanceof VisionRunner)
			Dashboard.setVision((VisionRunner)s);
		if(s != null && s instanceof Displayble)
			Dashboard.addDisplayable((Displayble)s);
		if(s != null && s instanceof EmergencyStopControl)
			Dashboard.setEmergencyStopControl((EmergencyStopControl)s);
		return s;
	}
}
