package edu.flash3388.flashlib.robot.scheduling.actions;

import edu.flash3388.flashlib.robot.RobotResources;
import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Scheduler;
import edu.flash3388.flashlib.time.Clock;
import edu.flash3388.flashlib.time.Time;

import java.util.function.BooleanSupplier;

/**
 * Conditional action uses a BooleanDataSource to select which action to use when {@link Action#start()} is
 * called. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class ConditionalAction extends Action {

	private final BooleanSupplier mCondition;
	private final Action mActionRunOnTrue;
	private final Action mActionRunOnFalse;

	private Action mActionRunning;

	public ConditionalAction(BooleanSupplier condition, Action runOnTrue, Action runOnFalse){
		this(RobotResources.SCHEDULER.get(), RobotResources.CLOCK.get(), condition, runOnTrue, runOnFalse);
	}

    /* package */ ConditionalAction(Scheduler scheduler, Clock clock, BooleanSupplier condition, Action runOnTrue, Action runOnFalse){
	    super(scheduler, clock, Time.INVALID);

        mCondition = condition;
        mActionRunOnTrue = runOnTrue;
        mActionRunOnFalse = runOnFalse;
    }

	@Override
	protected final void initialize() {
		mActionRunning = mCondition.getAsBoolean() ? mActionRunOnTrue : mActionRunOnFalse;
		mActionRunning.start();
	}

	@Override
	protected void execute() {

	}

	@Override
	protected final boolean isFinished() {
		return !mActionRunning.isRunning();
	}

    @Override
    protected final void interrupted() {
        end();
    }

	@Override
	protected final void end() {
		if (mActionRunning.isRunning()) {
			mActionRunning.cancel();
		}
	}
}
