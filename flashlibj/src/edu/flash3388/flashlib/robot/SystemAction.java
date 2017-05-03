package edu.flash3388.flashlib.robot;

import java.util.Enumeration;

public class SystemAction extends Action{

	private Action action;
	
	public SystemAction(System system, Action action){
		this.action = action;
		
		setTimeOut(action.getTimeOut());
		
		requires(system);
		
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
