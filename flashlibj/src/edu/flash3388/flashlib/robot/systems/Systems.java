package edu.flash3388.flashlib.robot.systems;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.InstantAction;

/**
 * Systems provides assistance methods for creation of system-related objects and instances wih ease.
 * This is just a convenience class, and not necessary for use.
 * 
 * @author Tom Tzook'
 * @since FlashLib 1.0.1
 */
public final class Systems {
	private Systems(){}

	/**
	 * Creates an {@link Action} which calls {@link YAxisMovable#forward(double)} during the execute() phase of the action
	 * and {@link YAxisMovable#stop()} during the end() phase of the action. This action contains a system
	 * requirement of the instance of {@link YAxisMovable} used by calling {@link YAxisMovable#getSystem()}.
	 * 
	 * @param system the system
	 * @param speed the movement speed
	 * @return the action
	 */
	public static Action forwardAction(YAxisMovable system, double speed){
		return new Action(){
			{requires(system.getSystem());}
			@Override
			public void execute() {system.forward(speed);}
			@Override
			public void end() { system.stop();}
		};
	}
	/**
	 * Creates an {@link Action} which calls {@link YAxisMovable#backward(double)} during the execute() phase of the action
	 * and {@link YAxisMovable#stop()} during the end() phase of the action. This action contains a system
	 * requirement of the instance of {@link YAxisMovable} used by calling {@link YAxisMovable#getSystem()}.
	 * 
	 * @param system the system
	 * @param speed the movement speed
	 * @return the action
	 */
	public static Action backwardAction(YAxisMovable system, double speed){
		return new Action(){
			{requires(system.getSystem());}
			@Override
			public void execute() {system.backward(speed);}
			@Override
			public void end() { system.stop();}
		};
	}
	/**
	 * Creates an {@link Action} which calls {@link XAxisMovable#right(double)} during the execute() phase of the action
	 * and {@link XAxisMovable#stop()} during the end() phase of the action. This action contains a system
	 * requirement of the instance of {@link XAxisMovable} used by calling {@link XAxisMovable#getSystem()}.	 
	 * 
	 * @param system the system
	 * @param speed the movement speed
	 * @return the action
	 */
	public static Action rightAction(XAxisMovable system, double speed){
		return new Action(){
			{requires(system.getSystem());}
			@Override
			public void execute() {system.right(speed);}
			@Override
			public void end() { system.stop();}
		};
	}
	/**
	 * Creates an {@link Action} which calls {@link XAxisMovable#left(double)} during the execute() phase of the action
	 * and {@link XAxisMovable#stop()} during the end() phase of the action. This action contains a system
	 * requirement of the instance of {@link XAxisMovable} used by calling {@link XAxisMovable#getSystem()}.	 
	 * 
	 * @param system the system
	 * @param speed the movement speed
	 * @return the action
	 */
	public static Action leftAction(XAxisMovable system, double speed){
		return new Action(){
			{requires(system.getSystem());}
			@Override
			public void execute() {system.left(speed);}
			@Override
			public void end() { system.stop();}
		};
	}
	/**
	 * Creates an {@link Action} which calls {@link Rotatable#rotateRight(double)} during the execute() phase of the action
	 * and {@link Rotatable#stop()} during the end() phase of the action. This action contains a system
	 * requirement of the instance of {@link Rotatable} used by calling {@link Rotatable#getSystem()}.	 
	 * 
	 * @param system the system
	 * @param speed the movement speed
	 * @return the action
	 */
	public static Action rotateRightAction(Rotatable system, double speed){
		return new Action(){
			{requires(system.getSystem());}
			@Override
			public void execute() {system.rotateRight(speed);}
			@Override
			public void end() { system.stop();}
		};
	}
	/**
	 * Creates an {@link Action} which calls {@link Rotatable#rotateLeft(double)} during the execute() phase of the action
	 * and {@link Rotatable#stop()} during the end() phase of the action. This action contains a system
	 * requirement of the instance of {@link Rotatable} used by calling {@link Rotatable#getSystem()}.	 
	 * 
	 * @param system the system
	 * @param speed the movement speed
	 * @return the action
	 */
	public static Action rotateLeftAction(Rotatable system, double speed){
		return new Action(){
			{requires(system.getSystem());}
			@Override
			public void execute() {system.rotateLeft(speed);}
			@Override
			public void end() { system.stop();}
		};
	}
	
	

	/**
	 * Creates an {@link InstantAction} which calls {@link YAxisMovable#stop()} during the execute() phase of the action. 
	 * This action contains a system requirement of the instance of {@link YAxisMovable} used by calling 
	 * {@link YAxisMovable#getSystem()}.	 
	 * 
	 * @param system the system
	 * @return the action
	 */
	public static Action stopAction(YAxisMovable system){
		return new InstantAction(){
			{requires(system.getSystem());}
			@Override
			public void execute() {system.stop();}
		};
	}
	/**
	 * Creates an {@link InstantAction} which calls {@link XAxisMovable#stop()} during the execute() phase of the action. 
	 * This action contains a system requirement of the instance of {@link XAxisMovable} used by calling 
	 * {@link XAxisMovable#getSystem()}.	 
	 * 
	 * @param system the system
	 * @return the action
	 */
	public static Action stopAction(XAxisMovable system){
		return new InstantAction(){
			{requires(system.getSystem());}
			@Override
			public void execute() {system.stop();}
		};
	}
	/**
	 * Creates an {@link InstantAction} which calls {@link Rotatable#stop()} during the execute() phase of the action. 
	 * This action contains a system requirement of the instance of {@link Rotatable} used by calling 
	 * {@link Rotatable#getSystem()}.	 
	 * 
	 * @param system the system
	 * @return the action
	 */
	public static Action stopAction(Rotatable system){
		return new InstantAction(){
			{requires(system.getSystem());}
			@Override
			public void execute() {system.stop();}
		};
	}
}
