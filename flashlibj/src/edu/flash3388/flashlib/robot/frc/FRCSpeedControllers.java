package edu.flash3388.flashlib.robot.frc;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;
import edu.flash3388.flashlib.robot.devices.ModableMotor;
import edu.flash3388.flashlib.util.FlashUtil;

import edu.wpi.first.wpilibj.SpeedController;

import com.ctre.CANTalon;

/**
 * Wrapper for speed controllers in WPILib to use in FlashLib.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class FRCSpeedControllers implements FlashSpeedController, ModableMotor{
	
	private SpeedController[] motor_controllers;
	private boolean brakeMode = false, inverted = false;
	
	/**
	 * Creates a new wrapper for speed controllers.
	 * 
	 * @param controllers array of WPILib {@link SpeedController}
	 */
	public FRCSpeedControllers(SpeedController...controllers) {
		motor_controllers = FlashUtil.copy(controllers);
		enableBrakeMode(false);
		setInverted(false);
	}
	
	/**
	 * Gets a controller held in this container by the index
	 * @param index the index of the controller
	 * @return a controller from the container
	 * @throws IllegalArgumentException if the index is negative
	 * @throws IndexOutOfBoundsException if the index exceeds the array size
	 */
	public SpeedController getController(int index){
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
			motor_controllers[i].set(0);
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
			SpeedController c = motor_controllers[i];
			if(c instanceof CANTalon)
				((CANTalon)c).enableBrakeMode(mode);
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
