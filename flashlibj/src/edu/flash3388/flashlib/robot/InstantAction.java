package edu.flash3388.flashlib.robot;

public abstract class InstantAction extends Action{
	@Override
	protected void end() {}
	@Override
	protected boolean isFinished() {return true;}
}
