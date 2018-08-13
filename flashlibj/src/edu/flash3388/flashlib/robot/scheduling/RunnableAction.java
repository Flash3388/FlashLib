package edu.flash3388.flashlib.robot.scheduling;

/**
 * An action which runs a given {@link Runnable} object during {@link Action#execute()}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class RunnableAction extends Action{

	private Runnable mRunnable;
	
	public RunnableAction(Runnable runnable){
		mRunnable = runnable;
	}
	
	@Override
	protected void execute() {
		mRunnable.run();
	}

	@Override
	protected void end() {}
}
