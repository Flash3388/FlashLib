package edu.flash3388.flashlib.flashboard;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.flash3388.flashlib.communications.SendableException;
import edu.flash3388.flashlib.robot.io.devices.actuators.FlashSpeedController;
import edu.flash3388.flashlib.robot.io.devices.actuators.ModableMotor;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.BooleanSource;
import edu.flash3388.flashlib.util.beans.DoubleSource;

/**
 * Represents a motor tracker on the Flashboard.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class FlashboardMotorTester extends FlashboardControl{
	
	/**
	 * Represents a tracking wrapper for motors on the Flashboard.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.0
	 */
	public static class TesterMotor extends FlashboardControl{
		
		public static final byte UPDATE_TESTER_NAME = 0x5;
		
		private String testerName;
		private boolean sendData = false, updateTesterName = false;
		private FlashSpeedController controller;
		private DoubleSource voltSource, ampSource;
		private BooleanSource brakeModeSource;
		
		private double speed = 0.0, voltage = 0.0, current = 0.0;
		private boolean brakemode = false;
		
		/**
		 * Creates a new tester wrapper for a motor. 
		 * 
		 * @param name name of the motor
		 * @param controller the motor controller
		 * @param tester the tester which will use it
		 */
		public TesterMotor(String name, FlashSpeedController controller, FlashboardMotorTester tester) {
			super(name, FlashboardSendableType.MOTOR);
			this.controller = controller;
			testerName = tester.getName();
			
			if(controller instanceof ModableMotor)
				brakeModeSource = ()->((ModableMotor)controller).inBrakeMode();
		}

		/**
		 * Sets the source for the motor voltage usage.
		 * @param source voltage source
		 * @return this instance
		 */
		public TesterMotor setVoltageSource(DoubleSource source){
			voltSource = source;
			return this;
		}
		/**
		 * Sets the source for the motor current usage.
		 * @param source current source
		 * @return this instance
		 */
		public TesterMotor setCurrentSource(DoubleSource source){
			ampSource = source;
			return this;
		}
		/**
		 * Sets the source for the motor brake mode.
		 * @param source brake mode source
		 * @return this instance
		 */
		public TesterMotor setBrakeModeSource(BooleanSource source){
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
		public void newData(byte[] data) throws SendableException {
		}
		@Override
		public byte[] dataForTransmission() throws SendableException {
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
	
	public static final byte START = 0xe;
	public static final byte STOP = 0x5;
	
	private Map<String, TesterMotor> motors = new HashMap<String, TesterMotor>();
	private boolean sending = false;
	
	/**
	 * Creates a new tester for motors. Uses the sendable type {@link FlashboardSendableType#TESTER}.
	 * @param name the name of the tester.
	 */
	public FlashboardMotorTester(String name) {
		super(name, FlashboardSendableType.TESTER);
	}

	/**
	 * Gets whether or not the motor tester is enabled. This is determined by the Flashboard: if the 
	 * tester motor window is open and this tester is selected, then it is enabled.
	 * @return true if the tester is enabled, false otherwise.
	 */
	public boolean isEnabled(){
		return sending;
	}
	
	/**
	 * Adds a new motor to the tester.
	 * 
	 * @param motor the wrapper for tester motor
	 * @return the motor object
	 * @see TesterMotor
	 */
	public TesterMotor addMotor(TesterMotor motor){
		motors.put(motor.getName(), motor);
		Flashboard.attach(motor);
		motor.setDataSending(sending);
		return motor;
	}
	/**
	 * Adds a new motor to the tester.
	 * 
	 * @param name name of the motor
	 * @param controller the motor controller
	 * @return the created motor object
	 * @see TesterMotor
	 */
	public TesterMotor addMotor(String name, FlashSpeedController controller){
		return addMotor(new TesterMotor(name, controller, this));
	}
	/**
	 * Adds new motors to the tester.
	 * 
	 * @param motors an array of tester motor wrappers
	 * @see TesterMotor
	 */
	public void addMotors(TesterMotor...motors){
		for (TesterMotor testerMotor : motors)
			addMotor(testerMotor);
	}
	
	/**
	 * Gets a wrapper for tester motor by name.
	 * 
	 * @param name the name of the wrapper
	 * @return the wrapper object, or null if it does not exist.
	 */
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
	public void newData(byte[] data) throws SendableException {
		if(data[0] == START){
			startDataSending();
		}else if(data[0] == STOP){
			stopDataSending();
		}
	}
	@Override
	public byte[] dataForTransmission() throws SendableException {
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
