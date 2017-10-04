package edu.flash3388.flashlib.dashboard;

import edu.flash3388.flashlib.dashboard.controls.*;
import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.communications.SendableCreator;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;

public class FlashboardSendableCreator implements SendableCreator{

	private Sendable get(String name, byte type) {
		switch(type){
			case FlashboardSendableType.DIR_INDICATOR: return new DirectionControl(name);
			case FlashboardSendableType.BOOL_INDICATOR: return new BooleanIndicatorControl(name);
			case FlashboardSendableType.CHECKBOX: return new CheckBoxControl(name);
			case FlashboardSendableType.ACTIVATABLE: return new ButtonControl(name);
			case FlashboardSendableType.LABEL: return new LabelControl(name);
			case FlashboardSendableType.INPUT: return new InputFieldControl(name);
			case FlashboardSendableType.SLIDER: return new SliderControl(name);
			case FlashboardSendableType.JOYSTICK: return Dashboard.getHIDControl();
			case FlashboardSendableType.CHOOSER: return new ChooserControl(name);
			case FlashboardSendableType.TESTER: return new FlashboardTester(name);
			case FlashboardSendableType.MOTOR: return new TesterMotorControl(name);
			case FlashboardSendableType.LOG: return new LogWindow.RemoteLog(name);
			case FlashboardSendableType.VISION: return (Sendable) Dashboard.getVision();
			case FlashboardSendableType.PDP: return new PDPControl(name);
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
