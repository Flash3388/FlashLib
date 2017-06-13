package edu.flash3388.flashlib.robot;

public abstract class InterruptionAction extends Action{

	private Action ittrpAction;
	
	protected void interrupt(Action action){
		if(!isRunning()) return;
		
		if(ittrpAction != null)
			ittrpAction.interrupted();
		ittrpAction = action;
		ittrpAction.initialize();
	}
	
	@Override
	protected void initialize() {
		ittrpAction = null;
		stdInitialize();
	}
	@Override
	protected void execute() {
		if(ittrpAction != null){
			ittrpAction.execute();
			if(ittrpAction.isFinished()){
				ittrpAction.end();
				ittrpAction = null;
			}
		}else stdExecute();
	}
	@Override
	protected boolean isFinished() {
		return stdIsFinished();
	}
	@Override
	protected void end() {
		if(ittrpAction != null)
			ittrpAction.end();
		stdEnd();
	}

	
	protected abstract void stdInitialize();
	protected abstract void stdExecute();
	protected abstract boolean stdIsFinished();
	protected abstract void stdEnd();
}
