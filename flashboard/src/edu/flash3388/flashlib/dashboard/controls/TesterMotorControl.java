package edu.flash3388.flashlib.dashboard.controls;

import edu.flash3388.flashlib.dashboard.Displayable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.flashboard.FlashboardTesterMotor;
import edu.flash3388.flashlib.util.FlashUtil;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class TesterMotorControl extends Displayable{

	private String testerName = null;
	
	private SimpleStringProperty nameprop;
	private SimpleDoubleProperty speedprop = new SimpleDoubleProperty(0.0);
	private SimpleDoubleProperty currentprop = new SimpleDoubleProperty(0.0);
	private SimpleDoubleProperty voltageprop = new SimpleDoubleProperty(0.0);
	private SimpleBooleanProperty brakeprop = new SimpleBooleanProperty(false);
	
	private boolean update = false;
	private boolean brakemode = false;
	private double speed = 0.0, current = 0.0, voltage = 0.0;
	
	public TesterMotorControl(String name) {
		super(name, FlashboardSendableType.MOTOR);
		FlashboardTester.allocateTesterMotor(this);
		nameprop = new SimpleStringProperty(name);
	}

	public SimpleStringProperty nameProperty(){
		return nameprop;
	}
	public SimpleDoubleProperty speedProperty(){
		return speedprop;
	}
	public SimpleDoubleProperty currentProperty(){
		return currentprop;
	}
	public SimpleDoubleProperty voltageProperty(){
		return voltageprop;
	}
	public SimpleBooleanProperty brakeModeProperty(){
		return brakeprop;
	}
	
	public String getTesterName(){
		return testerName;
	}
	
	@Override
	public void newData(byte[] data) {
		if(data[0] == FlashboardTesterMotor.UPDATE_TESTER_NAME){
			testerName = new String(data, 1, data.length - 1);
			FlashboardTester.allocateTesterMotor(this);
		}else{
			int pos = 8;
			speed = FlashUtil.toDouble(data);
			current = FlashUtil.toDouble(data, pos); pos += 8;
			voltage = FlashUtil.toDouble(data, pos); pos += 8;
			brakemode = data[pos] == 1;
			update = true;
		}
	}
	@Override
	public byte[] dataForTransmition() {
		return null;
	}
	@Override
	public boolean hasChanged() {
		return false;
	}
	@Override
	public void onConnection() {
	}
	@Override
	public void onConnectionLost() {
	}
	
	@Override
	public void update() {
		if(!update) return;
		
		if(speedprop.get() != speed)
			speedprop.set(speed);
		if(currentprop.get() != current)
			currentprop.set(current);
		if(voltageprop.get() != voltage)
			voltageprop.set(voltage);
		if(brakeprop.get() != brakemode)
			brakeprop.set(brakemode);
		update = false;
	}
}
