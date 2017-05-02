package edu.flash3388.flashlib.robot;

import java.util.Enumeration;

import edu.flash3388.flashlib.robot.devices.DoubleDataSource;
import edu.flash3388.flashlib.util.FlashUtil;

public class SelectableAction extends Action{

	private Action[] actions;
	private Action runningAction;
	private int nextActionIndex;
	private int selectedIndex = -1;
	private DoubleDataSource source;
	
	public SelectableAction(DoubleDataSource source, Action...actions){
		this.actions = new Action[actions.length];
		java.lang.System.arraycopy(actions, 0, this.actions, 0, actions.length);
		nextActionIndex = actions.length;
		this.source = source;
	}
	public SelectableAction(Action...actions){
		this(null, actions);
	}
	public SelectableAction(int capacity){
		this.actions = new Action[capacity];
		nextActionIndex = 0;
	}
	public SelectableAction(){
		this(5);
	}
	
	private void validateRequirements(Enumeration<System> systems){
		resetRequirements();
		for (; systems.hasMoreElements();) {
			System s = systems.nextElement();
			if(s != null)
				requires(s);
		}
	}
	private void checkRange(){
		if(nextActionIndex >= actions.length){
			Action[] newActions = new Action[actions.length + 5];
			java.lang.System.arraycopy(actions, 0, newActions, 0, actions.length);
			actions = newActions;
		}
	}
	private void shiftArray(int index){
		for (int i = index; i < nextActionIndex; i++) 
			actions[i] = actions[i+1];
	}
	
	public SelectableAction select(int index){
		if(index < 0 || index > nextActionIndex)
			throw new IndexOutOfBoundsException("Index out of bounds");
		selectedIndex = index;
		return this;
	}
	public SelectableAction setIndexSource(DoubleDataSource source){
		this.source = source;
		return this;
	}
	public SelectableAction addAction(Action action){
		setAction(action, nextActionIndex);
		nextActionIndex++;
		return this;
	}
	public SelectableAction setAction(Action action, int index){
		if(action == null)
			throw new NullPointerException("Cannot add a null object");
		checkRange();
		actions[index] = action;
		return this;
	}
	public SelectableAction removeAction(int index){
		if(index < 0 || index > nextActionIndex)
			throw new IndexOutOfBoundsException("Index out of bounds");
		actions[index] = null;
		shiftArray(index);
		nextActionIndex--;
		return this;
	}
	
	@Override
	public void start(){
		int index = source != null? (int) source.get() : selectedIndex;
		if(index < 0 || index >= nextActionIndex){
			FlashUtil.getLog().reportError("Invalid action index");
			return;
		}
		if(actions[index] == null){
			FlashUtil.getLog().reportError("Null action!!");
			return;
		}
		runningAction = actions[index];
		validateRequirements(runningAction.getRequirements());
		super.start();
	}
	@Override
	public void removed(){
		super.removed();
		runningAction = null;
	}
	
	protected void initialize(){ 
		runningAction.initialize();
	}
	protected void execute(){
		runningAction.execute();
	}
	protected boolean isFinished(){ 
		return runningAction.isFinished();
	}
	protected void end(){
		runningAction.end();
	}
	protected void interrupted(){ 
		runningAction.interrupted();
	}
}
