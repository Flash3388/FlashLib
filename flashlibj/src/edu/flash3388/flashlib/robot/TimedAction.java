package edu.flash3388.flashlib.robot;

/**
 * A wrapper for an action which adds a timeout. When the timed action is executed it will run the
 * methods of the wrapped action.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class TimedAction extends Action {

	private Action action;
	
	public TimedAction(Action action, double seconds){
		this(action, (int) (seconds * 1000));
	}
	public TimedAction(Action action, int milliseconds){
		this.action = action;
		
		setTimeout(milliseconds);
		
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
