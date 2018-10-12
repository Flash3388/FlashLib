package edu.flash3388.flashlib.robot.scheduling;

/**
 * A wrapper for an action which adds a timeout. When the timed action is executed it will run the
 * methods of the wrapped action.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class TimedAction extends Action {

	private Action mAction;
	
	public TimedAction(Action action, double timeoutSeconds){
		this(action, (int) (timeoutSeconds * 1000));
	}

	public TimedAction(Action action, int timeoutMs){
		mAction = action;
		
		copyActionProperties(action);
		setTimeoutMs(timeoutMs);
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
