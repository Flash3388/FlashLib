package edu.flash3388.flashlib.robot.rio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ctre.CANTalon;

import edu.flash3388.flashlib.robot.Direction;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Victor;

public class RioControllers implements FlashSpeedController{

	public enum ControllerType {
		Talon, Jaguar, Victor, CANTalon, CANJaguar
	}
	
	private List<SpeedController> motor_controllers;
	private boolean brakeMode = false, inverted = false;
	
	public RioControllers(int c, ControllerType t){
		this(controllerFromType(t, c));
	}
	
	/**
	 * Creates an instance of the Controllers class. The instance is created for a single motor controller.
	 * 
	 * @param c An instance of the FlashSpeedController class representing the motor controller.
	 */
	public RioControllers(SpeedController c){
		this(Arrays.asList(c));
	}
	public RioControllers(int front, int back, ControllerType t){
		this(controllerFromType(t, front), controllerFromType(t, back));
	}
	
	/**
	 * Creates an instance of the Controllers class. The instance is created for two motor controllers.
	 * 
	 * @param front The PWM channel of the front motor controller.
	 * @param tf The ControllerType of the front motor controller.
	 * @param back The PWM channel of the rear motor controller.
	 * @param tb The ControllerType of the rear motor controller.
	 */
	public RioControllers(int front, ControllerType tf, int back, ControllerType tb){
		this(controllerFromType(tf, front), controllerFromType(tb, back));
	}
	
	/**
	 * Creates an instance of the Controllers class. The instance is created for two Talon type motor controllers.
	 * 
	 * @param front The PWM channel of the front motor controller.
	 * @param back The PWM channel of the rear motor controller.
	 */
	public RioControllers(int front, int back){
		this(controllerFromType(ControllerType.Talon, front), controllerFromType(ControllerType.Talon, back));
	}
	
	/**
	 * Creates an instance of the Controllers class. The instance is created for two motor controllers.
	 * 
	 * @param front An instance of the FlashSpeedController class representing the front motor controller.
	 * @param back An instance of the FlashSpeedController class representing the rear motor controller.
	 */
	public RioControllers(SpeedController front, SpeedController back){
		this(Arrays.asList(front, back));
	}
	
	/**
	 * Creates an instance of the Controllers class. The instance is created for three motor controllers.
	 * 
	 * @param front An instance of the FlashSpeedController class representing the front motor controller.
	 * @param middle An instance of the FlashSpeedController class representing the center motor controller.
	 * @param back An instance of the FlashSpeedController class representing the rear motor controller.
	 */
	public RioControllers(SpeedController front, SpeedController middle, SpeedController back){
		this(Arrays.asList(front, middle, back));
	}
	
	public RioControllers(SpeedController...controllers) {
		this(Arrays.asList(controllers));
	}
	
	public RioControllers(ControllerType t, int...controllers){
		motor_controllers = new ArrayList<SpeedController>(controllers.length);
		for (int i = 0; i < controllers.length; i++)
			motor_controllers.add(controllerFromType(t, controllers[i]));
		enableBrakeMode(false);
		setInverted(false);
	}
	/**
	 * Creates an instance of the Controllers class. The instance is created for an unlimited amount of
	 * motor controllers.
	 * 
	 * @param controllers An array of FlashSpeedController class instances representing all the motor controllers for
	 * 					for this side.
	 * @throws IllegalArgumentException if controllers is null
	 */
	public RioControllers(List<SpeedController> controllers){
		if(controllers == null) return;
		motor_controllers = controllers;
		enableBrakeMode(false);
		setInverted(false);
	}
	
	public SpeedController getController(int index){
		if(index < 0) throw new IllegalArgumentException("Index must be non-negative");
		else if(index >= motor_controllers.size()) 
			throw new IndexOutOfBoundsException("Index out of bounds of list - " + motor_controllers.size());
		
		return motor_controllers.get(index);
	}
	
	public int getControllerCount(){
		return motor_controllers.size();
	}
	
	public List<SpeedController> getCollection(){
		return motor_controllers;
	}
	
	@Override
	public void enableBrakeMode(boolean mode) {
		for(SpeedController c : motor_controllers){
			if(c instanceof CANTalon)
				((CANTalon)c).enableBrakeMode(mode);
		}
		brakeMode = mode;
	}
	@Override
	public boolean inBrakeMode() {
		return brakeMode;
	}

	@Override
	public void set(double speed) {
		if(speed == 0) set(0, 0);
		set(Math.abs(speed), (int) (speed/Math.abs(speed)));
	}
	@Override
	public void set(double speed, int direction) {
		if(speed < 0) speed = Math.abs(speed);
		speed = (speed > 1)? 1 : speed;
		if(direction != 1 && direction != -1 && direction != 0) 
			return;
		
		for(SpeedController c : motor_controllers)
			c.set(speed * direction);
	}
	@Override
	public void stop() {
		for(SpeedController c : motor_controllers)
			c.stopMotor();
	}

	@Override
	public double get() {
		double sp = 0;
		for(SpeedController c : motor_controllers)
			sp += c.get();
		return sp / motor_controllers.size();
	}

	@Override
	public boolean isInverted() {
		return inverted;
	}
	@Override
	public void setInverted(boolean inverted) {
		this.inverted = inverted;
		for(SpeedController c : motor_controllers)
			c.setInverted(inverted);
	}
	
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
