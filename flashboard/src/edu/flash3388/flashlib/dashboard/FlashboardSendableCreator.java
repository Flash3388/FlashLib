package edu.flash3388.flashlib.dashboard;

import edu.flash3388.flashlib.dashboard.controls.*;
import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.communications.SendableCreator;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.vision.CvRunner;

public class FlashboardSendableCreator implements SendableCreator{

	private Sendable get(String name, int id, byte type) {
		switch(type){
			case FlashboardSendableType.ACTIVATABLE: return new Button(name, id);
			case FlashboardSendableType.BOOLEAN: return new BooleanProperty(name, id);
			case FlashboardSendableType.DOUBLE: return new DoubleProperty(name, id);
			case FlashboardSendableType.STRING: return new StringProperty(name, id);
			case FlashboardSendableType.INPUT: return new InputField(name, id);
			case FlashboardSendableType.JOYSTICK: return new Controller(name, id);
			case FlashboardSendableType.CHOOSER: return new Chooser(name, id);
			case FlashboardSendableType.TESTER: Tester.init(id); return Tester.getInstance();
			case FlashboardSendableType.MOTOR: if(Tester.getInstance() != null) return Tester.getInstance().addMotor(id);
							else return null;
			case FlashboardSendableType.LOG: return new LogWindow.RemoteLog(name, id);
			case FlashboardSendableType.VISION: return Dashboard.visionInitialized()? null : new CvRunner(name, id);
			case FlashboardSendableType.PDP: return new PDP(name, id);
			case FlashboardSendableType.ESTOP: return new EmergencyStopControl(id);
		}
		return null;
	}
	@Override
	public Sendable create(String name, int id, byte type) {
		Sendable s = get(name, id, type);
		if(s != null && s instanceof CvRunner)
			Dashboard.setVision((CvRunner)s);
		if(s != null && s instanceof Displayble)
			Dashboard.addDisplayable((Displayble)s);
		if(s != null && s instanceof EmergencyStopControl)
			Dashboard.setEmergencyStopControl((EmergencyStopControl)s);
		return s;
	}
}
