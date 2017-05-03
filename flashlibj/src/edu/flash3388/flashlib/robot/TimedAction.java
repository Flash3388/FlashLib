package edu.flash3388.flashlib.robot;

import java.util.Enumeration;

public class TimedAction extends Action {

	private Action action;
	
	public TimedAction(Action action, double seconds){
		this(action, (long) (seconds * 1000));
	}
	public TimedAction(Action action, long milliseconds){
		this.action = action;
		
		setTimeOut(milliseconds);
		
		Enumeration<System> enumS = action.getRequirements();
		while(enumS.hasMoreElements()){
			requires(enumS.nextElement());
		}
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
