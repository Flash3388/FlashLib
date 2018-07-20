package edu.flash3388.flashlib.robot.scheduling;

import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.BooleanSource;

/**
 * Conditional action uses a BooleanDataSource to select which action to use when {@link Action#start()} is
 * called. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class ConditionalAction extends Action {

	private BooleanSource condition;
	private Action actionTrue, actionFalse, runAction;
	
	/**
	 * Creates a new conditional action.
	 * @param condition the condition source
	 * @param aTrue action to run when condition is true
	 * @param aFalse action to run when condition is false
	 */
	public ConditionalAction(BooleanSource condition, Action aTrue, Action aFalse){
		this.condition = condition;
		this.actionTrue = aTrue;
		this.actionFalse = aFalse;
	}
	
	private void validateRequirements(Action action){
		resetRequirements();
		copyRequirements(action);
	}
	
	public void setConditionSource(BooleanSource source){
		this.condition = source;
	}
	public void setActionOnTrue(Action action){
		actionTrue = action;
	}
	public void setActionOnFalse(Action action){
		actionFalse = action;
	}
	
	@Override
	public void start(){
		if(condition == null){
			FlashUtil.getLogger().severe("Missing condition source");
			return;
		}
		runAction = condition.get()? actionTrue : actionFalse;
		validateRequirements(runAction);
		super.start();
	}
	
	@Override
	protected void initialize(){ 
		runAction.initialize();
	}
	@Override
	protected void execute() {
		runAction.execute();
	}
	@Override
	protected boolean isFinished(){ 
		return runAction.isFinished();
	}
	@Override
	protected void end() {
		runAction.end();
	}
	@Override
	protected void interrupted(){ 
		runAction.interrupted();
	}
}
