package edu.flash3388.flashlib.robot;

public class InstantRunnableAction extends RunnableAction{

	public InstantRunnableAction(Runnable runnable) {
		super(runnable);
	}

	@Override
	protected boolean isFinished() {
		return true;
	}
}
