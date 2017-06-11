package edu.flash3388.flashlib.flashboard;

import java.util.HashMap;
import java.util.Iterator;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;

public class Tester extends Sendable{
	
	public static final byte START = 0xe;
	public static final byte STOP = 0x5;
	
	private HashMap<String, TesterMotor> motors = new HashMap<String, TesterMotor>();
	private boolean sending = false;
	
	public Tester(String name) {
		super(name, FlashboardSendableType.TESTER);
	}

	public TesterMotor addMotor(TesterMotor motor){
		motors.put(motor.getName(), motor);
		Flashboard.attach(motor);
		motor.setDataSending(sending);
		return motor;
	}
	public TesterMotor addMotor(String name, FlashSpeedController controller){
		return addMotor(new TesterMotor(name, controller, this));
	}
	public void addMotors(TesterMotor...motors){
		for (TesterMotor testerMotor : motors)
			addMotor(testerMotor);
	}
	
	public TesterMotor getMotor(String name){
		return motors.get(name);
	}
	
	private void startDataSending(){
		sending = true;
		for (Iterator<TesterMotor> iterator = motors.values().iterator(); iterator.hasNext();)
			iterator.next().setDataSending(true);
	}
	private void stopDataSending(){
		sending = false;
		for (Iterator<TesterMotor> iterator = motors.values().iterator(); iterator.hasNext();)
			iterator.next().setDataSending(false);
	}
	
	@Override
	public void newData(byte[] data) {
		if(data[0] == START){
			startDataSending();
		}else if(data[0] == STOP){
			stopDataSending();
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
		stopDataSending();
	}
}
