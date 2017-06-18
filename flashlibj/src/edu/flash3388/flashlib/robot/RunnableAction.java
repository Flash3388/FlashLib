package edu.flash3388.flashlib.robot;

/**
 * An action which runs a given {@link Runnable} object during {@link Action#execute()}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class RunnableAction extends Action{

	private Runnable runnable;
	
	public RunnableAction(Runnable runnable){
		this.runnable = runnable;
	}
	
	@Override
	protected void execute() {
		runnable.run();
	}

	@Override
	protected void end() {}
}
