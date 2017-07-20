package edu.flash3388.flashlib.flashboard;

import java.util.HashMap;
import java.util.Iterator;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;

/**
 * Represents a motor tracker on the Flashboard.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class Tester extends Sendable{
	
	public static final byte START = 0xe;
	public static final byte STOP = 0x5;
	
	private HashMap<String, TesterMotor> motors = new HashMap<String, TesterMotor>();
	private boolean sending = false;
	
	/**
	 * Creates a new tester for motors. Uses the sendable type {@link FlashboardSendableType#TESTER}.
	 * @param name the name of the tester.
	 */
	public Tester(String name) {
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
