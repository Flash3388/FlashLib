package edu.flash3388.flashlib.robot;

/**
 * An action which runs {@link Action#execute()} once. Runs a given {@link Runnable} object when 
 * {@link Action#execute()} is called.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class InstantRunnableAction extends RunnableAction{

	public InstantRunnableAction(Runnable runnable) {
		super(runnable);
	}

	@Override
	protected boolean isFinished() {
		return true;
	}
}
