package edu.flash3388.flashlib.robot;

/**
 * Represents a system on the robot. Each system should extend this class in order to work optimally with the
 * Scheduler. A system has a default action which runs when no other action runs on the system.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class Subsystem{
	
	private Action default_action;
	private Action current_action;
	
	private String name = null;
	
	protected Subsystem(String name){
		RobotFactory.getImplementation().scheduler().registerSystem(this);
		this.name = name;
	}
	
	/**
	 * Sets the name of the system
	 * @param name name
	 */
	public void setName(String name){
		this.name = name;
	}
	/**
	 * Gets the name of the system
	 * @return the name of the system
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Cancels the current action running on this system, if there is one.
	 */
	public void cancelCurrentAction(){
		if(hasCurrentAction() && getCurrentAction().isRunning())
			getCurrentAction().cancel();
	}
	/**
	 * Gets whether or not this system contains a current action.
	 * @return true if there is a current action, false otherwise
	 */
	public boolean hasCurrentAction(){
		return current_action != null;
	}
	
	void setCurrentAction(Action action){
		current_action = action;
	}
	Action getCurrentAction(){
		return current_action;
	}
	void startDefaultAction(){
		if(default_action != null) 
			default_action.start();
	}
	
	/**
	 * Sets the default action for this system
	 * @param action the default action
	 */
	public void setDefaultAction(Action action){
		default_action = action;
	}
}
