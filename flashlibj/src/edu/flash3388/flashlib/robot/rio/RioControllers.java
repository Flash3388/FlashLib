package edu.flash3388.flashlib.robot.rio;

import java.util.List;

import com.ctre.CANTalon;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;
import edu.flash3388.flashlib.robot.devices.ModableMotor;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Victor;

/**
 * Wrapper for speed controllers in WPILib to use in FlashLib.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class RioControllers implements FlashSpeedController, ModableMotor{

	/**
	 * Enumeration for types of speed controllers available.
	 * @author Tom Tzook
	 * @since FlashLib 1.0.0
	 */
	public static enum ControllerType {
		Talon, Jaguar, Victor, CANTalon, CANJaguar
	}
	
	private SpeedController[] motor_controllers;
	private boolean brakeMode = false, inverted = false;
	
	/**
	 * Creates a new wrapper for a speed controller of a given type.
	 * 
	 * @param c the port 
	 * @param t the type
	 */
	public RioControllers(int c, ControllerType t){
		this(controllerFromType(t, c));
	}
	/**
	 * Creates a new wrapper for 2 speed controllers of the same type.
	 * 
	 * @param front the first port 
	 * @param back the second port
	 * @param t the type
	 */
	public RioControllers(int front, int back, ControllerType t){
		this(controllerFromType(t, front), controllerFromType(t, back));
	}
	/**
	 * Creates a new wrapper for 2 speed controllers of given types.
	 * @param front first port
	 * @param tf first type
	 * @param back second port
	 * @param tb second type
	 */
	public RioControllers(int front, ControllerType tf, int back, ControllerType tb){
		this(controllerFromType(tf, front), controllerFromType(tb, back));
	}
	/**
	 * Creates a new wrapper for 2 speed controllers of type {@link ControllerType#Talon}.
	 * 
	 * @param front the first port 
	 * @param back the second port
	 */
	public RioControllers(int front, int back){
		this(controllerFromType(ControllerType.Talon, front), controllerFromType(ControllerType.Talon, back));
	}
	/**
	 * Creates a new wrapper for speed controllers.
	 * 
	 * @param controllers array of WPILib {@link SpeedController}
	 */
	public RioControllers(SpeedController...controllers) {
		motor_controllers = new SpeedController[controllers.length];
		for (int i = 0; i < controllers.length; i++)
			motor_controllers[i] = controllers[i];
		enableBrakeMode(false);
		setInverted(false);
	}
	/**
	 * Creates a new wrapper for speed controllers of the same type.
	 * 
	 * @param t the type of controllers
	 * @param controllers array of ports.
	 */
	public RioControllers(ControllerType t, int...controllers){
		motor_controllers = new SpeedController[controllers.length];
		for (int i = 0; i < controllers.length; i++)
			motor_controllers[i] = controllerFromType(t, controllers[i]);
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
		speed = Mathf.limit(speed, -1, 1);
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
	
	/**
	 * Creates a WPILib speed controller by type and port.
	 * 
	 * @param t the type
	 * @param channel the port
	 * @return a new speed controller
	 */
	public static SpeedController controllerFromType(ControllerType t, int channel){
		switch(t){
			case CANTalon:
				return new CANTalon(channel);
			case Jaguar:
				return new Jaguar(channel);
			case Talon:
				return new Talon(channel);
			case Victor:
				return new Victor(channel);
			default:
				return null;
		}
	}
}
