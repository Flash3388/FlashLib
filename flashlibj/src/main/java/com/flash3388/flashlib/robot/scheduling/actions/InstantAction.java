package com.flash3388.flashlib.robot.scheduling.actions;

/**
 * An action which runs {@link Action#execute()} once.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class InstantAction extends Action {

    @SuppressWarnings("EmptyMethod")
    @Override
    protected final void initialize() {
    }

    @Override
	protected final boolean isFinished() {
		return true;
	}

    @Override
    protected final void interrupted() {
    }

    @Override
    protected final void end() {
    }
}
