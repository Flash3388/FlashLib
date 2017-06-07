package edu.flash3388.flashlib.robot.devices;

import java.util.List;

import edu.flash3388.flashlib.math.Mathd;
import edu.flash3388.flashlib.robot.systems.ModableMotor;
import edu.flash3388.flashlib.util.FlashUtil;

public class MultiSpeedController implements FlashSpeedController, ModableMotor{

	private FlashSpeedController[] motor_controllers;
	private boolean brakeMode = false, inverted = false;
	
	public MultiSpeedController(FlashSpeedController...controllers) {
		motor_controllers = FlashUtil.copy(controllers);
		
		init();
	}
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
	
	public FlashSpeedController getController(int index){
		if(index < 0) throw new IllegalArgumentException("Index must be non-negative");
		else if(index >= motor_controllers.length) 
			throw new IndexOutOfBoundsException("Index out of bounds of list - " + motor_controllers.length);
		
		return motor_controllers[index];
	}
	public int getControllerCount(){
		return motor_controllers.length;
	}
	
	@Override
	public void set(double speed) {
		Mathd.limit(speed, -1, 1);
		for(FlashSpeedController c : motor_controllers)
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
		for(FlashSpeedController c : motor_controllers)
			c.stop();
	}

	@Override
	public double get() {
		double sp = 0;
		for(FlashSpeedController c : motor_controllers)
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
		for(FlashSpeedController c : motor_controllers)
			c.setInverted(inverted);
	}
	@Override
	public void enableBrakeMode(boolean mode) {
		this.brakeMode = mode;
		for(FlashSpeedController c : motor_controllers){
			if(c instanceof ModableMotor)
				((ModableMotor)c).enableBrakeMode(mode);
		}
	}
	@Override
	public boolean inBrakeMode() {
		return brakeMode;
	}
}
