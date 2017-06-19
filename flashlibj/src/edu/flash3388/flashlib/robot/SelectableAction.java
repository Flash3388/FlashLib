package edu.flash3388.flashlib.robot;

import java.util.Enumeration;

import edu.flash3388.flashlib.robot.devices.DoubleDataSource;
import edu.flash3388.flashlib.util.FlashUtil;

/**
 * An action which executes a selected action when started. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class SelectableAction extends Action{

	private Action[] actions;
	private Action runningAction;
	private int nextActionIndex;
	private int selectedIndex = -1;
	private DoubleDataSource source;
	
	/**
	 * Creates a new selectable action. 
	 * 
	 * @param source the index source
	 * @param actions an array of actions to select from
	 */
	public SelectableAction(DoubleDataSource source, Action...actions){
		this.actions = new Action[actions.length];
		java.lang.System.arraycopy(actions, 0, this.actions, 0, actions.length);
		nextActionIndex = actions.length;
		this.source = source;
	}
	/**
	 * Creates a new selectable action without an index selector. 
	 * 
	 * @param actions an array of actions to select from
	 */
	public SelectableAction(Action...actions){
		this(null, actions);
	}
	/**
	 * Creates a new empty selectable action with a given capacity. 
	 * @param capacity the capacity of the actions array
	 */
	public SelectableAction(int capacity){
		this.actions = new Action[capacity];
		nextActionIndex = 0;
	}
	/**
	 * Creates a new empty selectable action with a specific capacity. 
	 */
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
	
	/**
	 * Selects the action to be used when this action is started.
	 * 
	 * @param index action index
	 * @return this instance
	 * @throws IndexOutOfBoundsException if the index is out of the array bounds
	 */
	public SelectableAction select(int index){
		if(index < 0 || index > nextActionIndex)
			throw new IndexOutOfBoundsException("Index out of bounds");
		selectedIndex = index;
		return this;
	}
	/**
	 * Sets the action index source.
	 * 
	 * @param source the source of index
	 * @return this instance
	 */
	public SelectableAction setIndexSource(DoubleDataSource source){
		this.source = source;
		return this;
	}
	/**
	 * Adds a new action to the action array.
	 * 
	 * @param action action to be added
	 * @return this instance
	 */
	public SelectableAction addAction(Action action){
		setAction(action, nextActionIndex);
		nextActionIndex++;
		return this;
	}
	/**
	 * Sets the action at the given array index.
	 * 
	 * @param action the action to set
	 * @param index the index in the array
	 * @return this instance
	 */
	public SelectableAction setAction(Action action, int index){
		if(action == null)
			throw new NullPointerException("Cannot add a null object");
		checkRange();
		actions[index] = action;
		return this;
	}
	/**
	 * Removes the action at the given array index.
	 * 
	 * @param index the index in the array
	 * @return this instance
	 * @throws IndexOutOfBoundsException if the index is out of the array bounds
	 */
	public SelectableAction removeAction(int index){
		if(index < 0 || index > nextActionIndex)
			throw new IndexOutOfBoundsException("Index out of bounds");
		actions[index] = null;
		shiftArray(index);
		nextActionIndex--;
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Uses the source selector to select the index to use. If the source is null, than the manually selected index
	 * is used.
	 * </p>
	 */
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
	
	@Override
	protected void initialize(){ 
		runningAction.initialize();
	}
	@Override
	protected void execute(){
		runningAction.execute();
	}
	@Override
	protected boolean isFinished(){ 
		return runningAction.isFinished();
	}
	@Override
	protected void end(){
		runningAction.end();
	}
	@Override
	protected void interrupted(){ 
		runningAction.interrupted();
	}
}
