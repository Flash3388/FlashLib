package edu.flash3388.flashlib.robot;

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
