package examples.robot.frc;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.util.beans.DoubleSource;

/*
 * In this example we will review how to create a custom Action for 
 * FlashLibs scheduling system.
 * 
 * As explained in the Wiki, an Action object represents an action
 * that is executed on a subsystem. This includes, but is not limited to:
 * Movement actions, operation actions, autonomous control, manual control, etc.
 * It mainly depends on the subsystem. 
 * 
 * In general, each Action performs an action which includes one or more
 * subsystems and in order to insure proper operation, users must report the used
 * subsystems when initializing an action. But it is even possible to not use any subsystem if wanted.
 * 
 * So FlashLib does not force system requirement reporting and it is entirely up to the user. It is important
 * to remember that if you use a subsystem in your action but do not report it the scheduling
 * system will not be able to appropriately operate.
 * 
 * We will simulate an action which operates the subsystem create in the ExampleCustomSubsystem example.
 * 
 * The action will receive an instance of the subsystem and a DoubleSource object. In execute the action
 * will call rotate and pass the DoubleSource value.
 */
public class ExampleAction extends Action{

	//the DoubleSource object. This will retreive speed values for the subsystem
	private DoubleSource source;
	//our subsystem object
	private ExampleSubsystem subsytem;
	
	//constructor: receives both the subsystem and speed source object
	//save the objects
	//declare system requirement
	public ExampleAction(ExampleSubsystem subsystem, DoubleSource source) {
		this.subsytem = subsystem;
		this.source = source;
		
		/*
		 * Declare system requirement. This is important so that the scheduling system can
		 * insure no 2 actions use the same system an cause problems. It is possible
		 * to declare multiple requirements.
		 */
		requires(subsystem);
	}
	
	/*
	 * Called once when the action is started. Allows for initialization 
	 * before operation. 
	 * In this case we have nothing to do. 
	 * This method has a default implementation which is empty, so it is not necessary 
	 * to implement it.
	 */
	@Override
	protected void initialize() {
	}
	/*
	 * The main action phase. Called periodically by the scheduler, so here we
	 * will perform the actual action. 
	 */
	@Override
	protected void execute() {
		//call the rotate action and pass it the value from our speed source.
		subsytem.rotate(source.get());
	}
	/*
	 * Called periodically while the action is running. If this method returns true, the action
	 * is stopped. Using this allows us to declare a stop condition to our action. 
	 * In this example we have no stop condition, we just run until someone stops us, so we return false.
	 * This method has a default implementation which returns false, so it is not necessary to
	 * implement it.
	 */
	@Override
	protected boolean isFinished() {
		return false;
	}
	/*
	 * Called when the action finished execution properly (isFinished returns true). Here
	 * we should stop our operations to insure safety.
	 */
	@Override
	protected void end() {
		//call stop to stop the subsystem.
		//this is a safety measure.
		subsytem.stop();
	}
	/*
	 * Called when the action execution is interrupted, causing it to stop. This can occur whenever the
	 * action is stopped and isFinished did not return true. Here we should stop our operation to insure
	 * safety. 
	 * In this example, we don't have something special to do when interrupted so we simply call
	 * end().
	 * This method has a default implementation which calls end(), so it is not necessary to implement it.
	 */
	@Override
	protected void interrupted() {
		end();
	}
}
