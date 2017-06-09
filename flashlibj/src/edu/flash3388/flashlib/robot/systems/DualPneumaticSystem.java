package edu.flash3388.flashlib.robot.systems;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.InstantAction;
import edu.flash3388.flashlib.robot.SystemAction;
import edu.flash3388.flashlib.robot.System;
import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 * A basic pneumactic System composed of two double-solenoids. Can represent systems like the 2015 robot arms.
 * 
 * @author Tom Tzook 
 */
public class DualPneumaticSystem extends System {

	public static enum State{
		Forward, Backward, Off
	}
	
	public final Action FORWARD_ACTION = new SystemAction(this, new InstantAction(){
		@Override
		protected void execute() {forward();}
	});
	public final Action BACKWARD_ACTION = new SystemAction(this, new InstantAction(){
		@Override
		public void execute() {backward();}
	});
	public final Action OFF_ACTION = new SystemAction(this, new InstantAction(){
		@Override
		public void execute() {off();}
	});
	public final Action SWITCH_ACTION = new SystemAction(this, new InstantAction(){
		@Override
		public void execute() {
			if(open)
				backward();
			else forward();
		}
	});
	
	private DoubleSolenoid solenoid_1, solenoid_2;
	private State state = State.Off;
	private boolean open = false;
	
	public DualPneumaticSystem(int forward_1, int backward_1, int forward_2, int backward_2){
		this(forward_1, forward_2, backward_1, backward_2, null);
	}
	public DualPneumaticSystem(int forward_1, int backward_1, int forward_2, int backward_2, Action defaultAction){
		super(null);
		solenoid_1 = new DoubleSolenoid(forward_1, backward_1);
		solenoid_2 = new DoubleSolenoid(forward_2, backward_2);
		
		backward();
		setDefaultAction(defaultAction);
	}
	
	public void setState(State state){
		switch(state){
			case Backward: backward();
				break;
			case Forward: forward();
				break;
			case Off: off();
				break;
		}
	}
	public void forward(){
		solenoid_1.set(DoubleSolenoid.Value.kForward);
		solenoid_2.set(DoubleSolenoid.Value.kForward);
		
		state = State.Forward;
		open = true;
	}
	public void backward(){
		solenoid_1.set(DoubleSolenoid.Value.kReverse);
		solenoid_2.set(DoubleSolenoid.Value.kReverse);
		
		state = State.Backward;
		open = false;
	}
	public void off(){
		solenoid_1.set(DoubleSolenoid.Value.kOff);
		solenoid_2.set(DoubleSolenoid.Value.kOff);
		
		state = State.Off;
	}
	public State getState(){
		return state;
	}

}
