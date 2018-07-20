package edu.flash3388.flashlib.robot.scheduling;

/**
 * An action which runs {@link Action#execute()} once.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class InstantAction extends Action{
	@Override
	protected void end() {}
	@Override
	protected boolean isFinished() {return true;}
}
