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

	private BooleanSource mCondition;
	private Action mActionRunOnTrue;
	private Action mActionRunOnFalse;

	private Action mActionRunning;

	public ConditionalAction(BooleanSource condition, Action runOnTrue, Action runOnFalse){
		mCondition = condition;
		mActionRunOnTrue = runOnTrue;
		mActionRunOnFalse = runOnFalse;
	}

	@Override
	protected void initialize() {
		mActionRunning = mCondition.get() ? mActionRunOnTrue : mActionRunOnFalse;
		mActionRunning.start();
	}

	@Override
	protected void execute() {

	}

	@Override
	protected boolean isFinished() {
		return !mActionRunning.isRunning();
	}

	@Override
	protected void end() {
		if (mActionRunning.isRunning()) {
			mActionRunning.cancel();
		}
	}
}
