package edu.flash3388.flashlib.robot;

/**
 * A wrapper for an action which adds a system requirement. When the system action is executed it will run the
 * methods of the wrapped action.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class SystemAction extends Action{

	private Action action;
	
	public SystemAction(Action action, System... systems){
		this.action = action;
		
		setTimeOut(action.getTimeOut());
		requires(systems);
		copyRequirements(action);
	}
	
	@Override
	protected void initialize(){
		action.initialize();
	}
	
	@Override
	protected void execute() {
		action.execute();
	}

	@Override
	protected boolean isFinished(){
		return action.isFinished();
	}
	
	@Override
	protected void end() {
		action.end();
	}
	
	@Override
	protected void interrupted(){
		action.interrupted();
	}
}
