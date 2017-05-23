package edu.flash3388.flashlib.robot.rio;

import java.util.List;

import com.ctre.CANTalon;

import edu.flash3388.flashlib.math.Mathd;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;
import edu.flash3388.flashlib.robot.systems.ModableMotor;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Victor;

public class RioControllers implements FlashSpeedController, ModableMotor{

	public static enum ControllerType {
		Talon, Jaguar, Victor, CANTalon, CANJaguar
	}
	
	private SpeedController[] motor_controllers;
	private boolean brakeMode = false, inverted = false;
	
	public RioControllers(int c, ControllerType t){
		this(controllerFromType(t, c));
	}
	public RioControllers(int front, int back, ControllerType t){
		this(controllerFromType(t, front), controllerFromType(t, back));
	}
	public RioControllers(int front, ControllerType tf, int back, ControllerType tb){
		this(controllerFromType(tf, front), controllerFromType(tb, back));
	}
	public RioControllers(int front, int back){
		this(controllerFromType(ControllerType.Talon, front), controllerFromType(ControllerType.Talon, back));
	}
	public RioControllers(SpeedController...controllers) {
		motor_controllers = new SpeedController[controllers.length];
		for (int i = 0; i < controllers.length; i++)
			motor_controllers[i] = controllers[i];
	}
	public RioControllers(ControllerType t, int...controllers){
		motor_controllers = new SpeedController[controllers.length];
		for (int i = 0; i < controllers.length; i++)
			motor_controllers[i] = controllerFromType(t, controllers[i]);
		enableBrakeMode(false);
		setInverted(false);
	}
	public RioControllers(List<SpeedController> controllers){
		if(controllers == null) return;
		motor_controllers = new SpeedController[controllers.size()];
		for (int i = 0; i < controllers.size(); i++)
			motor_controllers[i] = controllers.get(i);
		enableBrakeMode(false);
		setInverted(false);
	}
	
	public SpeedController getController(int index){
		if(index < 0) throw new IllegalArgumentException("Index must be non-negative");
		else if(index >= motor_controllers.length) 
			throw new IndexOutOfBoundsException("Index out of bounds of list - " + motor_controllers.length);
		
		return motor_controllers[index];
	}
	
	public int getControllerCount(){
		return motor_controllers.length;
	}
	
	public SpeedController[] getControllers(){
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
		Mathd.limit(speed, -1, 1);
		for(SpeedController c : motor_controllers)
			c.set(speed);
	}
	@Override
	public void set(double speed, int direction) {
		set(direction >= 0? speed : -speed);
	}
	@Override
	public void set(double speed, boolean direction) {
		set(direction? speed : -speed);
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
		return sp / motor_controllers.length;
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
	public static RioControllers create(ControllerType t, int channel){
		return new RioControllers(t, channel);
	}
}
