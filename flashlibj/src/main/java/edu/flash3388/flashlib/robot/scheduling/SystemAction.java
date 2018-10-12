package edu.flash3388.flashlib.robot.scheduling;

/**
 * A wrapper for an action which adds a system requirement. When the system action is executed it will run the
 * methods of the wrapped action.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class SystemAction extends Action {

	private Action mAction;
	
	public SystemAction(Action action, Subsystem... subsystems){
		mAction = action;
		
		copyActionProperties(action);
		requires(subsystems);
	}
	
	@Override
	protected void initialize(){
		mAction.initialize();
	}
	
	@Override
	protected void execute() {
		mAction.execute();
	}

	@Override
	protected boolean isFinished(){
		return mAction.isFinished();
	}
	
	@Override
	protected void end() {
		mAction.end();
	}
	
	@Override
	protected void interrupted(){
		mAction.interrupted();
	}
}
