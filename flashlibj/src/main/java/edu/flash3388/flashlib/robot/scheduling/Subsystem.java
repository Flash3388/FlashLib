package edu.flash3388.flashlib.robot.scheduling;

/**
 * Subsystem is the base for robot systems. When defining a class for a system on a robot, extend this class. 
 * Doing so, allows operation of the system with FlashLib's scheduling system. 
 * <p>
 * A subsystem can be defined as a system on a robot which can be used separately from other parts of the robot. 
 * Examples for subsystems include but are not limited to: drive trains, arms, shooters, etc.
 * The concept of what makes a part of a robot into a subsystem depends on the way you wish
 * to organize you code, but in general remains the same.
 * <p>
 * Subsystems can allow only one {@link Action} object to use them at any given time. The {@link Scheduler} is
 * responsible for insuring that.
 * <p>
 * In addition, a subsystem can have a default {@link Action} object. This object will start running if no
 * other {@link Action} is running on the system. This {@link Scheduler} makes this possible by checking it
 * scheduling are running on the system and starting the default action if not. Default scheduling can be set
 * by calling {@link #setDefaultAction(Action)}.
 * <p>
 * Each subsystem should have only one instance in our robot code.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class Subsystem{
	
	private Action mDefaultAction;
	private Action mCurrentAction;
	
	/**
	 * Creates a new subsystem. The name is just for comfort of data logging if needed.
	 * To register this subsystem to the {@link Scheduler}, {@link Scheduler#registerSubsystem(Subsystem)} is
	 * called, passing it this object.
	 */
	protected Subsystem(){
		Scheduler.getInstance().registerSubsystem(this);
	}
	
	/**
	 * Cancels the current action running on this system, if there is one.
	 */
	public void cancelCurrentAction(){
		if(hasCurrentAction() && getCurrentAction().isRunning()) {
			getCurrentAction().cancel();
		}
	}

	/**
	 * Gets whether or not this system contains a current action.
	 * 
	 * @return true if there is a current action, false otherwise
	 */
	public boolean hasCurrentAction(){
		return mCurrentAction != null;
	}
	
	/**
	 * Sets the default action for this system
	 * 
	 * @param action the default action
	 */
	public void setDefaultAction(Action action){
		mDefaultAction = action;
	}

	void setCurrentAction(Action action){
		mCurrentAction = action;
	}

	Action getCurrentAction(){
		return mCurrentAction;
	}

	void startDefaultAction(){
		if(mDefaultAction != null) {
			mDefaultAction.start();
		}
	}
}
