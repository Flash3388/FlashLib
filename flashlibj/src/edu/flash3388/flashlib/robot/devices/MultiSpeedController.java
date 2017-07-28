package edu.flash3388.flashlib.robot.devices;

import java.util.List;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.util.FlashUtil;

/**
 * A container for multiple speed controllers. Grouping controllers in such manner allows simultaneous 
 * control of several motors. When motors should be activated together, this container is extremly useful.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class MultiSpeedController implements FlashSpeedController, ModableMotor{

	private FlashSpeedController[] motor_controllers;
	private boolean brakeMode = false, inverted = false;
	
	/**
	 * Creates a new container for an array of speed controller.
	 * 
	 * @param controllers array of controllers to be contained
	 */
	public MultiSpeedController(FlashSpeedController...controllers) {
		motor_controllers = FlashUtil.copy(controllers);
		
		init();
	}
	/**
	 * Creates a new container for a list of speed controller.
	 * 
	 * @param controllers list of controllers to be contained
	 */
	public MultiSpeedController(List<FlashSpeedController> controllers){
		if(controllers == null) return;
		motor_controllers = new FlashSpeedController[controllers.size()];
		for (int i = 0; i < controllers.size(); i++)
			motor_controllers[i] = controllers.get(i);
		
		init();
	}
	
	private void init(){
		enableBrakeMode(false);
		setInverted(false);
		set(0);
	}
	
	/**
	 * Gets a controller held in this container by the index
	 * @param index the index of the controller
	 * @return a controller from the container
	 * @throws IllegalArgumentException if the index is negative
	 * @throws IndexOutOfBoundsException if the index exceeds the array size
	 */
	public FlashSpeedController getController(int index){
		if(index < 0) throw new IllegalArgumentException("Index must be non-negative");
		else if(index >= motor_controllers.length) 
			throw new IndexOutOfBoundsException("Index out of bounds of list - " + motor_controllers.length);
		
		return motor_controllers[index];
	}
	/**
	 * Gets the amount of controllers held in this container.
	 * @return the amount of controllers
	 */
	public int getControllerCount(){
		return motor_controllers.length;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets the speed value to all the speed controllers contained in this object.
	 * </p>
	 */
	@Override
	public void set(double speed) {
		speed = Mathf.constrain(speed, -1, 1);
		for (int i = 0; i < motor_controllers.length; i++)
			motor_controllers[i].set(speed);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets the speed value to all the speed controllers contained in this object.
	 * </p>
	 */
	@Override
	public void set(double speed, int direction) {
		set(direction >= 0? speed : -speed);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets the speed value to all the speed controllers contained in this object.
	 * </p>
	 */
	@Override
	public void set(double speed, boolean direction) {
		set(direction? speed : -speed);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Stops all the speed controllers contained in this object.
	 * </p>
	 */
	@Override
	public void stop() {
		for(int i = 0; i < motor_controllers.length; i++)
			motor_controllers[i].stop();
	}

	/**
	 * {@inheritDoc}
	 *<p>
	 * Gets the average speed value of all the speed controllers contained in this object.
	 *</p>
	 */
	@Override
	public double get() {
		double sp = 0;
		for(int i = 0; i < motor_controllers.length; i++)
			sp += motor_controllers[i].get();
		return sp / motor_controllers.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isInverted() {
		return inverted;
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets all the speed controllers contained in this object .
	 * </p>
	 */
	@Override
	public void setInverted(boolean inverted) {
		this.inverted = inverted;
		for(int i = 0; i < motor_controllers.length; i++)
			motor_controllers[i].setInverted(inverted);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets all the speed controllers contained in this object .
	 * </p>
	 */
	@Override
	public void enableBrakeMode(boolean mode) {
		this.brakeMode = mode;
		for(int i = 0; i < motor_controllers.length; i++){
			FlashSpeedController c = motor_controllers[i];
			if(c instanceof ModableMotor)
				((ModableMotor)c).enableBrakeMode(mode);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean inBrakeMode() {
		return brakeMode;
	}
}
