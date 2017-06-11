package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.robot.devices.BooleanDataSource;
import edu.flash3388.flashlib.robot.devices.DoubleDataSource;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.robot.systems.ModableMotor;

public class TesterMotor extends Sendable{
	
	public static final byte UPDATE_TESTER_NAME = 0x5;
	
	private String testerName;
	private boolean sendData = false, updateTesterName = false;
	private FlashSpeedController controller;
	private DoubleDataSource voltSource, ampSource;
	private BooleanDataSource brakeModeSource;
	
	private double speed = 0.0, voltage = 0.0, current = 0.0;
	private boolean brakemode = false;
	
	public TesterMotor(String name, FlashSpeedController controller, Tester tester) {
		super(name, FlashboardSendableType.MOTOR);
		this.controller = controller;
		testerName = tester.getName();
		
		if(controller instanceof ModableMotor)
			brakeModeSource = ()->((ModableMotor)controller).inBrakeMode();
	}

	public TesterMotor setVoltageSource(DoubleDataSource source){
		voltSource = source;
		return this;
	}
	public TesterMotor setCurrentSource(DoubleDataSource source){
		ampSource = source;
		return this;
	}
	public TesterMotor setBrakeModeSource(BooleanDataSource source){
		brakeModeSource = source;
		return this;
	}
	
	void setDataSending(boolean send){
		sendData = send;
	}
	
	private boolean getBrakeMode(){
		return brakeModeSource != null? brakeModeSource.get() : brakemode;
	}
	private double getSpeed(){
		return controller.get();
	}
	private double getCurrent(){
		return ampSource != null? ampSource.get() : current;
	}
	private double getVoltage(){
		return voltSource != null? voltSource.get() : voltage;
	}
	
	@Override
	public void newData(byte[] data) {
	}
	@Override
	public byte[] dataForTransmition() {
		if(updateTesterName){
			 updateTesterName = false;
			 byte[] data = testerName.getBytes();
			 byte[] send = new byte[data.length+1];
			 send[0] = UPDATE_TESTER_NAME;
			 System.arraycopy(data, 0, send, 1, data.length);
			 return send;
		}
		
		speed = getSpeed();
		current = getCurrent();
		brakemode = getBrakeMode();
		voltage = getVoltage();
		
		byte[] data = new byte[25];
		int pos = 0;
		FlashUtil.fillByteArray(speed, data); pos += 8;
		FlashUtil.fillByteArray(current, pos, data); pos += 8;
		FlashUtil.fillByteArray(voltage, pos, data); pos += 8;
		data[pos] = (byte) (brakemode? 1 : 0);
		
		return data;
	}
	@Override
	public boolean hasChanged(){
		return updateTesterName || (sendData && (getBrakeMode() != brakemode || 
				getSpeed() != speed || getCurrent() != current || getVoltage() != voltage));
	}
	@Override
	public void onConnection() {
		updateTesterName = true;
	}
	@Override
	public void onConnectionLost() {
	}
}
