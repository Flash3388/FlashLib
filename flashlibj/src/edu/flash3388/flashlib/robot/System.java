package edu.flash3388.flashlib.robot;

public abstract class System{
	
	private Action default_action;
	private Action current_action;
	
	private String name = null;
	
	protected System(String name){
		Scheduler.getInstance().registerSystem(this);
		this.name = name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
	
	public void cancelCurrentAction(){
		if(hasCurrentAction() && getCurrentAction().isRunning())
			getCurrentAction().cancel();
	}
	public boolean hasCurrentAction(){
		return current_action != null;
	}
	public void setCurrentAction(Action action){
		current_action = action;
	}
	public Action getCurrentAction(){
		return current_action;
	}
	public void startDefaultAction(){
		if(default_action != null) 
			default_action.start();
	}
	protected void setDefaultAction(Action action){
		default_action = action;
	}
	
	
	protected abstract void initDefaultAction();

}
